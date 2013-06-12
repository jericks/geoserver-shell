package org.geoserver.shell;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import org.springframework.shell.support.util.OsUtils;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.get;
import static com.xebialabs.restito.semantics.Condition.method;
import static com.xebialabs.restito.semantics.Condition.uri;
import static junit.framework.Assert.assertEquals;

public class FontCommandsTest extends BaseTest {

    @Test
    public void fontList() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/fonts.xml")).then(stringContent(getResource("fonts.xml")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        FontCommands commands = new FontCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list(null);
        String expected = "Bordeaux Roman Bold LET" + OsUtils.LINE_SEPARATOR + 
                "Bradley Hand ITC TT" + OsUtils.LINE_SEPARATOR + 
                "Didot-Bold" + OsUtils.LINE_SEPARATOR + 
                "GillSans-Light" + OsUtils.LINE_SEPARATOR + 
                "HiraMinPro-W3" + OsUtils.LINE_SEPARATOR + 
                "LiHeiPro" + OsUtils.LINE_SEPARATOR + 
                "Lucida Bright Demibold Italic" + OsUtils.LINE_SEPARATOR + 
                "LucidaSans" + OsUtils.LINE_SEPARATOR + 
                "Palatino" + OsUtils.LINE_SEPARATOR + 
                "Skia-Regular_Light-Condensed" + OsUtils.LINE_SEPARATOR + 
                "StoneSansITCTT-SemiIta" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/fonts.xml"));
    }

    @Test
    public void fontListWithSearch() throws Exception {
        whenHttp(server).match(get("/geoserver/rest/fonts.xml")).then(stringContent(getResource("fonts.xml")), status(HttpStatus.OK_200));

        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        FontCommands commands = new FontCommands();
        commands.setGeoserver(geoserver);
        String actual = commands.list("Lu");
        String expected =  "Lucida Bright Demibold Italic" + OsUtils.LINE_SEPARATOR +
                "LucidaSans" + OsUtils.LINE_SEPARATOR;
        assertEquals(expected, actual);

        verifyHttp(server).once(method(Method.GET), uri("/geoserver/rest/fonts.xml"));
    }

}
