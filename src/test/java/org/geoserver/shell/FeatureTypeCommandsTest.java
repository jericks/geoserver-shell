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

public class FeatureTypeCommandsTest extends BaseTest {

    @Test
    public void listFeatureTypes() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("featuretypes.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        FeatureTypeCommands commands = new FeatureTypeCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list("topp", "taz_shapes", "configured");
        String expected = "tasmania_cities" + OsUtils.LINE_SEPARATOR + "tasmania_hydro" + OsUtils.LINE_SEPARATOR + "tasmania_parcels" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        assertEquals("configured", server.getCalls().get(0).getParameters().get("list")[0]);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getFeatureType() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes/tasmania_cities.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("featuretype.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        FeatureTypeCommands commands = new FeatureTypeCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.get("topp", "taz_shapes", "tasmania_cities");
        String expected = "tasmania_cities" + OsUtils.LINE_SEPARATOR +
                "   Native Name: tasmania_cities" + OsUtils.LINE_SEPARATOR +
                "   Title: Tasmania cities" + OsUtils.LINE_SEPARATOR +
                "   Description: null" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR +
                "   Advertised: null" + OsUtils.LINE_SEPARATOR +
                "   Namespace: topp" + OsUtils.LINE_SEPARATOR +
                "   Keywords: " + OsUtils.LINE_SEPARATOR +
                "      cities" + OsUtils.LINE_SEPARATOR +
                "      Tasmania" + OsUtils.LINE_SEPARATOR +
                "   Native CRS: " + OsUtils.LINE_SEPARATOR +
                "		GEOGCS[\"GCS_WGS_1984\", DATUM[\"WGS_1984\", SPHEROID[\"WGS_1984\", 6378137.0, 298.257223563]], PRIMEM[\"Greenwich\"," + OsUtils.LINE_SEPARATOR +
                "		0.0], UNIT[\"degree\", 0.017453292519943295], AXIS[\"Longitude\", EAST], AXIS[\"Latitude\", NORTH]]" + OsUtils.LINE_SEPARATOR +
                "	" + OsUtils.LINE_SEPARATOR +
                "   SRS: EPSG:4326" + OsUtils.LINE_SEPARATOR +
                "   Projection Policy: FORCE_DECLARED" + OsUtils.LINE_SEPARATOR +
                "   Native Bounding Box: 145.19754,-43.423512,148.27298000000002,-40.852802 EPSG:4326" + OsUtils.LINE_SEPARATOR +
                "   LatLon Bounding Box: 145.19754,-43.423512,148.27298000000002,-40.852802 EPSG:4326" + OsUtils.LINE_SEPARATOR +
                "   Metadata: " + OsUtils.LINE_SEPARATOR +
                "      indexingEnabled = false" + OsUtils.LINE_SEPARATOR +
                "      cacheAgeMax = 3600" + OsUtils.LINE_SEPARATOR +
                "      cachingEnabled = true" + OsUtils.LINE_SEPARATOR +
                "      dirName = tasmania_cities" + OsUtils.LINE_SEPARATOR +
                "      kml.regionateFeatureLimit = 10" + OsUtils.LINE_SEPARATOR +
                "   Store: taz_shapes" + OsUtils.LINE_SEPARATOR +
                "   Max Features: 0" + OsUtils.LINE_SEPARATOR +
                "   numDecimals: 0" + OsUtils.LINE_SEPARATOR +
                "   Attributes: " + OsUtils.LINE_SEPARATOR +
                "      the_geom" + OsUtils.LINE_SEPARATOR +
                "         Binding: com.vividsolutions.jts.geom.MultiPoint" + OsUtils.LINE_SEPARATOR +
                "         Min Occurs: 0" + OsUtils.LINE_SEPARATOR +
                "         Max Occurs: 1" + OsUtils.LINE_SEPARATOR +
                "         Nillable: true" + OsUtils.LINE_SEPARATOR +
                "      CITY_NAME" + OsUtils.LINE_SEPARATOR +
                "         Binding: java.lang.String" + OsUtils.LINE_SEPARATOR +
                "         Min Occurs: 0" + OsUtils.LINE_SEPARATOR +
                "         Max Occurs: 1" + OsUtils.LINE_SEPARATOR +
                "         Nillable: true" + OsUtils.LINE_SEPARATOR +
                "         Length: 40" + OsUtils.LINE_SEPARATOR +
                "      ADMIN_NAME" + OsUtils.LINE_SEPARATOR +
                "         Binding: java.lang.String" + OsUtils.LINE_SEPARATOR +
                "         Min Occurs: 0" + OsUtils.LINE_SEPARATOR +
                "         Max Occurs: 1" + OsUtils.LINE_SEPARATOR +
                "         Nillable: true" + OsUtils.LINE_SEPARATOR +
                "         Length: 42" + OsUtils.LINE_SEPARATOR +
                "      CNTRY_NAME" + OsUtils.LINE_SEPARATOR +
                "         Binding: java.lang.String" + OsUtils.LINE_SEPARATOR +
                "         Min Occurs: 0" + OsUtils.LINE_SEPARATOR +
                "         Max Occurs: 1" + OsUtils.LINE_SEPARATOR +
                "         Nillable: true" + OsUtils.LINE_SEPARATOR +
                "         Length: 40" + OsUtils.LINE_SEPARATOR +
                "      STATUS" + OsUtils.LINE_SEPARATOR +
                "         Binding: java.lang.String" + OsUtils.LINE_SEPARATOR +
                "         Min Occurs: 0" + OsUtils.LINE_SEPARATOR +
                "         Max Occurs: 1" + OsUtils.LINE_SEPARATOR +
                "         Nillable: true" + OsUtils.LINE_SEPARATOR +
                "         Length: 50" + OsUtils.LINE_SEPARATOR +
                "      POP_CLASS" + OsUtils.LINE_SEPARATOR +
                "         Binding: java.lang.String" + OsUtils.LINE_SEPARATOR +
                "         Min Occurs: 0" + OsUtils.LINE_SEPARATOR +
                "         Max Occurs: 1" + OsUtils.LINE_SEPARATOR +
                "         Nillable: true" + OsUtils.LINE_SEPARATOR +
                "         Length: 22" + OsUtils.LINE_SEPARATOR;
        assertStringsEquals(expected, actual, true);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void deleteFeatureType() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes/tasmania_cities.xml";
        whenHttp(server).match(delete(url)).then(stringContent(getResourceString("featuretypes.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        FeatureTypeCommands commands = new FeatureTypeCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.delete("topp", "taz_shapes", "tasmania_cities", true);
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void publishFeatureType() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes.xml";
        whenHttp(server).match(post(url)).then(stringContent(getResourceString("featuretypes.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        FeatureTypeCommands commands = new FeatureTypeCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String dataStore = "taz_shapes";
        String featureType = "taz_soils";
        boolean result = commands.publish(workspace, dataStore, featureType);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<featureType><name>taz_soils</name></featureType>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void createFeatureType() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes.xml";
        whenHttp(server).match(post(url)).then(stringContent(getResourceString("featuretypes.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        FeatureTypeCommands commands = new FeatureTypeCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String dataStore = "taz_shapes";
        String featureType = "taz_soils";
        String schema = "the_geom:LineString:srid=4326,name:String,id:int";
        String title = "Soils";
        String description = "Soil Survey Polygons";
        String keywords = "soil, survey";
        String srs = "EPSG:4326";
        String projectionPolicy = null;
        boolean enabled = true;
        boolean advertised = true;
        int maxFeatures = 10000;
        int numDecimals = 5;
        String list = "configured";
        boolean result = commands.create(workspace, dataStore, featureType, schema, title, description, keywords, srs,
                projectionPolicy, enabled, advertised, maxFeatures, numDecimals, list);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<featureType><name>taz_soils</name><attributes><attribute><name>the_geom</name>" +
                "<binding>com.vividsolutions.jts.geom.LineString</binding></attribute><attribute>" +
                "<name>name</name><binding>java.lang.String</binding></attribute><attribute>" +
                "<name>id</name><binding>java.lang.Integer</binding></attribute></attributes>" +
                "<srs>EPSG:4326</srs><title>Soils</title><description>Soil Survey Polygons</description>" +
                "<keywords><keyword>soil</keyword><keyword>survey</keyword></keywords><enabled>true</enabled>" +
                "<advertised>true</advertised><maxFeatures>10000</maxFeatures>" +
                "<numDecimals>5</numDecimals></featureType>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void modifyFeatureType() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes/taz_soils.xml";
        whenHttp(server).match(put(url)).then(stringContent(getResourceString("featuretypes.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        FeatureTypeCommands commands = new FeatureTypeCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String dataStore = "taz_shapes";
        String featureType = "taz_soils";
        String newName = null;
        String title = "Soils";
        String description = "Soil Survey Polygons";
        String keywords = "soil, survey";
        String srs = "EPSG:4326";
        String projectionPolicy = null;
        String enabled = "true";
        String advertised = "true";
        String maxFeatures = "10000";
        String numDecimals = "5";
        String recalculate = "nativebbox";
        boolean result = commands.modify(workspace, dataStore, featureType, newName, title, description, keywords, srs,
                projectionPolicy, enabled, advertised, maxFeatures, numDecimals, recalculate);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<featureType><title>Soils</title><description>Soil Survey Polygons</description>" +
                "<keywords><keyword>soil</keyword><keyword> survey</keyword></keywords><srs>EPSG:4326</srs>" +
                "<enabled>true</enabled><advertised>true</advertised><maxFeatures>10000</maxFeatures>" +
                "<numDecimals>5</numDecimals></featureType>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }
}
