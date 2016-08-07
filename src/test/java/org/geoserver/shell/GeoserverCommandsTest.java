package org.geoserver.shell;

import com.google.common.io.Resources;
import com.xebialabs.restito.semantics.Function;
import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.*;
import static com.xebialabs.restito.semantics.Condition.*;
import static junit.framework.Assert.*;

public class GeoserverCommandsTest extends BaseTest {

    @Test
    public void set() throws IOException {
        whenHttp(server).match(get("/geoserver/rest/about/versions.xml")).then(stringContent(getResourceString("version.xml")), status(HttpStatus.OK_200));
        Geoserver gs = new Geoserver();
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(gs);
        String url = "http://00.0.0.0:8888/geoserver";
        String user = "admin";
        String pass = "geoserver";
        boolean result = commands.set(url, user, pass);
        assertTrue(result);
        assertEquals(url, gs.getUrl());
        assertEquals(user, gs.getUser());
        assertEquals(pass, gs.getPassword());
    }

    @Test
    public void setBadUrl() throws IOException {
        whenHttp(server).match(get("/geoserver/rest/about/versions.xml")).then(stringContent(getResourceString("version.xml")), status(HttpStatus.OK_200));
        Geoserver gs = new Geoserver();
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(gs);
        String url = "http://00.0.0.0:8888/bad_url/geoserver";
        String user = "admin";
        String pass = "geoserver";
        boolean result = commands.set(url, user, pass);
        assertFalse(result);
        assertEquals(url, gs.getUrl());
        assertEquals(user, gs.getUser());
        assertEquals(pass, gs.getPassword());
    }

    @Test
    public void show() {
        Geoserver gs = new Geoserver();
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(gs);
        String url = "http://localhost:8080/geoserver";
        String user = "admin";
        String pass = "geoserver";
        commands.set(url, user, pass);
        String expected = "http://localhost:8080/geoserver admin geoserver";
        String actual = commands.show();
        assertEquals(expected, actual);
    }

    @Test
    public void setVerbose() {
        Geoserver gs = new Geoserver();
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(gs);
        assertFalse(gs.isVerbose());
        assertTrue(commands.verbose(true));
        assertTrue(gs.isVerbose());
    }

    @Test
    public void reset() throws Exception {
        String url = "/geoserver/rest/reset";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.reset();
        assertTrue(result);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void reload() throws Exception {
        String url = "/geoserver/rest/reload";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.reload();
        assertTrue(result);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void backup() throws Exception {
        String url = "/geoserver/rest/bkprst/backup";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.backup("backupdir1", true, true, false);
        assertTrue(result);
        String expected = "<task><path>backupdir1</path><includedata>true</includedata><includegwc>true</includegwc><includelog>false</includelog></task>";
        String actual = server.getCalls().get(0).getPostBody();
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void restore() throws Exception {
        String url = "/geoserver/rest/bkprst/restore";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.restore("backupdir1");
        assertTrue(result);
        String expected = "<task><path>backupdir1</path></task>";
        String actual = server.getCalls().get(0).getPostBody();
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void getMap() throws Exception {
        String url = "/geoserver/wms/reflect";
        whenHttp(server).match(get(url)).then(custom(new Function<Response, Response>() {
            @Override
            public Response apply(Response r) {
                URL url = Resources.getResource("map.png");
                try {
                    r.getOutputStream().write(Resources.toByteArray(url));
                } catch (IOException e) {
                    System.err.println("Unable to read map.png!");
                }
                r.setContentType("image/png");
                return r;
            }
        }), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(geoserver);
        String result = commands.getMap("states", "map.png", null, null, null, null, null);
        assertEquals("map.png", result);
        assertEquals("states", server.getCalls().get(0).getParameters().get("layers")[0]);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getFeature() throws Exception {
        String url = "/geoserver/wfs";
        whenHttp(server).match(get(url)).then(resourceContent("points.csv"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(geoserver);
        String result = commands.getFeature("topp:states", "1.0.0", null, null, null, null, null, null, "csv", null);
        assertNotNull(result);
        String expected = IOUtils.toString(Resources.getResource("points.csv"));
        assertEquals(expected, result);
        assertEquals("topp:states", server.getCalls().get(0).getParameters().get("typeName")[0]);
        assertEquals("1.0.0", server.getCalls().get(0).getParameters().get("version")[0]);
        assertEquals("csv", server.getCalls().get(0).getParameters().get("outputFormat")[0]);
        assertNull(server.getCalls().get(0).getParameters().get("maxFeatures"));
        assertNull(server.getCalls().get(0).getParameters().get("sortBy"));
        assertNull(server.getCalls().get(0).getParameters().get("propertyName"));
        assertNull(server.getCalls().get(0).getParameters().get("featureID"));
        assertNull(server.getCalls().get(0).getParameters().get("bbox"));
        assertNull(server.getCalls().get(0).getParameters().get("srsName"));
        assertNull(server.getCalls().get(0).getParameters().get("cql_filter"));
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getLegend() throws Exception {
        String url = "/geoserver/wms";
        whenHttp(server).match(get(url)).then(custom(new Function<Response, Response>() {
            @Override
            public Response apply(Response r) {
                URL url = Resources.getResource("map.png");
                try {
                    r.getOutputStream().write(Resources.toByteArray(url));
                } catch (IOException e) {
                    System.err.println("Unable to read map.png!");
                }
                r.setContentType("image/png");
                return r;
            }
        }), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoserverCommands commands = new GeoserverCommands();
        commands.setGeoserver(geoserver);
        String result = commands.getLegend("states", "states_by_pop", null, null, null, "legend.png", "20", "20", "image/png");
        assertEquals("legend.png", result);
        Map<String,String[]> params = server.getCalls().get(0).getParameters();
        assertEquals("states", params.get("LAYER")[0]);
        assertEquals("states_by_pop", params.get("STYLE")[0]);
        assertNull(params.get("FEATURETYPE"));
        assertNull(params.get("RULE"));
        assertNull(params.get("SCALE"));
        assertEquals("image/png", params.get("FORMAT")[0]);
        assertEquals("20", params.get("WIDTH")[0]);
        assertEquals("20", params.get("HEIGHT")[0]);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

}
