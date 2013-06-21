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

public class OWSCommandsTest extends BaseTest {

    @Test
    public void listWcsSettings() throws Exception {
        String url = "/geoserver/rest/services/wcs/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wcs.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.wcsList(null);
        String expected = "WCS" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR +
                "   Cite Compliant: false" + OsUtils.LINE_SEPARATOR +
                "   Schema Base URL: http://schemas.opengis.net" + OsUtils.LINE_SEPARATOR +
                "   Verbose: false" + OsUtils.LINE_SEPARATOR +
                "   GML Prefixing: false" + OsUtils.LINE_SEPARATOR +
                "   LatLon: false" + OsUtils.LINE_SEPARATOR +
                "   Max Input Memory: -1" + OsUtils.LINE_SEPARATOR +
                "   Max Output Memory: -1" + OsUtils.LINE_SEPARATOR +
                "   Subsampling Enabled: false" + OsUtils.LINE_SEPARATOR +
                "   Versions:" + OsUtils.LINE_SEPARATOR +
                "      1.0.0" + OsUtils.LINE_SEPARATOR +
                "      1.1.1" + OsUtils.LINE_SEPARATOR +
                "      2.0.1" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void listWcsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wcs/workspaces/topp/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wcs.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.wcsList("topp");
        String expected = "WCS" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR +
                "   Cite Compliant: false" + OsUtils.LINE_SEPARATOR +
                "   Schema Base URL: http://schemas.opengis.net" + OsUtils.LINE_SEPARATOR +
                "   Verbose: false" + OsUtils.LINE_SEPARATOR +
                "   GML Prefixing: false" + OsUtils.LINE_SEPARATOR +
                "   LatLon: false" + OsUtils.LINE_SEPARATOR +
                "   Max Input Memory: -1" + OsUtils.LINE_SEPARATOR +
                "   Max Output Memory: -1" + OsUtils.LINE_SEPARATOR +
                "   Subsampling Enabled: false" + OsUtils.LINE_SEPARATOR +
                "   Versions:" + OsUtils.LINE_SEPARATOR +
                "      1.0.0" + OsUtils.LINE_SEPARATOR +
                "      1.1.1" + OsUtils.LINE_SEPARATOR +
                "      2.0.1" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void listWfsSettings() throws Exception {
        String url = "/geoserver/rest/services/wfs/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wfs.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.wfsList(null);
        String expected = "WFS" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR +
                "   Cite Compliant: false" + OsUtils.LINE_SEPARATOR +
                "   Schema Base URL: http://schemas.opengis.net" + OsUtils.LINE_SEPARATOR +
                "   Verbose: false" + OsUtils.LINE_SEPARATOR +
                "   serviceLevel: COMPLETE" + OsUtils.LINE_SEPARATOR +
                "   maxFeatures: 1000000" + OsUtils.LINE_SEPARATOR +
                "   featureBounding: true" + OsUtils.LINE_SEPARATOR +
                "   canonicalSchemaLocation: false" + OsUtils.LINE_SEPARATOR +
                "   encodeFeatureMember: false" + OsUtils.LINE_SEPARATOR +
                "   GML:" + OsUtils.LINE_SEPARATOR +
                "      Version: V_20" + OsUtils.LINE_SEPARATOR +
                "         SRS Name Style: URN2" + OsUtils.LINE_SEPARATOR +
                "         Override GML Attributes: false" + OsUtils.LINE_SEPARATOR +
                "      Version: V_10" + OsUtils.LINE_SEPARATOR +
                "         SRS Name Style: XML" + OsUtils.LINE_SEPARATOR +
                "         Override GML Attributes: true" + OsUtils.LINE_SEPARATOR +
                "      Version: V_11" + OsUtils.LINE_SEPARATOR +
                "         SRS Name Style: URN" + OsUtils.LINE_SEPARATOR +
                "         Override GML Attributes: false" + OsUtils.LINE_SEPARATOR +
                "   Versions:" + OsUtils.LINE_SEPARATOR +
                "      1.0.0" + OsUtils.LINE_SEPARATOR +
                "      1.1.0" + OsUtils.LINE_SEPARATOR +
                "      2.0.0" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void listWfsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wfs/workspaces/topp/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wfs.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.wfsList("topp");
        String expected = "WFS" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR +
                "   Cite Compliant: false" + OsUtils.LINE_SEPARATOR +
                "   Schema Base URL: http://schemas.opengis.net" + OsUtils.LINE_SEPARATOR +
                "   Verbose: false" + OsUtils.LINE_SEPARATOR +
                "   serviceLevel: COMPLETE" + OsUtils.LINE_SEPARATOR +
                "   maxFeatures: 1000000" + OsUtils.LINE_SEPARATOR +
                "   featureBounding: true" + OsUtils.LINE_SEPARATOR +
                "   canonicalSchemaLocation: false" + OsUtils.LINE_SEPARATOR +
                "   encodeFeatureMember: false" + OsUtils.LINE_SEPARATOR +
                "   GML:" + OsUtils.LINE_SEPARATOR +
                "      Version: V_20" + OsUtils.LINE_SEPARATOR +
                "         SRS Name Style: URN2" + OsUtils.LINE_SEPARATOR +
                "         Override GML Attributes: false" + OsUtils.LINE_SEPARATOR +
                "      Version: V_10" + OsUtils.LINE_SEPARATOR +
                "         SRS Name Style: XML" + OsUtils.LINE_SEPARATOR +
                "         Override GML Attributes: true" + OsUtils.LINE_SEPARATOR +
                "      Version: V_11" + OsUtils.LINE_SEPARATOR +
                "         SRS Name Style: URN" + OsUtils.LINE_SEPARATOR +
                "         Override GML Attributes: false" + OsUtils.LINE_SEPARATOR +
                "   Versions:" + OsUtils.LINE_SEPARATOR +
                "      1.0.0" + OsUtils.LINE_SEPARATOR +
                "      1.1.0" + OsUtils.LINE_SEPARATOR +
                "      2.0.0" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void listWmsSettings() throws Exception {
        String url = "/geoserver/rest/services/wms/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wms.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.wmsList(null);
        String expected = "WMS" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR +
                "   Cite Compliant: false" + OsUtils.LINE_SEPARATOR +
                "   Schema Base URL: http://schemas.opengis.net" + OsUtils.LINE_SEPARATOR +
                "   Verbose: false" + OsUtils.LINE_SEPARATOR +
                "   Interpolation: Nearest" + OsUtils.LINE_SEPARATOR +
                "   Max Buffer: 0" + OsUtils.LINE_SEPARATOR +
                "   Max Request Memory: 0" + OsUtils.LINE_SEPARATOR +
                "   Max Rendering Time: 0" + OsUtils.LINE_SEPARATOR +
                "   Max Rendering Errors: 0" + OsUtils.LINE_SEPARATOR +
                "   Watermark:" + OsUtils.LINE_SEPARATOR +
                "      Enabled: false" + OsUtils.LINE_SEPARATOR +
                "      Position: BOT_RIGHT" + OsUtils.LINE_SEPARATOR +
                "      Transparency: 100" + OsUtils.LINE_SEPARATOR +
                "   Versions:" + OsUtils.LINE_SEPARATOR +
                "      1.1.1" + OsUtils.LINE_SEPARATOR +
                "      1.3.0" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void listWmsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wms/workspaces/topp/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("wms.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.wmsList("topp");
        String expected = "WMS" + OsUtils.LINE_SEPARATOR +
                "   Enabled: true" + OsUtils.LINE_SEPARATOR +
                "   Cite Compliant: false" + OsUtils.LINE_SEPARATOR +
                "   Schema Base URL: http://schemas.opengis.net" + OsUtils.LINE_SEPARATOR +
                "   Verbose: false" + OsUtils.LINE_SEPARATOR +
                "   Interpolation: Nearest" + OsUtils.LINE_SEPARATOR +
                "   Max Buffer: 0" + OsUtils.LINE_SEPARATOR +
                "   Max Request Memory: 0" + OsUtils.LINE_SEPARATOR +
                "   Max Rendering Time: 0" + OsUtils.LINE_SEPARATOR +
                "   Max Rendering Errors: 0" + OsUtils.LINE_SEPARATOR +
                "   Watermark:" + OsUtils.LINE_SEPARATOR +
                "      Enabled: false" + OsUtils.LINE_SEPARATOR +
                "      Position: BOT_RIGHT" + OsUtils.LINE_SEPARATOR +
                "      Transparency: 100" + OsUtils.LINE_SEPARATOR +
                "   Versions:" + OsUtils.LINE_SEPARATOR +
                "      1.1.1" + OsUtils.LINE_SEPARATOR +
                "      1.3.0" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void deleteWcsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wcs/workspaces/topp/settings.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.wcsDelete("topp");
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void deleteWfsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wfs/workspaces/topp/settings.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.wfsDelete("topp");
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void deleteWmsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wms/workspaces/topp/settings.xml";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.wmsDelete("topp");
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    @Test
    public void createWcsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wcs/workspaces/topp/settings.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        boolean enabled = true;
        boolean verbose = true;
        boolean gmlPrefixing = false;
        boolean latlon = false;
        int maxInputMemory = 512;
        int maxOutputMemory = 256;
        boolean subsamplingEnabled = true;
        boolean result = commands.wcsCreate(workspace, enabled, verbose, gmlPrefixing, latlon, maxInputMemory, maxOutputMemory, subsamplingEnabled);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<wcs><workspace>topp</workspace><name>WCS</name><enabled>true</enabled>" +
                "<verbose>true</verbose><gmlPrefixing>false</gmlPrefixing><latLon>false</latLon>" +
                "<maxInputMemory>512</maxInputMemory><maxOutputMemory>256</maxOutputMemory>" +
                "<subsamplingEnabled>true</subsamplingEnabled></wcs>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void modifyWcsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wcs/workspaces/topp/settings.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String enabled = "true";
        String verbose = "true";
        String gmlPrefixing = "false";
        String latlon = "false";
        String maxInputMemory = "512";
        String maxOutputMemory = "256";
        String subsamplingEnabled = "true";
        boolean result = commands.wcsModify(workspace, enabled, verbose, gmlPrefixing, latlon, maxInputMemory, maxOutputMemory, subsamplingEnabled);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<wcs><enabled>true</enabled><verbose>true</verbose><gmlPrefixing>false</gmlPrefixing>" +
                "<latLon>false</latLon><maxInputMemory>512</maxInputMemory><maxOutputMemory>256</maxOutputMemory>" +
                "<subsamplingEnabled>true</subsamplingEnabled></wcs>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void createWfsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wfs/workspaces/topp/settings.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        boolean enabled = true;
        boolean verbose = true;
        long maxFeatures = 10000;
        boolean featureBounding = true;
        boolean canonicalSchemaLocation = false;
        boolean encodeFeatureMember = true;
        boolean citeCompliant = false;
        String serverLevel = "BASIC";
        boolean result = commands.wfsCreate(workspace, enabled, verbose, maxFeatures, featureBounding, canonicalSchemaLocation, encodeFeatureMember, citeCompliant, serverLevel);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<wfs><workspace>topp</workspace><name>WFS</name><enabled>true</enabled>" +
                "<verbose>true</verbose><citeCompliant>false</citeCompliant><serviceLevel>BASIC</serviceLevel>" +
                "<maxFeatures>10000</maxFeatures><featureBounding>true</featureBounding>" +
                "<canonicalSchemaLocation>false</canonicalSchemaLocation>" +
                "<encodeFeatureMember>true</encodeFeatureMember></wfs>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void modifyWfsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wfs/workspaces/topp/settings.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String enabled = "true";
        String verbose = "true";
        String maxFeatures = "10000";
        String featureBounding = "true";
        String canonicalSchemaLocation = "false";
        String encodeFeatureMember = "true";
        String citeCompliant = "false";
        String serverLevel = "BASIC";
        boolean result = commands.wfsModify(workspace, enabled, verbose, maxFeatures, featureBounding, canonicalSchemaLocation, encodeFeatureMember, citeCompliant, serverLevel);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<wfs><enabled>true</enabled>" +
                "<verbose>true</verbose><citeCompliant>false</citeCompliant><serviceLevel>BASIC</serviceLevel>" +
                "<maxFeatures>10000</maxFeatures><featureBounding>true</featureBounding>" +
                "<canonicalSchemaLocation>false</canonicalSchemaLocation>" +
                "<encodeFeatureMember>true</encodeFeatureMember></wfs>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void createWmsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wms/workspaces/topp/settings.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        boolean enabled = true;
        boolean verbose = true;
        boolean citeCompliant = true;
        long maxBuffer = 1000;
        long maxRequestMemory = 256;
        long maxRenderingTime = 56;
        long maxRenderingErrors = 6;
        String interpolation = "Bicubic";
        boolean waterMarkEnabled = true;
        String waterMarkPosition = "TOP";
        int waterMarkTransparency = 98;
        boolean result = commands.wmsCreate(workspace, enabled, verbose, citeCompliant, maxBuffer, maxRequestMemory, maxRenderingTime, maxRenderingErrors, interpolation, waterMarkEnabled, waterMarkPosition, waterMarkTransparency);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<wms><workspace>topp</workspace><name>WMS</name><enabled>true</enabled>" +
                "<verbose>true</verbose><citeCompliant>true</citeCompliant><watermark><enabled>true</enabled>" +
                "<position>TOP</position><transparency>98</transparency></watermark>" +
                "<interpolation>Bicubic</interpolation><maxBuffer>1000</maxBuffer>" +
                "<maxRequestMemory>256</maxRequestMemory><maxRenderingTime>56</maxRenderingTime>" +
                "<maxRenderingErrors>6</maxRenderingErrors></wms>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void modifyWmsSettingsForWorkspace() throws Exception {
        String url = "/geoserver/rest/services/wms/workspaces/topp/settings.xml";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        OWSCommands commands = new OWSCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String enabled = "true";
        String verbose = "true";
        String citeCompliant = "true";
        String maxBuffer = "1000";
        String maxRequestMemory = "256";
        String maxRenderingTime = "56";
        String maxRenderingErrors = "6";
        String interpolation = "Bicubic";
        String waterMarkEnabled = "true";
        String waterMarkPosition = "TOP";
        String waterMarkTransparency = "98";
        boolean result = commands.wmsModify(workspace, enabled, verbose, citeCompliant, maxBuffer, maxRequestMemory, maxRenderingTime, maxRenderingErrors, interpolation, waterMarkEnabled, waterMarkPosition, waterMarkTransparency);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<wms><enabled>true</enabled>" +
                "<verbose>true</verbose><citeCompliant>true</citeCompliant><watermark><enabled>true</enabled>" +
                "<position>TOP</position><transparency>98</transparency></watermark>" +
                "<interpolation>Bicubic</interpolation><maxBuffer>1000</maxBuffer>" +
                "<maxRequestMemory>256</maxRequestMemory><maxRenderingTime>56</maxRenderingTime>" +
                "<maxRenderingErrors>6</maxRenderingErrors></wms>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }
}
