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

public class WmsStoreCommandsTest extends BaseTest {

    @Test
    public void listStores() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wmsStores.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listStores("topp");
        String expected = "massgis" + OsUtils.LINE_SEPARATOR +
                "usgs" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores/massgis.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wmsStore.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getStore("topp", "massgis");
        String expected = "massgis" + OsUtils.LINE_SEPARATOR +
                "   Type: WMS" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR +
                "   Workspace: topp" + OsUtils.LINE_SEPARATOR +
                "   Capabilities URL: " + OsUtils.LINE_SEPARATOR +
                "		http://giswebservices.massgis.state.ma.us/geoserver/wms?request=GetCapabilities&version=1.1.0&service=wms" + OsUtils.LINE_SEPARATOR +
                "	" + OsUtils.LINE_SEPARATOR +
                "   Max Connections: 6" + OsUtils.LINE_SEPARATOR +
                "   Read Timeout: 60" + OsUtils.LINE_SEPARATOR +
                "   Connect Timeout: 30" + OsUtils.LINE_SEPARATOR +
                "   Metadata: " + OsUtils.LINE_SEPARATOR +
                "      useConnectionPooling: true" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void createStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String store = "trimet";
        String capabilitiesUrl = "http://maps.trimet.org/geoserver/wms?request=GetCapabilities&version=1.1.0&service=wms";
        int maxConnections = 4;
        int readTimeout = 30;
        int connectTimeout = 45;
        boolean enabled = true;
        boolean result = commands.createStore(workspace, store, capabilitiesUrl, maxConnections, readTimeout, connectTimeout, enabled);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<wmsStore><name>trimet</name><type>WMS</type><enabled>true</enabled>" +
                "<capabilitiesURL>http://maps.trimet.org/geoserver/wms?" +
                "request=GetCapabilities&amp;version=1.1.0&amp;service=wms</capabilitiesURL>" +
                "<maxConnections>4</maxConnections><readTimeout>30</readTimeout><connectTimeout>45</connectTimeout>" +
                "</wmsStore>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void modifyStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores/massgis.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wmsStore.xml")), status(HttpStatus.OK_200));
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String store = "massgis";
        String capabilitiesUrl = "http://maps.massgis.org/geoserver/wms?request=GetCapabilities&version=1.1.0&service=wms";
        String maxConnections = "4";
        String readTimeout = "30";
        String connectTimeout = "45";
        String enabled = "true";
        boolean result = commands.modifyStore(workspace, store, capabilitiesUrl, maxConnections, readTimeout, connectTimeout, enabled);
        assertTrue(result);
        String actual = server.getCalls().get(1).getPostBody();
        String expected = "<wmsStore><name>massgis</name><type>WMS</type><enabled>true</enabled><workspace>" +
                "<name>topp</name><atom:link xmlns:atom=\"http://www.w3.org/2005/Atom\" rel=\"alternate\" " +
                "href=\"http://localhost:8080/geoserver/rest/workspaces/topp.xml\" type=\"application/xml\" />" +
                "</workspace><metadata><entry key=\"useConnectionPooling\">true</entry></metadata>" +
                "<__default>false</__default><capabilitiesURL>http://maps.massgis.org/geoserver/wms?" +
                "request=GetCapabilities&amp;version=1.1.0&amp;service=wms</capabilitiesURL>" +
                "<maxConnections>4</maxConnections><readTimeout>30</readTimeout>" +
                "<connectTimeout>45</connectTimeout></wmsStore>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void deleteStore() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores/massgis.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.deleteStore("topp", "massgis", true);
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void listPublishedLayers() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores/massgis/wmslayers.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wms_published_layers.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listPublishedLayers("topp", "massgis");
        String expected = "massgis:GISDATA.BIKETRAILS_ARC" + OsUtils.LINE_SEPARATOR + "massgis:WELLS.WELLS_PT" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void listAvailableLayers() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores/massgis/wmslayers.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wms_available_layers.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listAvailableLayers("topp", "massgis");
        String expected = "massgis:AFREEMAN.AUDUBON_BIRD_S_V" + OsUtils.LINE_SEPARATOR +
                "massgis:AFREEMAN.AUDUBON_BUTTERFLY_S_V" + OsUtils.LINE_SEPARATOR +
                "massgis:AFREEMAN.AUDUBON_GRID_POLY" + OsUtils.LINE_SEPARATOR +
                "massgis:AFREEMAN.BLDGS_TEST_POLY" + OsUtils.LINE_SEPARATOR +
                "massgis:AFREEMAN.CAMPUS_BLDGS_POLY" + OsUtils.LINE_SEPARATOR +
                "massgis:AFREEMAN.CAMPUS_SITES_POLY" + OsUtils.LINE_SEPARATOR +
                "massgis:AFREEMAN.CPA_COMBINED_V_PT" + OsUtils.LINE_SEPARATOR +
                "massgis:AFREEMAN.CPA_PT" + OsUtils.LINE_SEPARATOR +
                "massgis:AFREEMAN.DCAM_BLDG_PTS_20120229" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        assertEquals("available", server.getCalls().get(0).getParameters().get("list")[0]);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getLayer() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores/massgis/wmslayers/massgis:WELLS.WELLS_PT.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wms_layer.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getLayer("topp", "massgis", "massgis:WELLS.WELLS_PT");
        String expected = "massgis:WELLS.WELLS_PT" + OsUtils.LINE_SEPARATOR +
                "   Native Name: massgis:WELLS.WELLS_PT" + OsUtils.LINE_SEPARATOR +
                "   Title: DCR Well Points" + OsUtils.LINE_SEPARATOR +
                "   Description: Generated from webeditor_sde" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR +
                "   Advertised: true" + OsUtils.LINE_SEPARATOR +
                "   Namespace: topp" + OsUtils.LINE_SEPARATOR +
                "   Keywords: " + OsUtils.LINE_SEPARATOR +
                "      WELLS.WELLS_PT" + OsUtils.LINE_SEPARATOR +
                "      webeditor_sde" + OsUtils.LINE_SEPARATOR +
                "   Native CRS: PROJCS[\"NAD83 / Massachusetts Mainland\"," + OsUtils.LINE_SEPARATOR +
                "		GEOGCS[\"NAD83\"," + OsUtils.LINE_SEPARATOR +
                "		DATUM[\"North American Datum 1983\"," + OsUtils.LINE_SEPARATOR +
                "		SPHEROID[\"GRS 1980\", 6378137.0, 298.257222101, AUTHORITY[\"EPSG\",\"7019\"]]," + OsUtils.LINE_SEPARATOR +
                "		TOWGS84[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]," + OsUtils.LINE_SEPARATOR +
                "		AUTHORITY[\"EPSG\",\"6269\"]]," + OsUtils.LINE_SEPARATOR +
                "		PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]]," + OsUtils.LINE_SEPARATOR +
                "		UNIT[\"degree\", 0.017453292519943295]," + OsUtils.LINE_SEPARATOR +
                "		AXIS[\"Geodetic longitude\", EAST]," + OsUtils.LINE_SEPARATOR +
                "		AXIS[\"Geodetic latitude\", NORTH]," + OsUtils.LINE_SEPARATOR +
                "		AUTHORITY[\"EPSG\",\"4269\"]]," + OsUtils.LINE_SEPARATOR +
                "		PROJECTION[\"Lambert_Conformal_Conic_2SP\", AUTHORITY[\"EPSG\",\"9802\"]]," + OsUtils.LINE_SEPARATOR +
                "		PARAMETER[\"central_meridian\", -71.5]," + OsUtils.LINE_SEPARATOR +
                "		PARAMETER[\"latitude_of_origin\", 41.0]," + OsUtils.LINE_SEPARATOR +
                "		PARAMETER[\"standard_parallel_1\", 42.68333333333334]," + OsUtils.LINE_SEPARATOR +
                "		PARAMETER[\"false_easting\", 200000.0]," + OsUtils.LINE_SEPARATOR +
                "		PARAMETER[\"false_northing\", 750000.0]," + OsUtils.LINE_SEPARATOR +
                "		PARAMETER[\"scale_factor\", 1.0]," + OsUtils.LINE_SEPARATOR +
                "		PARAMETER[\"standard_parallel_2\", 41.71666666666667]," + OsUtils.LINE_SEPARATOR +
                "		UNIT[\"m\", 1.0]," + OsUtils.LINE_SEPARATOR +
                "		AXIS[\"Easting\", EAST]," + OsUtils.LINE_SEPARATOR +
                "		AXIS[\"Northing\", NORTH]," + OsUtils.LINE_SEPARATOR +
                "		AUTHORITY[\"EPSG\",\"26986\"]]" + OsUtils.LINE_SEPARATOR +
                "	" + OsUtils.LINE_SEPARATOR +
                "   SRS: EPSG:26986" + OsUtils.LINE_SEPARATOR +
                "   Projection Policy: FORCE_DECLARED" + OsUtils.LINE_SEPARATOR +
                "   Native Bounding Box: 33863.73245999857,777606.3746099799,330836.9724600009,959743.0446099902 EPSG:26986" + OsUtils.LINE_SEPARATOR +
                "   LatLon Bounding Box: -73.53331821222113,41.23116987753426,-69.89858964987232,42.88811062709496 EPSG:26986" + OsUtils.LINE_SEPARATOR +
                "   Store: massgis" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void createLayer() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores/massgis/wmslayers.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String store = "massgis";
        String layer = "massgis:WELLS.WELLS_PT";
        String title = "Wells";
        String description = "Health department wells";
        String keywords = "health,wells";
        boolean enabled = true;
        boolean advertised = true;
        String srs = "EPSG:4326";
        String projectionPolicy = "project";
        String recalculate = "latlonbbox";
        boolean result = commands.createWmsLayer(workspace, store, layer, title, description, keywords, enabled, advertised, srs, projectionPolicy, recalculate);
        assertTrue(result);
        assertEquals(recalculate, server.getCalls().get(0).getParameters().get("recalculate")[0]);
        String expected = "<wmsLayer><name>massgis:WELLS.WELLS_PT</name><enabled>true</enabled>" +
                "<advertised>true</advertised><title>Wells</title><description>Health department wells</description>" +
                "<keywords><string>health</string><string>wells</string></keywords><srs>EPSG:4326</srs>" +
                "<projectionPolicy>project</projectionPolicy></wmsLayer>";
        String actual = server.getCalls().get(0).getPostBody();
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void modifyLayer() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores/massgis/wmslayers/massgis:WELLS.WELLS_PT.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String store = "massgis";
        String layer = "massgis:WELLS.WELLS_PT";
        String title = "Wells";
        String description = "Health department wells";
        String keywords = "health,wells";
        String enabled = "true";
        String advertised = "true";
        String srs = "EPSG:4326";
        String projectionPolicy = "project";
        String recalculate = "latlonbbox";
        boolean result = commands.modifyWmsLayer(workspace, store, layer, title, description, keywords, enabled, advertised, srs, projectionPolicy, recalculate);
        assertTrue(result);
        assertEquals(recalculate, server.getCalls().get(0).getParameters().get("recalculate")[0]);
        String expected = "<wmsLayer><name>massgis:WELLS.WELLS_PT</name><enabled>true</enabled>" +
                "<advertised>true</advertised><title>Wells</title><description>Health department wells</description>" +
                "<keywords><string>health</string><string>wells</string></keywords><srs>EPSG:4326</srs>" +
                "<projectionPolicy>project</projectionPolicy></wmsLayer>";
        String actual = server.getCalls().get(0).getPostBody();
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void deleteLayer() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/wmsstores/massgis/wmslayers/massgis:WELLS.WELLS_PT.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        WmsStoreCommands commands = new WmsStoreCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.deleteWmsLayer("topp", "massgis", "massgis:WELLS.WELLS_PT", true);
        assertTrue(result);
        assertEquals("true", server.getCalls().get(0).getParameters().get("recurse")[0]);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }
}
