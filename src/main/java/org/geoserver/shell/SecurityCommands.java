package org.geoserver.shell;

import it.geosolutions.geoserver.rest.HTTPUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class SecurityCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({
            "security acl get", "security acl set",
            "security acl layers get", "security acl layers create", "security acl layers modify", "security acl layers delete",
            "security acl services get", "security acl services create", "security acl services modify", "security acl services delete",
            "security acl rest get", "security acl rest create", "security acl rest modify", "security acl rest delete"
    })
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    /* Catalog */

    @CliCommand(value = "security acl get", help = "Get the security acl catalog mode.")
    public String getAclCatalog() throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/catalog.json";
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        String mode = new JSONObject(result).getString("mode");
        return mode;
    }

    @CliCommand(value = "security acl set", help = "Set the security acl catalog mode.")
    public boolean setAclCatalog(
            @CliOption(key = "mode", mandatory = true, help = "The catalog mode") AclCatalogMode mode
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/catalog.json";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mode", mode.toString());
        String result = HTTPUtils.put(url, jsonObject.toString(), "application/json", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    public static enum AclCatalogMode {
        HIDE,
        MIXED,
        CHALLENGE;
    }

    /* Layers */

    @CliCommand(value = "security acl layers get", help = "Get the security acl layer rules.")
    public String getAclLayerRules() throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/layers.json";
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        JSONObject jsonObject= new JSONObject(result);
        Iterator<String> it = jsonObject.keys();
        StringBuilder builder = new StringBuilder();
        while(it.hasNext()) {
            String key = it.next();
            builder.append(key).append(" = ").append(jsonObject.getString(key)).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "security acl layers create", help = "Create a security acl layer rule.")
    public boolean createAclLayerRule(
            @CliOption(key = "resource", mandatory = true, help = "The resource") String resource,
            @CliOption(key = "role", mandatory = true, help = "The role") String role
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/layers.json";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(resource, role);
        String result = HTTPUtils.post(url, jsonObject.toString(), "application/json", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "security acl layers modify", help = "Modify a security acl layer rule.")
    public boolean modifyAclLayerRule(
            @CliOption(key = "resource", mandatory = true, help = "The resource") String resource,
            @CliOption(key = "role", mandatory = true, help = "The role") String role
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/layers.json";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(resource, role);
        String result = HTTPUtils.put(url, jsonObject.toString(), "application/json", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "security acl layers delete", help = "Delete a security acl layer rule.")
    public boolean deleteAclLayerRule(
            @CliOption(key = "resource", mandatory = true, help = "The resource") String resource
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/layers/" + resource.replaceAll("/","%2F");
        boolean result = HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
        return result;
    }

    /* Services */

    @CliCommand(value = "security acl services get", help = "Get the security acl layer rules.")
    public String getAclServicesRules() throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/services.json";
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        JSONObject jsonObject= new JSONObject(result);
        Iterator<String> it = jsonObject.keys();
        StringBuilder builder = new StringBuilder();
        while(it.hasNext()) {
            String key = it.next();
            builder.append(key).append(" = ").append(jsonObject.getString(key)).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "security acl services create", help = "Create a security acl layer rule.")
    public boolean createAclServicesRule(
            @CliOption(key = "resource", mandatory = true, help = "The resource") String resource,
            @CliOption(key = "role", mandatory = true, help = "The role") String role
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/services.json";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(resource, role);
        String result = HTTPUtils.post(url, jsonObject.toString(), "application/json", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "security acl services modify", help = "Modify a security acl layer rule.")
    public boolean modifyAclServicesRule(
            @CliOption(key = "resource", mandatory = true, help = "The resource") String resource,
            @CliOption(key = "role", mandatory = true, help = "The role") String role
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/services.json";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(resource, role);
        String result = HTTPUtils.put(url, jsonObject.toString(), "application/json", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "security acl services delete", help = "Delete a security acl layer rule.")
    public boolean deleteAclServicesRule(
            @CliOption(key = "resource", mandatory = true, help = "The resource") String resource
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/services/" + resource.replaceAll("/","%2F");
        boolean result = HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
        return result;
    }

    /* Rest */

    @CliCommand(value = "security acl rest get", help = "Get the security acl layer rules.")
    public String getAclRestRules() throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/rest.json";
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        JSONObject jsonObject= new JSONObject(result);
        Iterator<String> it = jsonObject.keys();
        StringBuilder builder = new StringBuilder();
        while(it.hasNext()) {
            String key = it.next();
            builder.append(key).append(" = ").append(jsonObject.getString(key)).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "security acl rest create", help = "Create a security acl layer rule.")
    public boolean createAclRestRule(
            @CliOption(key = "resource", mandatory = true, help = "The resource") String resource,
            @CliOption(key = "role", mandatory = true, help = "The role") String role
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/rest.json";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(resource, role);
        String result = HTTPUtils.post(url, jsonObject.toString(), "application/json", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "security acl rest modify", help = "Modify a security acl layer rule.")
    public boolean modifyAclRestRule(
            @CliOption(key = "resource", mandatory = true, help = "The resource") String resource,
            @CliOption(key = "role", mandatory = true, help = "The role") String role
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/rest.json";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(resource, role);
        String result = HTTPUtils.put(url, jsonObject.toString(), "application/json", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "security acl rest delete", help = "Delete a security acl layer rule.")
    public boolean deleteAclRestRule(
            @CliOption(key = "resource", mandatory = true, help = "The resource") String resource
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/security/acl/rest/" + resource.replaceAll("/","%2F");
        boolean result = HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
        return result;
    }
}
