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

public class TemplateCommandsTest extends BaseTest {

    @Test
    public void buildUrl() throws Exception {
        TemplateCommands commands = new TemplateCommands();
        assertEquals("http://00.0.0.0:8888/geoserver/rest",
                commands.buildUrl("http://00.0.0.0:8888/geoserver", null, null, null, null, null).toString());
        assertEquals("http://00.0.0.0:8888/geoserver/rest/workspaces/topp",
                commands.buildUrl("http://00.0.0.0:8888/geoserver", "topp", null, null, null, null).toString());
        assertEquals("http://00.0.0.0:8888/geoserver/rest/workspaces/topp/datastores/states",
                commands.buildUrl("http://00.0.0.0:8888/geoserver", "topp", "states", null, null, null).toString());
        assertEquals("http://00.0.0.0:8888/geoserver/rest/workspaces/topp/datastores/states/featuretypes/states",
                commands.buildUrl("http://00.0.0.0:8888/geoserver", "topp", "states", "states", null, null).toString());
        assertEquals("http://00.0.0.0:8888/geoserver/rest/workspaces/topp/coveragestores/mosaic",
                commands.buildUrl("http://00.0.0.0:8888/geoserver", "topp", null, null, "mosaic", null).toString());
        assertEquals("http://00.0.0.0:8888/geoserver/rest/workspaces/topp/coveragestores/mosaic/coverages/mosaic",
                commands.buildUrl("http://00.0.0.0:8888/geoserver", "topp", null, null, "mosaic", "mosaic").toString());

    }

    @Test
    public void listTemplates() throws Exception {
        String url = "/geoserver/rest/templates.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("templates.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        TemplateCommands commands = new TemplateCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list(null, null, null, null, null);
        String expected = "header.ftl" + OsUtils.LINE_SEPARATOR +
                "footer.ftl" + OsUtils.LINE_SEPARATOR +
                "body.ftl" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getTemplate() throws Exception {
        String url = "/geoserver/rest/templates/title.ftl";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("template.ftl")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        TemplateCommands commands = new TemplateCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.get("title", null, null, null, null, null, null);
        String expected = "${STATE_NAME} has ${TOTAL_POP} total population";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getTemplateAsFile() throws Exception {
        String url = "/geoserver/rest/templates/title.ftl";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("template.ftl")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        TemplateCommands commands = new TemplateCommands();
        commands.setGeoserver(geoserver);
        File file = File.createTempFile("template", ".ftl");
        String result = commands.get("title", null, null, null, null, null, file);
        assertEquals(file.getAbsolutePath(), result);
        String expected = FileUtils.readFileToString(getResourceFile("template.ftl"));
        String actual = FileUtils.readFileToString(file);
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void createTemplate() throws Exception {
        String url = "/geoserver/rest/templates/template.ftl";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        TemplateCommands commands = new TemplateCommands();
        commands.setGeoserver(geoserver);
        File file = getResourceFile("template.ftl");
        String name = null;
        String workspace = null;
        String dataStore = null;
        String featureType = null;
        String coverageStore = null;
        String coverage = null;
        boolean result = commands.create(file, name, workspace, dataStore, featureType, coverageStore, coverage);
        assertTrue(result);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void modifyTemplate() throws Exception {
        String url = "/geoserver/rest/templates/template.ftl";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        TemplateCommands commands = new TemplateCommands();
        commands.setGeoserver(geoserver);
        File file = getResourceFile("template.ftl");
        String name = "template";
        String workspace = null;
        String dataStore = null;
        String featureType = null;
        String coverageStore = null;
        String coverage = null;
        boolean result = commands.modify(name, file, workspace, dataStore, featureType, coverageStore, coverage);
        assertTrue(result);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void deleteTemplate() throws Exception {
        String url = "/geoserver/rest/templates/template.ftl";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        TemplateCommands commands = new TemplateCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.delete("template", null, null, null, null, null);
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }
}
