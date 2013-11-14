package org.geoserver.shell;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import org.springframework.shell.support.util.OsUtils;

import java.io.File;
import java.net.URL;
import java.util.Map;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class DataStoreCommandsTest extends BaseTest {

    @Test
    public void listDataStores() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("datastores.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        DataStoreCommands commands = new DataStoreCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list("topp");
        String expected = "geology" + OsUtils.LINE_SEPARATOR + "rivers" + OsUtils.LINE_SEPARATOR + "states" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getDataStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/taz_shapes.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("datastore.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        DataStoreCommands commands = new DataStoreCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.get("topp", "taz_shapes");
        String expected = "taz_shapes" + OsUtils.LINE_SEPARATOR +
                "   Enabled? true" + OsUtils.LINE_SEPARATOR +
                "   Description: null" + OsUtils.LINE_SEPARATOR +
                "   Store Type: null" + OsUtils.LINE_SEPARATOR +
                "   Type: UNKNOWN" + OsUtils.LINE_SEPARATOR +
                "   Workspace: topp" + OsUtils.LINE_SEPARATOR +
                "   Connection Parameters:" + OsUtils.LINE_SEPARATOR +
                "      url: file:data/taz_shapes" + OsUtils.LINE_SEPARATOR +
                "      namespace: http://www.openplans.org/topp" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void deleteDataStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/taz_shapes.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        DataStoreCommands commands = new DataStoreCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.delete("topp", "taz_shapes", true);
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void createDataStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        DataStoreCommands commands = new DataStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String name = "hydro";
        String connectionParams = "dbtype=h2 database=test.db";
        String description = null;
        boolean enabled = true;
        boolean result = commands.create(workspace, name, connectionParams, description, enabled);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<dataStore><name>hydro</name><enabled>true</enabled>" +
                "<connectionParameters><dbtype>h2</dbtype><database>test.db</database></connectionParameters>" +
                "</dataStore>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void createNeo4jDataStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores.json";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        DataStoreCommands commands = new DataStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String name = "neo4j";
        String connectionParams = "'The directory path of the neo4j database: '=/opt/neo4j/data/graph.db";
        String description = null;
        boolean enabled = true;
        boolean result = commands.create(workspace, name, connectionParams, description, enabled);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "{" +
                "\"dataStore\":{" +
                    "\"enabled\":true," +
                    "\"connectionParamters\":{" +
                        "\"entry\":[" +
                            "{" +
                                "\"@key\":\"The directory path of the neo4j database: \"," +
                                "\"$\":\"/opt/neo4j/data/graph.db\"" +
                            "}" +
                        "]" +
                    "}," +
                    "\"name\":\"neo4j\"" +
                    "}" +
                "}";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void modifyDataStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/hydro.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        DataStoreCommands commands = new DataStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String name = "hydro";
        String connectionParams = null;
        String description = "The hydro lines for Tasmania";
        String enabled = null;
        boolean result = commands.modify(workspace, name, connectionParams, description, enabled);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<dataStore><name>hydro</name>" +
                "<description>The hydro lines for Tasmania</description></dataStore>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void modifyNeo4jDataStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/hydro.json";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        DataStoreCommands commands = new DataStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String name = "hydro";
        String connectionParams = "'The directory path of the neo4j database: '=/opt/neo4j/data/graph.db";
        String description = "The hydro lines for Tasmania";
        String enabled = null;
        boolean result = commands.modify(workspace, name, connectionParams, description, enabled);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "{" +
                    "\"dataStore\":{" +
                        "\"connectionParamters\":{" +
                            "\"entry\":[" +
                                "{" +
                                    "\"@key\":\"The directory path of the neo4j database: \"," +
                                    "\"$\":\"/opt/neo4j/data/graph.db\"" +
                                "}" +
                            "]" +
                        "}," +
                        "\"description\":\"The hydro lines for Tasmania\"," +
                        "\"name\":\"hydro\"" +
                    "}" +
                "}";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void uploadDataStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/hydro/file.shp";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        DataStoreCommands commands = new DataStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String name = "hydro";
        String type = "shp";
        File file = File.createTempFile("hydro", ".shp");
        String configure = "first";
        String target = "shp";
        String update = "overwrite";
        String charset = "UTF-8";
        boolean result = commands.upload(workspace, name, type, file, configure, target, update, charset);
        assertTrue(result);
        Map<String, String[]> params = server.getCalls().get(0).getParameters();
        assertEquals(configure, params.get("configure")[0]);
        assertEquals(target, params.get("target")[0]);
        assertEquals(update, params.get("update")[0]);
        assertEquals(charset, params.get("charset")[0]);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void getParametersFromString() throws Exception {

        // PostGIS
        Map params = DataStoreCommands.getParametersFromString("dbtype=postgis database=postgres host=localhost port=5432");
        assertEquals(params.get("dbtype"), "postgis");
        assertEquals(params.get("database"), "postgres");
        assertEquals(params.get("host"), "localhost");
        assertEquals(params.get("port"), "5432");

        // Neo4j
        params = DataStoreCommands.getParametersFromString("'The directory path of the neo4j database'=/opt/neo4j/data/graph.db");
        assertEquals(params.get("The directory path of the neo4j database"), "/opt/neo4j/data/graph.db");

        // H2
        params = DataStoreCommands.getParametersFromString("dbtype=h2 database='C:\\My Data\\my.db'");
        assertEquals(params.get("dbtype"), "h2");
        assertTrue(params.containsKey("database"));

        // Shapefile
        params = DataStoreCommands.getParametersFromString("/my/states.shp");
        assertTrue(params.containsKey("url"));

        params = DataStoreCommands.getParametersFromString("url='/my/states.shp' 'create spatial index'=true");
        assertTrue(params.containsKey("url"));
        assertEquals(params.get("create spatial index"), "true");

        // Property
        params = DataStoreCommands.getParametersFromString("directory=/my/states.properties");
        assertTrue(params.containsKey("directory"));

        params = DataStoreCommands.getParametersFromString("directory=/my/propertyfiles");
        assertTrue(params.containsKey("directory"));

        params = DataStoreCommands.getParametersFromString("/my/states.properties");
        assertTrue(params.containsKey("directory"));

    }
}
