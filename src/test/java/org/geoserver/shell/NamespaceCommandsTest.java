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
import static com.xebialabs.restito.semantics.Condition.post;
import static com.xebialabs.restito.semantics.Condition.put;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class NamespaceCommandsTest extends BaseTest {

    @Test
    public void listNamespaces() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/namespaces.xml")).then(stringContent(getResourceString("namespaces.xml")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        NamespaceCommands commands = new NamespaceCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list();
        String expected = "cite" + OsUtils.LINE_SEPARATOR + "it.geosolutions" + OsUtils.LINE_SEPARATOR + "nurc" +
                OsUtils.LINE_SEPARATOR + "sde" + OsUtils.LINE_SEPARATOR + "sf" + OsUtils.LINE_SEPARATOR + "tiger" +
                OsUtils.LINE_SEPARATOR + "topp" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/namespaces.xml"));
    }

    @Test
    public void getNamespace() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/namespaces/topp.xml")).then(stringContent(getResourceString("namespace.xml")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        NamespaceCommands commands = new NamespaceCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.get("topp");
        String expected = "topp" + OsUtils.LINE_SEPARATOR + "http://www.openplans.org/topp";
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/namespaces/topp.xml"));
    }

    @Test
    public void deleteNamespace() throws Exception {
        whenHttp(server).match(delete("/geoserver/rest/workspaces/topp")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        NamespaceCommands commands = new NamespaceCommands();
        commands.setGeoserver(geoserver);
        boolean actual = commands.delete("topp", true);
        assertTrue(actual);

        verifyHttp(server).once(method(Method.DELETE), uri("/geoserver/rest/workspaces/topp"));
    }

    @Test
    public void createNamespace() throws Exception {
        whenHttp(server).match(post("/geoserver/rest/namespaces")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        NamespaceCommands commands = new NamespaceCommands();
        commands.setGeoserver(geoserver);
        boolean actual = commands.create("topp","http://topp.org");
        assertTrue(actual);
        String expectedXml = "<namespace><prefix>topp</prefix><uri>http://topp.org</uri></namespace>";
        String actualXml = server.getCalls().get(0).getPostBody();
        assertEquals(expectedXml, actualXml);

        verifyHttp(server).once(method(Method.POST), uri("/geoserver/rest/namespaces"));
    }

    @Test
    public void getDefaultNamespace() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/namespaces/default.xml")).then(stringContent("<namespace><prefix>cite</prefix><uri>http://cite.org</uri></namespace>"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        NamespaceCommands commands = new NamespaceCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getDefault();
        String expected = "cite" + OsUtils.LINE_SEPARATOR + "http://cite.org";
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/namespaces/default.xml"));
    }

    @Test
    public void setDefaultNamespace() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/namespaces/default.xml")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        NamespaceCommands commands = new NamespaceCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.setDefault("topp");
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<namespace><prefix>topp</prefix></namespace>";
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/namespaces/default.xml"));
    }
    
}
