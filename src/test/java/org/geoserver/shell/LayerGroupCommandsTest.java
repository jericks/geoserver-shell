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

public class LayerGroupCommandsTest extends BaseTest {

    @Test
    public void listLayerGroups() throws Exception {
        String url = "/geoserver/rest/layergroups.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("layergroups.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerGroupCommands commands = new LayerGroupCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list(null);
        String expected = "basemap" + OsUtils.LINE_SEPARATOR +
                "census" + OsUtils.LINE_SEPARATOR +
                "spearfish" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void listLayerGroupsWithWorkspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/layergroups.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("layergroups.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerGroupCommands commands = new LayerGroupCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list("topp");
        String expected = "basemap" + OsUtils.LINE_SEPARATOR +
                "census" + OsUtils.LINE_SEPARATOR +
                "spearfish" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getLayerGroup() throws Exception {
        String url = "/geoserver/rest/layergroups/spearfish.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("layergroup.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerGroupCommands commands = new LayerGroupCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.get("spearfish", null);
        String expected = "spearfish" + OsUtils.LINE_SEPARATOR +
                "   Title: null" + OsUtils.LINE_SEPARATOR +
                "   Abstract: null" + OsUtils.LINE_SEPARATOR +
                "   Workspace: null" + OsUtils.LINE_SEPARATOR +
                "   Mode: SINGLE" + OsUtils.LINE_SEPARATOR +
                "   CRS: EPSG:26713" + OsUtils.LINE_SEPARATOR +
                "   Bounds: 589425.9342365642, 4913959.224611808, 609518.6719560538, 4928082.949945881" + OsUtils.LINE_SEPARATOR +
                "   Layers: " + OsUtils.LINE_SEPARATOR +
                "      None" + OsUtils.LINE_SEPARATOR +
                "   Publishables: " + OsUtils.LINE_SEPARATOR +
                "      sfdem" + OsUtils.LINE_SEPARATOR +
                "      streams" + OsUtils.LINE_SEPARATOR +
                "      roads" + OsUtils.LINE_SEPARATOR +
                "      restricted" + OsUtils.LINE_SEPARATOR +
                "      archsites" + OsUtils.LINE_SEPARATOR +
                "      bugsites" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getLayerGroupWithWorkspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/layergroups/spearfish.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("layergroup.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerGroupCommands commands = new LayerGroupCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.get("spearfish", "topp");
        String expected = "spearfish" + OsUtils.LINE_SEPARATOR +
                "   Title: null" + OsUtils.LINE_SEPARATOR +
                "   Abstract: null" + OsUtils.LINE_SEPARATOR +
                "   Workspace: null" + OsUtils.LINE_SEPARATOR +
                "   Mode: SINGLE" + OsUtils.LINE_SEPARATOR +
                "   CRS: EPSG:26713" + OsUtils.LINE_SEPARATOR +
                "   Bounds: 589425.9342365642, 4913959.224611808, 609518.6719560538, 4928082.949945881" + OsUtils.LINE_SEPARATOR +
                "   Layers: " + OsUtils.LINE_SEPARATOR +
                "      None" + OsUtils.LINE_SEPARATOR +
                "   Publishables: " + OsUtils.LINE_SEPARATOR +
                "      sfdem" + OsUtils.LINE_SEPARATOR +
                "      streams" + OsUtils.LINE_SEPARATOR +
                "      roads" + OsUtils.LINE_SEPARATOR +
                "      restricted" + OsUtils.LINE_SEPARATOR +
                "      archsites" + OsUtils.LINE_SEPARATOR +
                "      bugsites" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void deleteLayerGroup() throws Exception {
        String url = "/geoserver/rest/layergroups/spearfish.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerGroupCommands commands = new LayerGroupCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.delete("spearfish", null);
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void deleteLayerGroupWithWorkspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/layergroups/spearfish.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerGroupCommands commands = new LayerGroupCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.delete("spearfish", "topp");
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void createLayerGroup() throws Exception {
        String url = "/geoserver/rest/layergroups.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerGroupCommands commands = new LayerGroupCommands();
        commands.setGeoserver(geoserver);
        String name = "census";
        String layers = "tracts,block groups,blocks";
        String styles = "";
        String workspace = null;
        boolean result = commands.create(name, layers, styles, workspace);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<layerGroup><name>census</name><layers><layer>tracts</layer><layer>block groups</layer>" +
                "<layer>blocks</layer></layers><styles></styles></layerGroup>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void createLayerGroupWithWorspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/layergroups.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerGroupCommands commands = new LayerGroupCommands();
        commands.setGeoserver(geoserver);
        String name = "census";
        String layers = "tracts,block groups,blocks";
        String styles = "tracts,block groups, blocks";
        String workspace = "topp";
        boolean result = commands.create(name, layers, styles, workspace);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<layerGroup><name>census</name><layers><layer>tracts</layer><layer>block groups</layer>" +
                "<layer>blocks</layer></layers><styles><style>tracts</style><style>block groups</style>" +
                "<style>blocks</style></styles></layerGroup>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void modifyLayerGroup() throws Exception {
        String url = "/geoserver/rest/layergroups/census.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerGroupCommands commands = new LayerGroupCommands();
        commands.setGeoserver(geoserver);
        String name = "census";
        String layers = "tracts,block groups,blocks";
        String styles = "tracts,block groups,blocks";
        String workspace = null;
        boolean result = commands.modify(name, layers, styles, workspace);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<layerGroup><name>census</name><layers><layer>tracts</layer><layer>block groups</layer>" +
                "<layer>blocks</layer></layers><styles><style>tracts</style><style>block groups</style>" +
                "<style>blocks</style></styles></layerGroup>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void modifyLayerGroupWithWorkspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/layergroups/census.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerGroupCommands commands = new LayerGroupCommands();
        commands.setGeoserver(geoserver);
        String name = "census";
        String layers = "tracts,block groups,blocks";
        String styles = "tracts,block groups,blocks";
        String workspace = "topp";
        boolean result = commands.modify(name, layers, styles, workspace);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<layerGroup><name>census</name><layers><layer>tracts</layer><layer>block groups</layer>" +
                "<layer>blocks</layer></layers><styles><style>tracts</style><style>block groups</style>" +
                "<style>blocks</style></styles></layerGroup>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }
}
