package org.geoserver.shell;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import org.springframework.shell.support.util.OsUtils;

import java.io.File;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class StyleCommandsTest extends BaseTest {

    @Test
    public void listStyles() throws Exception {
        String url = "/geoserver/rest/styles.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("styles.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list(null);
        String expected = "burg" + OsUtils.LINE_SEPARATOR +
                "capitals" + OsUtils.LINE_SEPARATOR +
                "cite_lakes" + OsUtils.LINE_SEPARATOR +
                "dem" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void listStylesWithWorkspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/styles.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("styles.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list("topp");
        String expected = "burg" + OsUtils.LINE_SEPARATOR +
                "capitals" + OsUtils.LINE_SEPARATOR +
                "cite_lakes" + OsUtils.LINE_SEPARATOR +
                "dem" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getStyle() throws Exception {
        String url = "/geoserver/rest/styles/polygon.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("style.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getStyle("polygon", null);
        String expected = "polygon" + OsUtils.LINE_SEPARATOR +
                "   SLD Version: 1.0.0" + OsUtils.LINE_SEPARATOR +
                "   File Name: default_polygon.sld" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getStyleWithWorkspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/styles/polygon.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("style.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getStyle("polygon", "topp");
        String expected = "polygon" + OsUtils.LINE_SEPARATOR +
                "   SLD Version: 1.0.0" + OsUtils.LINE_SEPARATOR +
                "   File Name: default_polygon.sld" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getSld() throws Exception {
        String url = "/geoserver/rest/styles/polygon.sld";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("polygon.sld")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getSld("polygon", null, null, false);
        String expected = getResourceString("polygon.sld");
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getSldWithWorkspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/styles/polygon.sld";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("polygon.sld")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        File file = File.createTempFile("polygon", ".sld");
        String actual = commands.getSld("polygon", "topp", file, false);
        String expected = file.getAbsolutePath();
        assertEquals(expected, actual);
        String expectedSld = getResourceString("polygon.sld");
        String actualSld = FileUtils.readFileToString(file);
        assertEquals(expectedSld, actualSld);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void deleteStyle() throws Exception {
        String url = "/geoserver/rest/styles/polygon";
        whenHttp(server).match(delete(url)).then(status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.delete("polygon", null, true);
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void deleteStyleWithWorkspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/styles/polygon";
        whenHttp(server).match(delete(url)).then(status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.delete("polygon", "topp", true);
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void createStyle() throws Exception {
        String url = "/geoserver/rest/styles";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        File file = getResourceFile("polygon.sld");
        boolean result = commands.create("polygon", null, file);
        assertTrue(result);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void createStyleWithWorkspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/styles";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        File file = getResourceFile("polygon.sld");
        boolean result = commands.create("polygon", "topp", file);
        assertTrue(result);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void modifyStyle() throws Exception {
        String url = "/geoserver/rest/styles/polygon";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        File file = getResourceFile("polygon.sld");
        boolean result = commands.modify("polygon", null, file);
        assertTrue(result);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void modifyStyleWithWorkspace() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/styles/polygon";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        StyleCommands commands = new StyleCommands();
        commands.setGeoserver(geoserver);
        File file = getResourceFile("polygon.sld");
        boolean result = commands.modify("polygon", "topp", file);
        assertTrue(result);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }
}
