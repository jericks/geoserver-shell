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

public class SettingsCommandsTest extends BaseTest {

    @Test
    public void listSettings() throws Exception {
        String url = "/geoserver/rest/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("settings.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SettingsCommands commands = new SettingsCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list();
        String expected = "Settings" + OsUtils.LINE_SEPARATOR +
                "   Charset: null" + OsUtils.LINE_SEPARATOR +
                "   Number of Decimals: 0" + OsUtils.LINE_SEPARATOR +
                "   Online Resource: null" + OsUtils.LINE_SEPARATOR +
                "   Verbose: true" + OsUtils.LINE_SEPARATOR +
                "   Verbose Exceptions: true" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "Contact" + OsUtils.LINE_SEPARATOR +
                "   City: Somewhere" + OsUtils.LINE_SEPARATOR +
                "   Country: USA" + OsUtils.LINE_SEPARATOR +
                "   Type: Home" + OsUtils.LINE_SEPARATOR +
                "   Email: somebody@somewhere.com" + OsUtils.LINE_SEPARATOR +
                "   Organization: Geoserver" + OsUtils.LINE_SEPARATOR +
                "   Name: John Doe" + OsUtils.LINE_SEPARATOR +
                "   Position: Map Maker" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "Java Advanced Imaging (JAI)" + OsUtils.LINE_SEPARATOR +
                "   Allow Interpolation: false" + OsUtils.LINE_SEPARATOR +
                "   Recycling: false" + OsUtils.LINE_SEPARATOR +
                "   Allow Interpolation: false" + OsUtils.LINE_SEPARATOR +
                "   Tile Threads: 0" + OsUtils.LINE_SEPARATOR +
                "   Memory Capacity: 0.0" + OsUtils.LINE_SEPARATOR +
                "   Memory Threshold: 0.0" + OsUtils.LINE_SEPARATOR +
                "   Image IO Cache: false" + OsUtils.LINE_SEPARATOR +
                "   Png Acceleration: true" + OsUtils.LINE_SEPARATOR +
                "   Jpeg Acceleration: true" + OsUtils.LINE_SEPARATOR +
                "   Allow Native Mosaic: false" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "Coverage Access" + OsUtils.LINE_SEPARATOR +
                "   maxPoolSize: 10" + OsUtils.LINE_SEPARATOR +
                "   corePoolSize: 5" + OsUtils.LINE_SEPARATOR +
                "   keepAliveTime: 30000" + OsUtils.LINE_SEPARATOR +
                "   queueType: UNBOUNDED" + OsUtils.LINE_SEPARATOR +
                "   imageIOCacheThreshold: 0" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "Other" + OsUtils.LINE_SEPARATOR +
                "   Update Sequence: 245" + OsUtils.LINE_SEPARATOR +
                "   Feature Type Cache Size: 0" + OsUtils.LINE_SEPARATOR +
                "   Global Services: true" + OsUtils.LINE_SEPARATOR +
                "   XML Post Request Log Buffer Size: 1024" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void modifySettings() throws Exception {
        String url = "/geoserver/rest/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("settings.xml")), status(HttpStatus.OK_200));
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SettingsCommands commands = new SettingsCommands();
        commands.setGeoserver(geoserver);

        String charset = null;
        String numberOfDecimals = null;
        String onlineResource = null;
        String verbose = null;
        String verboseExceptions = null;
        // Global
        String updateSequence = null;
        String featureTypeCacheSize = null;
        String globalServices = null;
        String xmlPostRequestLogBufferSize = null;
        // JAI
        String allowInterpolation = null;
        String recycling = null;
        String tilePriority = null;
        String tileThreads = null;
        String memoryCapacity = null;
        String memoryThreshold = null;
        String imageIOCache = null;
        String pngAcceleration = null;
        String jpegAcceleration = null;
        String allowNativeMosaic = null;
        // Coverage Access
        String maxPoolSize = null;
        String corePoolSize = null;
        String keepAliveTime = null;
        String queueType = null;
        String imageIOCacheThreshold = null;
        // Contact
        String person = "Jared Erickson";
        String position = "Cartographer";
        String email = null;
        String organization = null;
        String city = "Seattle";
        String country = "USA";
        String addressType = null;

        boolean result = commands.modify(charset, numberOfDecimals, onlineResource, verbose, verboseExceptions,
                updateSequence, featureTypeCacheSize, globalServices, xmlPostRequestLogBufferSize,
                allowInterpolation, recycling, tilePriority, tileThreads, memoryCapacity, memoryThreshold, imageIOCache, pngAcceleration, jpegAcceleration, allowNativeMosaic,
                maxPoolSize, corePoolSize, keepAliveTime, queueType, imageIOCacheThreshold, person, position, email, organization, city, country, addressType);
        assertTrue(result);
        String actual = server.getCalls().get(1).getPostBody();
        String expected = "<global><settings><id>SettingsInfoImpl-4e79191a:13f122c7efe:-8000</id><contact>" +
                "<addressCity>Seattle</addressCity><addressCountry>USA</addressCountry><addressType>Home</addressType>" +
                "<contactEmail>somebody@somewhere.com</contactEmail><contactOrganization>Geoserver</contactOrganization>" +
                "<contactPerson>Jared Erickson</contactPerson><contactPosition>Cartographer</contactPosition></contact>" +
                "<numDecimals>0</numDecimals><verbose>true</verbose><verboseExceptions>true</verboseExceptions></settings>" +
                "<jai><allowInterpolation>false</allowInterpolation><recycling>false</recycling><tilePriority>0</tilePriority>" +
                "<tileThreads>0</tileThreads><memoryCapacity>0.0</memoryCapacity><memoryThreshold>0.0</memoryThreshold>" +
                "<imageIOCache>false</imageIOCache><pngAcceleration>true</pngAcceleration><jpegAcceleration>true</jpegAcceleration>" +
                "<allowNativeMosaic>false</allowNativeMosaic></jai><coverageAccess><maxPoolSize>10</maxPoolSize>" +
                "<corePoolSize>5</corePoolSize><keepAliveTime>30000</keepAliveTime><queueType>UNBOUNDED</queueType>" +
                "<imageIOCacheThreshold>0</imageIOCacheThreshold></coverageAccess><updateSequence>245</updateSequence>" +
                "<featureTypeCacheSize>0</featureTypeCacheSize><globalServices>true</globalServices>" +
                "<xmlPostRequestLogBufferSize>1024</xmlPostRequestLogBufferSize></global>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void listContactSettings() throws Exception {
        String url = "/geoserver/rest/settings/contact.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("contactSettings.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SettingsCommands commands = new SettingsCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listContact();
        String expected = "City: Somewhere" + OsUtils.LINE_SEPARATOR +
                "Country: USA" + OsUtils.LINE_SEPARATOR +
                "Type: Home" + OsUtils.LINE_SEPARATOR +
                "Email: somebody@somewhere.com" + OsUtils.LINE_SEPARATOR +
                "Organization: Geoserver" + OsUtils.LINE_SEPARATOR +
                "Name: John Doe" + OsUtils.LINE_SEPARATOR +
                "Position: Map Maker" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void modifyContactSettings() throws Exception {
        String url = "/geoserver/rest/settings/contact.xml";
        whenHttp(server).match(put(url)).then(stringContent(getResourceString("contactSettings.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SettingsCommands commands = new SettingsCommands();
        commands.setGeoserver(geoserver);
        String person = "Jane Doe";
        String position = "CIO";
        String email = "janedoe@cio.com";
        String organization = "GeoComp";
        String city = "Plainville";
        String country = "Canada";
        String addressType = "Work";
        boolean result = commands.modifyContact(person, position, email, organization, city, country, addressType);
        assertTrue(result);
        String actual = server.getCalls().get(0).getPostBody();
        String expected = "<contact><contactPerson>Jane Doe</contactPerson><contactPosition>CIO</contactPosition>" +
                "<contactEmail>janedoe@cio.com</contactEmail><contactOrganization>GeoComp</contactOrganization>" +
                "<addressCity>Plainville</addressCity><addressCountry>Canada</addressCountry><" +
                "addressType>Work</addressType></contact>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void listLocalSettings() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("localSettings.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SettingsCommands commands = new SettingsCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.listLocal("topp");
        String expected = "Settings" + OsUtils.LINE_SEPARATOR +
                "   Charset: UTF-8" + OsUtils.LINE_SEPARATOR +
                "   Number of Decimals: 4" + OsUtils.LINE_SEPARATOR +
                "   Online Resource: null" + OsUtils.LINE_SEPARATOR +
                "   Verbose: false" + OsUtils.LINE_SEPARATOR +
                "   Verbose Exceptions: false" + OsUtils.LINE_SEPARATOR +
                "" + OsUtils.LINE_SEPARATOR +
                "Contact" + OsUtils.LINE_SEPARATOR +
                "   City: null" + OsUtils.LINE_SEPARATOR +
                "   Country: null" + OsUtils.LINE_SEPARATOR +
                "   Type: work" + OsUtils.LINE_SEPARATOR +
                "   Email: null" + OsUtils.LINE_SEPARATOR +
                "   Organization: Work" + OsUtils.LINE_SEPARATOR +
                "   Name: John Doe" + OsUtils.LINE_SEPARATOR +
                "   Position: null" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test
    public void createLocalSettings() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("localSettings.xml")), status(HttpStatus.OK_200));
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SettingsCommands commands = new SettingsCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String charset = null;
        String numberOfDecimals = null;
        String verbose = null;
        String verboseExceptions = null;
        String person = "Jane Doe";
        String position = "CIO";
        String email = "janedoe@cio.com";
        String organization = "GeoComp";
        String city = "Plainville";
        String country = "Canada";
        String addressType = "Work";
        boolean result = commands.createLocal(workspace, charset, numberOfDecimals, verbose, verboseExceptions, person, position, email, organization, city, country, addressType);
        assertTrue(result);
        String actual = server.getCalls().get(1).getPostBody();
        String expected = "<settings><id>SettingsInfoImpl-4e79191a:13f122c7efe:-7ffc</id>" +
                "<workspace><name>topp</name></workspace><contact><id>contact</id><addressType>Work</addressType>" +
                "<contactOrganization>GeoComp</contactOrganization><contactPerson>Jane Doe</contactPerson>" +
                "<contactPosition>CIO</contactPosition><contactEmail>janedoe@cio.com</contactEmail>" +
                "<addressCity>Plainville</addressCity><addressCountry>Canada</addressCountry></contact>" +
                "<charset>UTF-8</charset><numDecimals>4</numDecimals><verbose>false</verbose>" +
                "<verboseExceptions>false</verboseExceptions><workspace><name>topp</name></workspace></settings>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void modifyLocalSettings() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/settings.xml";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("localSettings.xml")), status(HttpStatus.OK_200));
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SettingsCommands commands = new SettingsCommands();
        commands.setGeoserver(geoserver);
        String workspace = "topp";
        String charset = null;
        String numberOfDecimals = null;
        String verbose = null;
        String verboseExceptions = null;
        String person = "Jane Doe";
        String position = "CIO";
        String email = "janedoe@cio.com";
        String organization = "GeoComp";
        String city = "Plainville";
        String country = "Canada";
        String addressType = "Work";
        boolean result = commands.createLocal(workspace, charset, numberOfDecimals, verbose, verboseExceptions, person, position, email, organization, city, country, addressType);
        assertTrue(result);
        String actual = server.getCalls().get(1).getPostBody();
        String expected = "<settings><id>SettingsInfoImpl-4e79191a:13f122c7efe:-7ffc</id>" +
                "<workspace><name>topp</name></workspace><contact><id>contact</id><addressType>Work</addressType>" +
                "<contactOrganization>GeoComp</contactOrganization><contactPerson>Jane Doe</contactPerson>" +
                "<contactPosition>CIO</contactPosition><contactEmail>janedoe@cio.com</contactEmail>" +
                "<addressCity>Plainville</addressCity><addressCountry>Canada</addressCountry></contact>" +
                "<charset>UTF-8</charset><numDecimals>4</numDecimals><verbose>false</verbose>" +
                "<verboseExceptions>false</verboseExceptions><workspace><name>topp</name></workspace></settings>";
        assertEquals(expected, actual);
        verifyHttp(server).once(method(Method.GET), uri(url));
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test
    public void deleteLocalSettings() throws Exception {
        String url = "/geoserver/rest/workspaces/topp/settings.xml";
        whenHttp(server).match(delete(url)).then(stringContent(getResourceString("contactSettings.xml")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SettingsCommands commands = new SettingsCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.deleteLocal("topp");
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }
}
