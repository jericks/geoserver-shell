package org.geoserver.shell;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import org.springframework.shell.support.util.OsUtils;

import java.io.File;
import java.util.Map;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CoverageStoreCommandsTest extends BaseTest {

    @Test
    public void listCoverageStores() throws Exception {
        String url = "/geoserver/rest/workspaces/nurc/coveragestores.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("coveragestores.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageStoreCommands commands = new CoverageStoreCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list("nurc");
        String expected = "arcGridSample" + OsUtils.LINE_SEPARATOR + "mosaic" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void listAllCoverageStores() throws Exception {
        String url1 = "/geoserver/rest/workspaces/it.geosolutions/coveragestores.xml";
        whenHttp(server).match(get(url1)).then(stringContent(getResourceString("coveragestores.xml")), status(HttpStatus.OK_200));
        String url2 = "/geoserver/rest/workspaces/topp/coveragestores.xml";
        whenHttp(server).match(get(url2)).then(stringContent(getResourceString("coveragestores.xml")), status(HttpStatus.OK_200));
        String url3 = "/geoserver/rest/workspaces/cite/coveragestores.xml";
        whenHttp(server).match(get(url3)).then(stringContent(getResourceString("coveragestores.xml")), status(HttpStatus.OK_200));
        String workspaceUrl = "/geoserver/rest/workspaces.xml";
        whenHttp(server).match(get(workspaceUrl)).then(stringContent(getResourceString("workspaces.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageStoreCommands commands = new CoverageStoreCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list(null);
        String expected = "cite" + OsUtils.LINE_SEPARATOR +
                "----" + OsUtils.LINE_SEPARATOR +
                "arcGridSample" + OsUtils.LINE_SEPARATOR +
                "mosaic" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "it.geosolutions" + OsUtils.LINE_SEPARATOR +
                "---------------" + OsUtils.LINE_SEPARATOR +
                "arcGridSample" + OsUtils.LINE_SEPARATOR +
                "mosaic" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "topp" + OsUtils.LINE_SEPARATOR +
                "----" + OsUtils.LINE_SEPARATOR +
                "arcGridSample" + OsUtils.LINE_SEPARATOR +
                "mosaic" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url1));
        verifyHttp(server).once(method(Method.GET), uri(url2));
        verifyHttp(server).once(method(Method.GET), uri(url3));
        verifyHttp(server).once(method(Method.GET), uri(workspaceUrl));
    }

    @Test
    public void getCoverageStore() throws Exception {
        String url = "/geoserver/rest/workspaces/nurc/coveragestores/arcGridSample.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("coveragestore.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageStoreCommands commands = new CoverageStoreCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.get("nurc", "arcGridSample");
        String expected = "arcGridSample" + OsUtils.LINE_SEPARATOR +
                "   Type: ArcGrid" + OsUtils.LINE_SEPARATOR +
                "   URL: file:coverages/arc_sample/precip30min.asc" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void deleteCoverageStore() throws Exception {
        String url = "/geoserver/rest/workspaces/nurc/coveragestores/arcGridSample.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageStoreCommands commands = new CoverageStoreCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.delete("nurc", "arcGridSample", true);
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void createCoverageStore() throws Exception {
        String url = "/geoserver/rest/workspaces/nurc/coveragestores.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageStoreCommands commands = new CoverageStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "nurc";
        String name = "terrain";
        String type = "geotiff";
        String fileUrl = "file:/terrain.tif";
        boolean enabled = true;
        boolean result = commands.create(workspace, name, type, fileUrl, enabled);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<coverageStore><name>terrain</name><type>geotiff</type><url>file:/terrain.tif</url><enabled>true</enabled><workspace><name>nurc</name></workspace></coverageStore>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void modifyCoverageStore() throws Exception {
        String url = "/geoserver/rest/workspaces/nurc/coveragestores/terrain.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageStoreCommands commands = new CoverageStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "nurc";
        String coverageStore = "terrain";
        String name = null;
        String type = null;
        String fileUrl = null;
        String enabled = "false";
        boolean result = commands.modify(workspace, coverageStore, name, type, fileUrl, enabled);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<coverageStore><enabled>false</enabled><workspace><name>nurc</name></workspace></coverageStore>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void uploadCoverageStore() throws Exception {
        String workspace = "nurc";
        String coverageStore = "terrain";
        File file = getResourceFile("coveragestore.xml");
        String type = "geotiff";
        String configure = "first";
        String coverage = "myterrain";
        String recalculate = "nativebbox";
        String url = "/geoserver/rest/workspaces/nurc/coveragestores/terrain/file.geotiff";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageStoreCommands commands = new CoverageStoreCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.upload(workspace, coverageStore, file, type, configure, coverage, recalculate);
        assertTrue(result);
        String body = server.getCalls().get(0).getPostBody();
        String contentType = server.getCalls().get(0).getContentType();
        assertEquals("image/tiff", contentType);
        Map<String, String[]> params = server.getCalls().get(0).getParameters();
        assertEquals(configure, params.get("configure")[0]);
        assertEquals(recalculate, params.get("recalculate")[0]);
        assertEquals(coverage, params.get("coverageName")[0]);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }
}
