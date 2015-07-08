package org.geoserver.shell;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.method;
import static com.xebialabs.restito.semantics.Condition.post;
import static com.xebialabs.restito.semantics.Condition.uri;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PostGISCommandsTest extends BaseTest {

    @Test
    public void createDataStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        PostGISCommands commands = new PostGISCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String datastore = "postgis";
        String host = "localhost";
        String port = "5432";
        String database = "naturalearth";
        String schema = "ne";
        String user = "admin";
        String password = "s$cr$t";
        boolean result = commands.createDataStore(workspace, datastore, host, port, database, schema, user, password);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<dataStore>" +
                "<name>postgis</name><enabled>true</enabled>" +
                "<connectionParameters>" +
                "<database>naturalearth</database>" +
                "<dbtype>postgis</dbtype>" +
                "<host>localhost</host>" +
                "<passwd>s$cr$t</passwd>" +
                "<port>5432</port>" +
                "<schema>ne</schema>" +
                "<user>admin</user>" +
                "</connectionParameters></dataStore>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void publishFeatureType() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/postgis/featuretypes.xml";
        whenHttp(server).match(post(url)).then(stringContent(getResourceString("featuretypes.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        PostGISCommands commands = new PostGISCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String dataStore = "postgis";
        String table = "world_boundaries";
        boolean result = commands.publishLayer(workspace, dataStore, table);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<featureType><name>world_boundaries</name></featureType>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }
}
