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

public class AboutCommandsTest extends BaseTest {

    @Test
    public void versionList() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/about/versions.xml")).then(stringContent(getResourceString("version.xml")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        AboutCommands commands = new AboutCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.versionList();
        String expected = "GeoServer" + OsUtils.LINE_SEPARATOR +
                "   Version: 2.3.1" + OsUtils.LINE_SEPARATOR +
                "   Build Time: 20-Apr-2013 13:14" + OsUtils.LINE_SEPARATOR +
                "   Git Revision: 24d33ae18298272654ae86a981e4815f7f935f1d" + OsUtils.LINE_SEPARATOR +
                OsUtils.LINE_SEPARATOR +
                "GeoTools" + OsUtils.LINE_SEPARATOR +
                "   Version: 9.1" + OsUtils.LINE_SEPARATOR +
                "   Build Time: 20-Apr-2013 11:03" + OsUtils.LINE_SEPARATOR +
                "   Git Revision: f25b08094d1bf7e0949994f4971bc21fb117d37b" + OsUtils.LINE_SEPARATOR +
                OsUtils.LINE_SEPARATOR +
                "GeoWebCache" + OsUtils.LINE_SEPARATOR +
                "   Version: 1.4-SNAPSHOT" + OsUtils.LINE_SEPARATOR +
                "   Build Time: null" + OsUtils.LINE_SEPARATOR +
                "   Git Revision: c29e8dd283d29c764dd497bb84172d33fbcdd175/c29e8dd283d29c764dd497bb84172d33fbcdd175" + OsUtils.LINE_SEPARATOR
                + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/about/versions.xml"));
    }

    @Test
    public void manifestList() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/about/manifests.xml")).then(stringContent(getResourceString("manifests.xml")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        AboutCommands commands = new AboutCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.manifestList();
        String expected = "gt-xsd-gml3-9.1" + OsUtils.LINE_SEPARATOR + "gt-xsd-ows-9.1" + OsUtils.LINE_SEPARATOR + "gt-xsd-sld-9.1" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/about/manifests.xml"));
    }

    @Test
    public void manifestGet() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/about/manifests.xml")).then(stringContent(getResourceString("manifests.xml")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        AboutCommands commands = new AboutCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.manifestGet("gt-xsd-ows-9.1");
        String expected = "gt-xsd-ows-9.1" + OsUtils.LINE_SEPARATOR +
                "   Manifest-Version: 1.0" + OsUtils.LINE_SEPARATOR +
                "   Archiver-Version: Plexus Archiver" + OsUtils.LINE_SEPARATOR +
                "   Built-By: jetty" + OsUtils.LINE_SEPARATOR +
                "   Build-Timestamp: 20-Apr-2013 11:03" + OsUtils.LINE_SEPARATOR +
                "   Git-Revision: f25b08094d1bf7e0949994f4971bc21fb117d37b" + OsUtils.LINE_SEPARATOR +
                "   Build-Jdk: 1.6.0_22" + OsUtils.LINE_SEPARATOR +
                "   Project-Version: 9.1" + OsUtils.LINE_SEPARATOR +
                "   Created-By: Apache Maven" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/about/manifests.xml"));
    }

}
