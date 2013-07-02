package org.geoserver.shell;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class WorldImageCommandsTest extends BaseTest {

    @Test
    public void zipWorldImae() throws Exception {
        WorldImageCommands commands = new WorldImageCommands();
        File file = getResourceFile("raster.jpg");
        File zipFile = File.createTempFile("raster", ".zip");
        boolean result = commands.zip(file, zipFile);
        assertTrue(result);
        List<String> names = getFileNamesFromZip(zipFile);
        assertEquals(3, names.size());
        assertTrue(names.contains("raster.jpg"));
        assertTrue(names.contains("raster.jgw"));
        assertTrue(names.contains("raster.prj"));
    }

    @Test
    public void uploadWorldImage() throws Exception {
        String workspace = "nurc";
        String coverageStore = "terrain";
        File file = getResourceFile("coverageStore.xml");
        String coverage = "myterrain";
        String url = "/geoserver/rest/workspaces/nurc/coveragestores/terrain/file.worldimage";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WorldImageCommands commands = new WorldImageCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.publish(workspace, coverageStore, file, coverage);
        assertTrue(result);
        String body = server.getCalls().get(0).getPostBody();
        String contentType = server.getCalls().get(0).getContentType();
        assertEquals("application/zip", contentType);
        Map<String, String[]> params = server.getCalls().get(0).getParameters();
        assertEquals("first", params.get("configure")[0]);
        assertEquals(coverage, params.get("coverageName")[0]);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }
}
