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

public class LayerCommandsTest extends BaseTest {

    @Test
    public void listLayers() throws Exception {
        String url = "/geoserver/rest/layers.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("layers.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerCommands commands = new LayerCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list();
        String expected = "lines" + OsUtils.LINE_SEPARATOR +
                "points" + OsUtils.LINE_SEPARATOR +
                "polygons" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getLayer() throws Exception {
        String url = "/geoserver/rest/layers/streams.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("layer.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerCommands commands = new LayerCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.get("streams");
        String expected = "streams" + OsUtils.LINE_SEPARATOR +
                "   Title: null" + OsUtils.LINE_SEPARATOR +
                "   Type: VECTOR" + OsUtils.LINE_SEPARATOR +
                "   Abstract: null" + OsUtils.LINE_SEPARATOR +
                "   Default Style: simple_streams" + OsUtils.LINE_SEPARATOR +
                "   Namespace:    Type String: VECTOR" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void modifyLayer() throws Exception {
        String url = "/geoserver/rest/layers/streams.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerCommands commands = new LayerCommands();
        commands.setGeoserver(geoserver);
        String name = "streams";
        String title = "Streams";
        String abstractStr = null;
        String defaultStyle = null;
        String enabled = null;
        boolean result = commands.modify(name, title, abstractStr, defaultStyle, enabled);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<layer><name>streams</name><title>Streams</title></layer>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void deleteLayer() throws Exception {
        String url = "/geoserver/rest/layers/streams.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerCommands commands = new LayerCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.delete("streams",true);
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void listLayerStyles() throws Exception {
        String url = "/geoserver/rest/layers/streams/styles.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("layerStyles.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerCommands commands = new LayerCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listStyles("streams");
        String expected = "highway" + OsUtils.LINE_SEPARATOR +
                "interstate" + OsUtils.LINE_SEPARATOR +
                "road" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void addLayerStyles() throws Exception {
        String url = "/geoserver/rest/layers/streams/styles.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        LayerCommands commands = new LayerCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.addStyle("streams", "blueline");
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<style><name>blueline</name></style>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }
}
