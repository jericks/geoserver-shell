package org.geoserver.shell;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import org.springframework.shell.support.util.OsUtils;

import java.io.File;
import java.io.FileWriter;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ScriptCommandsTest extends BaseTest {

    // WPS

    @Test
    public void listWps() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/wps.json")).then(stringContent(getResourceString("scripts.json")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listWps();
        String expected = "buffer.groovy" + OsUtils.LINE_SEPARATOR + "merge.groovy" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/wps.json"));
    }

    @Test
    public void getWps() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/wps/buffer.groovy")).then(stringContent(getResourceString("script.groovy")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getWps("buffer.groovy", null);
        String expected = getResourceString("script.groovy");
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/wps/buffer.groovy"));
    }

    @Test
    public void getWpsToFile() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/wps/buffer.groovy")).then(stringContent(getResourceString("script.groovy")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        File file = File.createTempFile("buffer",".groovy");
        String result = commands.getWps("buffer.groovy", file);
        assertEquals(file.getAbsolutePath(), result);
        assertEquals(FileUtils.readFileToString(file), getResourceString("script.groovy"));

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/wps/buffer.groovy"));
    }

    @Test
    public void createWps() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/scripts/wps/buffer.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        File file = File.createTempFile("buffer", ".groovy");
        FileWriter writer = new FileWriter(file);
        writer.write(getResourceString("script.groovy"));
        writer.close();

        boolean result = commands.createWps("buffer.groovy", file);
        assertTrue(result);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/scripts/wps/buffer.groovy"));
    }

    @Test
    public void modifyWps() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/scripts/wps/buffer.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        File file = File.createTempFile("buffer", ".groovy");
        FileWriter writer = new FileWriter(file);
        writer.write(getResourceString("script.groovy"));
        writer.close();

        boolean result = commands.modifyWps("buffer.groovy", file);
        assertTrue(result);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/scripts/wps/buffer.groovy"));
    }

    @Test
    public void deleteWps() throws Exception {
        whenHttp(server).match(delete("/geoserver/rest/scripts/wps/buffer.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        boolean result = commands.deleteWps("buffer.groovy");
        assertTrue(result);

        verifyHttp(server).once(method(Method.DELETE), uri("/geoserver/rest/scripts/wps/buffer.groovy"));
    }

    // Function

    @Test
    public void listFunction() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/function.json")).then(stringContent(getResourceString("scripts.json")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listFunctions();
        String expected = "buffer.groovy" + OsUtils.LINE_SEPARATOR + "merge.groovy" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/function.json"));
    }

    @Test
    public void getFunction() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/function/buffer.groovy")).then(stringContent(getResourceString("script.groovy")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getFunction("buffer.groovy", null);
        String expected = getResourceString("script.groovy");
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/function/buffer.groovy"));
    }

    @Test
    public void getFunctionToFile() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/function/buffer.groovy")).then(stringContent(getResourceString("script.groovy")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        File file = File.createTempFile("buffer",".groovy");
        String result = commands.getFunction("buffer.groovy", file);
        assertEquals(file.getAbsolutePath(), result);
        assertEquals(FileUtils.readFileToString(file), getResourceString("script.groovy"));


        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/function/buffer.groovy"));
    }

    @Test
    public void createFunction() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/scripts/function/buffer.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        File file = File.createTempFile("buffer", ".groovy");
        FileWriter writer = new FileWriter(file);
        writer.write(getResourceString("script.groovy"));
        writer.close();

        boolean result = commands.createFunction("buffer.groovy", file);
        assertTrue(result);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/scripts/function/buffer.groovy"));
    }

    @Test
    public void modifyFunction() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/scripts/function/buffer.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        File file = File.createTempFile("buffer", ".groovy");
        FileWriter writer = new FileWriter(file);
        writer.write(getResourceString("script.groovy"));
        writer.close();

        boolean result = commands.modifyFunction("buffer.groovy", file);
        assertTrue(result);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/scripts/function/buffer.groovy"));
    }

    @Test
    public void deleteFunction() throws Exception {
        whenHttp(server).match(delete("/geoserver/rest/scripts/function/buffer.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        boolean result = commands.deleteFunction("buffer.groovy");
        assertTrue(result);

        verifyHttp(server).once(method(Method.DELETE), uri("/geoserver/rest/scripts/function/buffer.groovy"));
    }

    // WFS TX

    @Test
    public void listWfsTx() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/wfs/tx.json")).then(stringContent(getResourceString("scripts.json")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listWfsTx();
        String expected = "buffer.groovy" + OsUtils.LINE_SEPARATOR + "merge.groovy" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/wfs/tx.json"));
    }

    @Test
    public void getWfsTx() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/wfs/tx/buffer.groovy")).then(stringContent(getResourceString("script.groovy")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getWfsTx("buffer.groovy", null);
        String expected = getResourceString("script.groovy");
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/wfs/tx/buffer.groovy"));
    }

    @Test
    public void getWfsTxToFile() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/wfs/tx/buffer.groovy")).then(stringContent(getResourceString("script.groovy")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        File file = File.createTempFile("buffer",".groovy");
        String result = commands.getWfsTx("buffer.groovy", file);
        assertEquals(file.getAbsolutePath(), result);
        assertEquals(FileUtils.readFileToString(file), getResourceString("script.groovy"));


        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/wfs/tx/buffer.groovy"));
    }

    @Test
    public void createWfsTx() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/scripts/wfs/tx/buffer.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        File file = File.createTempFile("buffer", ".groovy");
        FileWriter writer = new FileWriter(file);
        writer.write(getResourceString("script.groovy"));
        writer.close();

        boolean result = commands.createWfsTx("buffer.groovy", file);
        assertTrue(result);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/scripts/wfs/tx/buffer.groovy"));
    }

    @Test
    public void modifyWfsTx() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/scripts/wfs/tx/buffer.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        File file = File.createTempFile("buffer", ".groovy");
        FileWriter writer = new FileWriter(file);
        writer.write(getResourceString("script.groovy"));
        writer.close();

        boolean result = commands.modifyWfsTx("buffer.groovy", file);
        assertTrue(result);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/scripts/wfs/tx/buffer.groovy"));
    }

    @Test
    public void deleteWfsTx() throws Exception {
        whenHttp(server).match(delete("/geoserver/rest/scripts/wfs/tx/buffer.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        boolean result = commands.deleteWfsTx("buffer.groovy");
        assertTrue(result);

        verifyHttp(server).once(method(Method.DELETE), uri("/geoserver/rest/scripts/wfs/tx/buffer.groovy"));
    }

    // App

    @Test
    public void listApp() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/apps.json")).then(stringContent(getResourceString("scripts.json")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listApps();
        String expected = "buffer.groovy" + OsUtils.LINE_SEPARATOR + "merge.groovy" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/apps.json"));
    }

    @Test
    public void getApp() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/apps/buffer/main.groovy")).then(stringContent(getResourceString("script.groovy")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getApp("buffer", "groovy", null);
        String expected = getResourceString("script.groovy");
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/apps/buffer/main.groovy"));
    }

    @Test
    public void getAppToFile() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/scripts/apps/buffer/main.groovy")).then(stringContent(getResourceString("script.groovy")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);
        File file = File.createTempFile("buffer",".groovy");
        String result = commands.getApp("buffer", "groovy", file);
        assertEquals(file.getAbsolutePath(), result);
        assertEquals(FileUtils.readFileToString(file), getResourceString("script.groovy"));

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/scripts/apps/buffer/main.groovy"));
    }

    @Test
    public void createApp() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/scripts/apps/buffer/main.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        File file = File.createTempFile("buffer", ".groovy");
        FileWriter writer = new FileWriter(file);
        writer.write(getResourceString("script.groovy"));
        writer.close();

        boolean result = commands.createApp("buffer", "groovy", file);
        assertTrue(result);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/scripts/apps/buffer/main.groovy"));
    }

    @Test
    public void modifyApp() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/scripts/apps/buffer/main.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        File file = File.createTempFile("buffer", "groovy");
        FileWriter writer = new FileWriter(file);
        writer.write(getResourceString("script.groovy"));
        writer.close();

        boolean result = commands.modifyApp("buffer", "groovy", file);
        assertTrue(result);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/scripts/apps/buffer/main.groovy"));
    }

    @Test
    public void deleteApp() throws Exception {
        whenHttp(server).match(delete("/geoserver/rest/scripts/apps/buffer/main.groovy")).then(stringContent("true"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        boolean result = commands.deleteApp("buffer","groovy");
        assertTrue(result);

        verifyHttp(server).once(method(Method.DELETE), uri("/geoserver/rest/scripts/apps/buffer/main.groovy"));
    }

    // Session

    @Test
    public void listSession() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/sessions")).then(stringContent(getResourceString("sessions.json")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        String actual = commands.listSession(null);
        String expected = "0 groovy" + OsUtils.LINE_SEPARATOR + "1 python" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/sessions"));
    }

    @Test
    public void getSession() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/sessions/groovy/1")).then(stringContent(getResourceString("session.json")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        String actual = commands.getSession("groovy","1");
        String expected = "1 groovy" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/sessions/groovy/1"));
    }

    @Test
    public void createSession() throws Exception {
        whenHttp(server).match(post("/geoserver/rest/sessions/groovy")).then(stringContent("1"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        String actual = commands.createSession("groovy");
        String expected = "1";
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.POST), uri("/geoserver/rest/sessions/groovy"));
    }

    @Test
    public void runSession() throws Exception {
        whenHttp(server).match(put("/geoserver/rest/sessions/groovy/1")).then(stringContent("Hello World!"), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        ScriptCommands commands = new ScriptCommands();
        commands.setGeoserver(geoserver);

        String actual = commands.runSession("groovy","1","println 'Hello World!'");
        String expected = "Hello World!";
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.PUT), uri("/geoserver/rest/sessions/groovy/1"));
    }

}


