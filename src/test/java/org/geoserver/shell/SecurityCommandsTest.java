package org.geoserver.shell;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Test;
import org.springframework.shell.support.util.OsUtils;

import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Condition.*;
import static org.junit.Assert.assertEquals;
import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static org.junit.Assert.assertTrue;

public class SecurityCommandsTest extends BaseTest {

    /* Catalog */

    @Test public void getAclCatalog() throws Exception {
        String url = "/geoserver/rest/security/acl/catalog.json";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("acl_catalog.json")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        String result = commands.getAclCatalog();
        assertEquals("HIDE", result);
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test public void setAclCatalog() throws Exception {
        String url = "/geoserver/rest/security/acl/catalog.json";
        whenHttp(server).match(put(url)).then(stringContent(getResourceString("acl_catalog.json")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.setAclCatalog(SecurityCommands.AclCatalogMode.HIDE);
        assertTrue(result);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    /* Layers */

    @Test public void getAclLayerRules() throws Exception {
        String url = "/geoserver/rest/security/acl/layers.json";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("acl_layers.json")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        String result = commands.getAclLayerRules();
        assertTrue(result.contains("*.*.r = *"));
        assertTrue(result.contains("myworkspace.*.w = ROLE_1,ROLE_2"));
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test public void createAclLayerRule() throws Exception {
        String url = "/geoserver/rest/security/acl/layers.json";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.createAclLayerRule("topp.*.w", "USER");
        assertTrue(result);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test public void modifyAclLayerRule() throws Exception {
        String url = "/geoserver/rest/security/acl/layers.json";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.modifyAclLayerRule("topp.*.w", "USER");
        assertTrue(result);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test public void deleteAclLayerRule() throws Exception {
        String url = "/geoserver/rest/security/acl/layers/topp.*.w";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.deleteAclLayerRule("topp.*.w");
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    /* Services */

    @Test public void getAclServicesRules() throws Exception {
        String url = "/geoserver/rest/security/acl/services.json";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("acl_services.json")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        String result = commands.getAclServicesRules();
        assertTrue(result.contains("wfs.* = *"));
        assertTrue(result.contains("wfs.GetTransaction = EDITOR"));
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test public void createAclServicesRule() throws Exception {
        String url = "/geoserver/rest/security/acl/services.json";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.createAclServicesRule("wfs.GetFeature", "USER");
        assertTrue(result);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test public void modifyAclServicesRule() throws Exception {
        String url = "/geoserver/rest/security/acl/services.json";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.modifyAclServicesRule("wfs.GetFeature", "USER");
        assertTrue(result);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test public void deleteAclServicesRule() throws Exception {
        String url = "/geoserver/rest/security/acl/services/wfs.GetFeature";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.deleteAclServicesRule("wfs.GetFeature");
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

    /* Rest */

    @Test public void getAclRestRules() throws Exception {
        String url = "/geoserver/rest/security/acl/rest.json";
        whenHttp(server).match(get(url)).then(stringContent(getResourceString("acl_rest.json")), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        String result = commands.getAclRestRules();
        assertTrue(result.contains("/topp/** = POST,DELETE,PUT"));
        assertTrue(result.contains("/** = GET"));
        verifyHttp(server).once(method(Method.GET), uri(url));
    }

    @Test public void createAclRestRule() throws Exception {
        String url = "/geoserver/rest/security/acl/rest.json";
        whenHttp(server).match(post(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.createAclRestRule("/**", "GET");
        assertTrue(result);
        verifyHttp(server).once(method(Method.POST), uri(url));
    }

    @Test public void modifyAclRestRule() throws Exception {
        String url = "/geoserver/rest/security/acl/rest.json";
        whenHttp(server).match(put(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.modifyAclRestRule("/**", "GET");
        assertTrue(result);
        verifyHttp(server).once(method(Method.PUT), uri(url));
    }

    @Test public void deleteAclRestRule() throws Exception {
        String url = "/geoserver/rest/security/acl/rest/%2F**";
        whenHttp(server).match(delete(url)).then(stringContent("true"), status(HttpStatus.OK_200));
        Geoserver geoserver = new Geoserver("http://00.0.0.0:8888/geoserver", "admin", "geoserver");
        SecurityCommands commands = new SecurityCommands();
        commands.setGeoserver(geoserver);
        boolean result = commands.deleteAclRestRule("/**");
        assertTrue(result);
        verifyHttp(server).once(method(Method.DELETE), uri(url));
    }

}
