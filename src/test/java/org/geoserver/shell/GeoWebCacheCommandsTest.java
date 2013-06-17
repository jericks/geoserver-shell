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

public class GeoWebCacheCommandsTest extends BaseTest {

    @Test
    public void listLayers() throws Exception {
        String url = "/geoserver/gwc/rest/layers";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("gwc_layers.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listLayers();
        String expected = "nurc:Arc_Sample" + OsUtils.LINE_SEPARATOR +
                "nurc:Img_Sample" + OsUtils.LINE_SEPARATOR +
                "nurc:Pk50095" + OsUtils.LINE_SEPARATOR +
                "nurc:mosaic" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getLayer() throws Exception {
        String url = "/geoserver/gwc/rest/layers/sf:roads.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("gwc_layer.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.getLayer("sf:roads");
        String expected = "sf:roads" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR +
                "   Gutter: 0" + OsUtils.LINE_SEPARATOR +
                "   Auto Cache Styles: true" + OsUtils.LINE_SEPARATOR +
                "   Mime Formats:" + OsUtils.LINE_SEPARATOR +
                "      image/png" + OsUtils.LINE_SEPARATOR +
                "      image/jpeg" + OsUtils.LINE_SEPARATOR +
                "   Grid Subsets:" + OsUtils.LINE_SEPARATOR +
                "      EPSG:900913" + OsUtils.LINE_SEPARATOR +
                "      EPSG:4326" + OsUtils.LINE_SEPARATOR +
                "   Meta Dimensions:" + OsUtils.LINE_SEPARATOR +
                "      Width: 4" + OsUtils.LINE_SEPARATOR +
                "      Height: 4" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void createGeoserverLayer() throws Exception {
        String url = "/geoserver/gwc/rest/layers/topp:hydro.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String name = "topp:hydro";
        boolean enabled = true;
        String mimeType = "image/png";
        String gridSetName = "EPSG:900913";
        String gutter = "10";
        String metaWidth = "4";
        String metaHeight = "4";
        boolean result = commands.createGeoServerLayer(name, enabled, mimeType, gridSetName, gutter, metaWidth, metaHeight);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<GeoServerLayer><name>topp:hydro</name><mimeFormats><string>image/png</string></mimeFormats>" +
                "<gridSubsets><gridSubset><gridSetName>EPSG:900913</gridSetName></gridSubset></gridSubsets>" +
                "<enabled>true</enabled><gutter>10</gutter><metaWidthHeight><int>4</int><int>4</int></metaWidthHeight>" +
                "</GeoServerLayer>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void modifyGeoserverLayer() throws Exception {
        String url = "/geoserver/gwc/rest/layers/topp:hydro.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String name = "topp:hydro";
        String enabled = "false";
        String mimeType = "image/jpeg";
        String gridSetName = "EPSG:900913";
        String gutter = "10";
        String metaWidth = "4";
        String metaHeight = "4";
        boolean result = commands.modifyGeoServerLayer(name, enabled, mimeType, gridSetName, gutter, metaWidth, metaHeight);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<GeoServerLayer><name>topp:hydro</name><mimeFormats><string>image/jpeg</string></mimeFormats>" +
                "<gridSubsets><gridSubset><gridSetName>EPSG:900913</gridSetName></gridSubset></gridSubsets>" +
                "<enabled>false</enabled><gutter>10</gutter><metaWidthHeight><int>4</int><int>4</int></metaWidthHeight>" +
                "</GeoServerLayer>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void createWmsLayer() throws Exception {
        String url = "/geoserver/gwc/rest/layers/wms_parcels.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String name = "wms_parcels";
        String wmsUrl = "http://localhost:8080/geoserver/wms";
        String wmsLayers = "parcels,zoning";
        String mimeType = "image/png";
        String gridSetName = "EPSG:900913";
        boolean transparent = true;
        String backgroundColor = "#ffffff";
        String gutter = "10";
        String metaWidth = "4";
        String metaHeight = "4";
        boolean result = commands.createWMSLayer(name, wmsUrl, wmsLayers, mimeType, gridSetName, transparent, backgroundColor, gutter, metaWidth, metaHeight);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<wmsLayer><name>wms_parcels</name><mimeFormats><string>image/png</string></mimeFormats>" +
                "<gridSubsets><gridSubset><gridSetName>EPSG:900913</gridSetName></gridSubset></gridSubsets>" +
                "<transparent>true</transparent><bgColor>#ffffff</bgColor><gutter>10</gutter>" +
                "<metaWidthHeight><int>4</int><int>4</int></metaWidthHeight>" +
                "<wmsUrl><string>http://localhost:8080/geoserver/wms</string></wmsUrl>" +
                "<wmsLayers>parcels,zoning</wmsLayers></wmsLayer>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void modifyWmsLayer() throws Exception {
        String url = "/geoserver/gwc/rest/layers/wms_parcels.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String name = "wms_parcels";
        String wmsUrl = "http://localhost:8080/geoserver/wms";
        String wmsLayers = "parcels,zoning";
        String mimeType = "image/png";
        String gridSetName = "EPSG:900913";
        String transparent = "true";
        String backgroundColor = "#ffffff";
        String gutter = "10";
        String metaWidth = "4";
        String metaHeight = "4";
        boolean result = commands.modifyWMSLayer(name, wmsUrl, wmsLayers, mimeType, gridSetName, transparent, backgroundColor, gutter, metaWidth, metaHeight);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<GeoServerLayer><name>wms_parcels</name><mimeFormats><string>image/png</string></mimeFormats>" +
                "<gridSubsets><gridSubset><gridSetName>EPSG:900913</gridSetName></gridSubset></gridSubsets>" +
                "<wmsUrl><string>http://localhost:8080/geoserver/wms</string></wmsUrl>" +
                "<wmsLayers>parcels,zoning</wmsLayers><transparent>true</transparent>" +
                "<bgColor>#ffffff</bgColor><gutter>10</gutter><metaWidthHeight><int>4</int><int>4</int>" +
                "</metaWidthHeight></GeoServerLayer>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void deleteLayer() throws Exception {
        String url = "/geoserver/gwc/rest/layers/topp:soils.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.deleteLayer("topp:soils");
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void getStatus() throws Exception {
        String url = "/geoserver/gwc/rest/seed.json";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("gwc_status.json")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.status(null);
        String expected = "Tasks:" + OsUtils.LINE_SEPARATOR +
                "   Task ID: 1" + OsUtils.LINE_SEPARATOR +
                "      Status: Running" + OsUtils.LINE_SEPARATOR +
                "      # Tile Processed: 17888" + OsUtils.LINE_SEPARATOR +
                "      Total # of Tiles to Process: 44739250" + OsUtils.LINE_SEPARATOR +
                "      # of Remaining Tiles: 18319" + OsUtils.LINE_SEPARATOR +
                "   Task ID: 2" + OsUtils.LINE_SEPARATOR +
                "      Status: Running" + OsUtils.LINE_SEPARATOR +
                "      # Tile Processed: 17744" + OsUtils.LINE_SEPARATOR +
                "      Total # of Tiles to Process: 44739250" + OsUtils.LINE_SEPARATOR +
                "      # of Remaining Tiles: 18468" + OsUtils.LINE_SEPARATOR +
                "   Task ID: 3" + OsUtils.LINE_SEPARATOR +
                "      Status: Pending" + OsUtils.LINE_SEPARATOR +
                "      # Tile Processed: 16608" + OsUtils.LINE_SEPARATOR +
                "      Total # of Tiles to Process: 44739250" + OsUtils.LINE_SEPARATOR +
                "      # of Remaining Tiles: 19733" + OsUtils.LINE_SEPARATOR +
                "   Task ID: 4" + OsUtils.LINE_SEPARATOR +
                "      Status: Pending" + OsUtils.LINE_SEPARATOR +
                "      # Tile Processed: 0" + OsUtils.LINE_SEPARATOR +
                "      Total # of Tiles to Process: 1000" + OsUtils.LINE_SEPARATOR +
                "      # of Remaining Tiles: 1000" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void getStatusOfLayer() throws Exception {
        String url = "/geoserver/gwc/rest/seed/topp:states.json";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("gwc_status.json")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.status("topp:states");
        String expected = "Tasks:" + OsUtils.LINE_SEPARATOR +
                "   Task ID: 1" + OsUtils.LINE_SEPARATOR +
                "      Status: Running" + OsUtils.LINE_SEPARATOR +
                "      # Tile Processed: 17888" + OsUtils.LINE_SEPARATOR +
                "      Total # of Tiles to Process: 44739250" + OsUtils.LINE_SEPARATOR +
                "      # of Remaining Tiles: 18319" + OsUtils.LINE_SEPARATOR +
                "   Task ID: 2" + OsUtils.LINE_SEPARATOR +
                "      Status: Running" + OsUtils.LINE_SEPARATOR +
                "      # Tile Processed: 17744" + OsUtils.LINE_SEPARATOR +
                "      Total # of Tiles to Process: 44739250" + OsUtils.LINE_SEPARATOR +
                "      # of Remaining Tiles: 18468" + OsUtils.LINE_SEPARATOR +
                "   Task ID: 3" + OsUtils.LINE_SEPARATOR +
                "      Status: Pending" + OsUtils.LINE_SEPARATOR +
                "      # Tile Processed: 16608" + OsUtils.LINE_SEPARATOR +
                "      Total # of Tiles to Process: 44739250" + OsUtils.LINE_SEPARATOR +
                "      # of Remaining Tiles: 19733" + OsUtils.LINE_SEPARATOR +
                "   Task ID: 4" + OsUtils.LINE_SEPARATOR +
                "      Status: Pending" + OsUtils.LINE_SEPARATOR +
                "      # Tile Processed: 0" + OsUtils.LINE_SEPARATOR +
                "      Total # of Tiles to Process: 1000" + OsUtils.LINE_SEPARATOR +
                "      # of Remaining Tiles: 1000" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void killSeedProcess() throws Exception {
        String url = "/geoserver/gwc/rest/seed";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.kill(null, "all");
        assertTrue(result);
        assertEquals("all", server.getCalls().get(0).getParameters().get("kill_all")[0]);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void killLayerSeedProcess() throws Exception {
        String url = "/geoserver/gwc/rest/seed/topp:states";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.kill("topp:states", "all");
        assertTrue(result);
        assertEquals("all", server.getCalls().get(0).getParameters().get("kill_all")[0]);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void seed() throws Exception {
        String url = "/geoserver/gwc/rest/seed/topp:states.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String name = "topp:states";
        String bounds = "0,0,100,100";
        String gridSet = "EPSG:4326";
        int start = 0;
        int stop = 5;
        String format = "image/png";
        int threads = 4;
        boolean result = commands.seed(name, bounds, gridSet, start, stop, format, threads);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<seedRequest><name>topp:states</name><bounds><coords><double>0</double><double>100</double>" +
                "<double>0</double><double>100</double></coords></bounds><gridSetId>EPSG:4326</gridSetId>" +
                "<zoomStart>0</zoomStart><zoomStop>5</zoomStop><format>image/png</format><type>seed</type>" +
                "<threadCount>4</threadCount></seedRequest>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void reseed() throws Exception {
        String url = "/geoserver/gwc/rest/seed/topp:states.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String name = "topp:states";
        String bounds = "0,0,100,100";
        String gridSet = "EPSG:4326";
        int start = 0;
        int stop = 5;
        String format = "image/png";
        int threads = 4;
        boolean result = commands.reseed(name, bounds, gridSet, start, stop, format, threads);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<seedRequest><name>topp:states</name><bounds><coords><double>0</double><double>100</double>" +
                "<double>0</double><double>100</double></coords></bounds><gridSetId>EPSG:4326</gridSetId>" +
                "<zoomStart>0</zoomStart><zoomStop>5</zoomStop><format>image/png</format><type>reseed</type>" +
                "<threadCount>4</threadCount></seedRequest>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test
    public void truncate() throws Exception {
        String url = "/geoserver/gwc/rest/seed/topp:states.xml";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        GeoWebCacheCommands commands = new GeoWebCacheCommands();
        commands.setGeoserver(geoserver);
        String name = "topp:states";
        String bounds = "0,0,100,100";
        String gridSet = "EPSG:4326";
        int start = 0;
        int stop = 5;
        String format = "image/png";
        boolean result = commands.truncate(name, bounds, gridSet, start, stop, format);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<seedRequest><name>topp:states</name><bounds><coords><double>0</double><double>100</double>" +
                "<double>0</double><double>100</double></coords></bounds><gridSetId>EPSG:4326</gridSetId>" +
                "<zoomStart>0</zoomStart><zoomStop>5</zoomStop><format>image/png</format><type>truncate</type>" +
                "<threadCount>1</threadCount></seedRequest>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }
}
