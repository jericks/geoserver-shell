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

public class WorkspaceCommandsTest extends BaseTest {

    @Test
    public void listWorkspaces() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/workspaces.xml")).then(stringContent(getResource("workspaces.xml")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WorkspaceCommands commands = new WorkspaceCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list();
        String expected = "cite" + OsUtils.LINE_SEPARATOR + "it.geosolutions" + OsUtils.LINE_SEPARATOR + "topp" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/workspaces.xml"));
    }

    @Test
    public void getWorkspace() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/workspaces/topp.xml")).then(stringContent(getResource("workspace.xml")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WorkspaceCommands commands = new WorkspaceCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.get("topp");
        String expected = "topp";
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/workspaces/topp.xml"));
    }

    @Test
    public void deleteWorkspace() throws Exception {
        whenHttp(server).match(delete("/geoserver/rest/workspaces/topp")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WorkspaceCommands commands = new WorkspaceCommands();
        commands.setGeoserver(geoserver);
        boolean actual = commands.delete("topp", true);
        assertTrue(actual);

        verifyHttp(server).once(method(Method.DELETE), uri("/geoserver/rest/workspaces/topp"));
    }

    @Test
    public void createWorkspace() throws Exception {
        whenHttp(server).match(post("/geoserver/rest/workspaces")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WorkspaceCommands commands = new WorkspaceCommands();
        commands.setGeoserver(geoserver);
        boolean actual = commands.create("topp");
        assertTrue(actual);
        String expectedXml = "<workspace><name>topp</name></workspace>";
        String actualXml = server.getCalls().get(0).getPostBody();
        assertEquals(expectedXml, actualXml);

        verifyHttp(server).once(method(Method.POST), uri("/geoserver/rest/workspaces"));
    }

    @Test
    public void getDefaultWorkspace() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/workspaces/default.xml")).then(stringContent("<workspace><name>cite</name></workspace>"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WorkspaceCommands commands = new WorkspaceCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getDefault();
        String expected = "cite";
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/workspaces/default.xml"));
    }

    @Test
    public void setDefaultWorkspace() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/workspaces/default.xml")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WorkspaceCommands commands = new WorkspaceCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.setDefault("topp");
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<workspace><name>topp</name></workspace>";
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/workspaces/default.xml"));
    }
}
