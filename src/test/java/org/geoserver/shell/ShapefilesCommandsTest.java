package org.geoserver.shell;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ShapefilesCommandsTest extends BaseTest {

    @Test
    public void publishShapefile() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/points/file.shp";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        whenHttp(server).match(post("/geoserver/rest/workspaces/topp/datastores/points/featuretypes.xml")).then(stringContent("true"), status(HttpStatus.OK_200));
        whenHttp(server).match(put("/geoserver/rest/layers/topp:points")).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ShapefileCommands commands = new ShapefileCommands();
        commands.setGeoserver(geoserver);
        File file = getResourceFile("points.zip");
        boolean result = commands.publish("topp", "points", "points", file, "EPSG:4326", null);
        assertTrue(result);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void zipShapefile() throws Exception {
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ShapefileCommands commands = new ShapefileCommands();
        commands.setGeoserver(geoserver);
        File file = getResourceFile("points.shp");
        File zipFile = File.createTempFile("points", ".zip");
        boolean result = commands.zip(file, zipFile);
        assertTrue(result);
        List<String> names = getFileNamesFromZip(zipFile);
        assertEquals(5, names.size());
        assertTrue(names.contains("points.shp"));
        assertTrue(names.contains("points.dbf"));
        assertTrue(names.contains("points.prj"));
        assertTrue(names.contains("points.shx"));
        assertTrue(names.contains("points.fix"));
    }
}
