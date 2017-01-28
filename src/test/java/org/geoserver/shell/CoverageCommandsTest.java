package org.geoserver.shell;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import org.springframework.shell.support.util.OsUtils;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CoverageCommandsTest extends BaseTest {

    @Test
    public void listCoverages() throws Exception {
        String url = "/geoserver/rest/workspaces/nurc/coveragestores/mosaic/coverages.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("coverages.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageCommands commands = new CoverageCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list("nurc", "mosaic");
        String expected = "mosaic" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void listAllCoverages() throws Exception {
        // Workspace
        String workspaceUrl = "/geoserver/rest/workspaces.xml";
        whenHttp(server).match(get(workspaceUrl)).then(stringContent(getResourceString("workspaces.xml")), status(HttpStatus.OK_200));
        String[] workspaces = {"it.geosolutions", "topp", "cite"};
        String[] coverageStores = {"arcGridSample", "mosaic"};
        for (String workspace : workspaces) {
            String coverageStoreUrl = "/geoserver/rest/workspaces/" + workspace + "/coveragestores.xml";
            whenHttp(server).match(get(coverageStoreUrl)).then(stringContent(getResourceString("coveragestores.xml")), status(HttpStatus.OK_200));
            for (String coverageStore : coverageStores) {
                String coveragesUrl = "/geoserver/rest/workspaces/" + workspace + "/coveragestores/" + coverageStore + "/coverages.xml";
                whenHttp(server).match(get(coveragesUrl)).then(stringContent(getResourceString("coverages.xml")), status(HttpStatus.OK_200));
            }
        }
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageCommands commands = new CoverageCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list(null, null);
        String expected = "cite" + OsUtils.LINE_SEPARATOR +
                "----" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "   arcGridSample" + OsUtils.LINE_SEPARATOR +
                "   -------------" + OsUtils.LINE_SEPARATOR +
                "   mosaic" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "   mosaic" + OsUtils.LINE_SEPARATOR +
                "   ------" + OsUtils.LINE_SEPARATOR +
                "   mosaic" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "it.geosolutions" + OsUtils.LINE_SEPARATOR +
                "---------------" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "   arcGridSample" + OsUtils.LINE_SEPARATOR +
                "   -------------" + OsUtils.LINE_SEPARATOR +
                "   mosaic" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "   mosaic" + OsUtils.LINE_SEPARATOR +
                "   ------" + OsUtils.LINE_SEPARATOR +
                "   mosaic" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "topp" + OsUtils.LINE_SEPARATOR +
                "----" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "   arcGridSample" + OsUtils.LINE_SEPARATOR +
                "   -------------" + OsUtils.LINE_SEPARATOR +
                "   mosaic" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "   mosaic" + OsUtils.LINE_SEPARATOR +
                "   ------" + OsUtils.LINE_SEPARATOR +
                "   mosaic" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(workspaceUrl));
        for (String workspace : workspaces) {
            String coverageStoreUrl = "/geoserver/rest/workspaces/" + workspace + "/coveragestores.xml";
            verifyHttp(server).once(method(Method.GET), uri(coverageStoreUrl));
            for (String coverageStore : coverageStores) {
                String coveragesUrl = "/geoserver/rest/workspaces/" + workspace + "/coveragestores/" + coverageStore + "/coverages.xml";
                verifyHttp(server).once(method(Method.GET), uri(coveragesUrl));
            }
        }
    }

    @Test
    public void getCoverage() throws Exception {
        String url = "/geoserver/rest/workspaces/nurc/coveragestores/mosaic/coverages/mosaic.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("coverage.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageCommands commands = new CoverageCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.get("nurc", "mosaic", "mosaic");
        String expected = "mosaic" + OsUtils.LINE_SEPARATOR +
                "   Namespace: nurc" + OsUtils.LINE_SEPARATOR +
                "   Title: mosaic" + OsUtils.LINE_SEPARATOR +
                "   Abstract: null" + OsUtils.LINE_SEPARATOR +
                "   Native Name: mosaic" + OsUtils.LINE_SEPARATOR +
                "   Native Format: ImageMosaic" + OsUtils.LINE_SEPARATOR +
                "   Store Name: mosaic" + OsUtils.LINE_SEPARATOR +
                "   BBox: 6.346,36.492,20.83,46.591" + OsUtils.LINE_SEPARATOR +
                "   SRS: EPSG:4326" + OsUtils.LINE_SEPARATOR +
                "   Native CRS: GEOGCS[\"WGS 84\"," + OsUtils.LINE_SEPARATOR +
                "\t\tDATUM[\"World Geodetic System 1984\"," + OsUtils.LINE_SEPARATOR +
                "\t\tSPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]]," + OsUtils.LINE_SEPARATOR +
                "\t\tAUTHORITY[\"EPSG\",\"6326\"]]," + OsUtils.LINE_SEPARATOR +
                "\t\tPRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]]," + OsUtils.LINE_SEPARATOR +
                "\t\tUNIT[\"degree\", 0.017453292519943295]," + OsUtils.LINE_SEPARATOR +
                "\t\tAXIS[\"Geodetic longitude\", EAST]," + OsUtils.LINE_SEPARATOR +
                "\t\tAXIS[\"Geodetic latitude\", NORTH]," + OsUtils.LINE_SEPARATOR +
                "\t\tAUTHORITY[\"EPSG\",\"4326\"]]" + OsUtils.LINE_SEPARATOR +
                "\t" + OsUtils.LINE_SEPARATOR +
                "   Dimension Info: " + OsUtils.LINE_SEPARATOR +
                "   Metadata List: " + OsUtils.LINE_SEPARATOR +
                "      dirName: mosaic_mosaic" + OsUtils.LINE_SEPARATOR +
                "   Attribute List: " + OsUtils.LINE_SEPARATOR;
        assertStringsEquals(expected, actual, true);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void createCoverage() throws Exception {
        String url = "/geoserver/rest/workspaces/nurc/coveragestores/mosaic/coverages.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageCommands commands = new CoverageCommands();
        commands.setGeoserver(geoserver);
        String workspace = "nurc";
        String coveragestore = "mosaic";
        String coverage = "new";
        String title = null;
        String description = null;
        String keywords = null;
        String srs = null;
        boolean enabled = true;
        boolean result = commands.create(workspace, coveragestore, coverage, title, description, keywords, srs, enabled);
        assertTrue(result);
        String expected = "<coverage><metadata /><keywords /><metadataLinks /><supportedFormats /><name>new</name><enabled>true</enabled></coverage>";
        String actual = server.getCalls().get(0).getPostBody();
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void modifyCoverage() throws Exception {
        String url = "/geoserver/rest/workspaces/nurc/coveragestores/mosaic/coverages/new.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageCommands commands = new CoverageCommands();
        commands.setGeoserver(geoserver);
        String workspace = "nurc";
        String coveragestore = "mosaic";
        String coverage = "new";
        String title = "New Title";
        String description = "New Description";
        String keywords = null;
        String srs = null;
        String enabled = null;
        boolean result = commands.modify(workspace, coveragestore, coverage, title, description, keywords, srs, enabled);
        assertTrue(result);
        String expected = "<coverage><enabled>true</enabled><metadata /><keywords /><metadataLinks /><supportedFormats /><name>new</name><title>New Title</title><description>New Title</description></coverage>";
        String actual = server.getCalls().get(0).getPostBody();
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void deleteCoverage() throws Exception {
        String url = "/geoserver/rest/workspaces/nurc/coveragestores/mosaic/coverages/new.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        CoverageCommands commands = new CoverageCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.delete("nurc", "mosaic", "new", true);
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }
}
