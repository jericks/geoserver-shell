package org.geoserver.shell;

import it.geosolutions.geoserver.rest.HTTPUtils;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.io.File;

@Component("GeoServerScriptCommands")
public class ScriptCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({
            "scripting wps list", "scripting wps get", "scripting wps create", "scripting wps modify", "scripting wps delete",
            "scripting function list", "scripting function get", "scripting function create", "scripting function modify", "scripting function delete",
            "scripting wfs tx list", "scripting wfs tx get", "scripting wfs tx create", "scripting wfs tx modify", "scripting wfs tx delete",
            "scripting app list", "scripting app get", "scripting app create", "scripting app modify", "scripting app delete",
            "scripting session list", "scripting session get", "scripting session create", "scripting session run"
    })
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    // WPS

    @CliCommand(value = "scripting wps list", help = "List WPS scripts.")
    public String listWps() throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/wps.json";
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        JSONObject scriptsObject = new JSONObject(result).getJSONObject("scripts");
        JSONArray jsonArray = scriptsObject.getJSONArray("script");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            String script = jsonArray.getJSONObject(i).getString("name");
            builder.append(script).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "scripting wps get", help = "Get a WPS script.")
    public String getWps(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name,
            @CliOption(key = "file", mandatory = false, help = "The output File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/wps/" + URLUtil.encode(name);
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        if (file != null) {
            FileUtils.write(file, result);
            return file.getAbsolutePath();
        } else {
            return result;
        }
    }

    @CliCommand(value = "scripting wps create", help = "Create a WPS script.")
    public boolean createWps(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name,
            @CliOption(key = "file", mandatory = true, help = "The script File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/wps/" + URLUtil.encode(name);
        String result = HTTPUtils.put(url, file, "plain/text", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "scripting wps modify", help = "Modify a WPS script.")
    public boolean modifyWps(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name,
            @CliOption(key = "file", mandatory = true, help = "The script File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/wps/" + URLUtil.encode(name);
        String result = HTTPUtils.put(url, file, "plain/text", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "scripting wps delete", help = "Delete a WPS script.")
    public boolean deleteWps(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/wps/" + URLUtil.encode(name);
        boolean result = HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
        return result;
    }

    // Function

    @CliCommand(value = "scripting function list", help = "List filter function scripts.")
    public String listFunctions() throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/function.json";
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        JSONObject scriptsObject = new JSONObject(result).getJSONObject("scripts");
        JSONArray jsonArray = scriptsObject.getJSONArray("script");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            String script = jsonArray.getJSONObject(i).getString("name");
            builder.append(script).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "scripting function get", help = "Get a filter function script.")
    public String getFunction(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name,
            @CliOption(key = "file", mandatory = false, help = "The output File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/function/" + URLUtil.encode(name);
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        if (file != null) {
            FileUtils.write(file, result);
            return file.getAbsolutePath();
        } else {
            return result;
        }
    }

    @CliCommand(value = "scripting function create", help = "Create a filter function script.")
    public boolean createFunction(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name,
            @CliOption(key = "file", mandatory = true, help = "The script File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/function/" + URLUtil.encode(name);
        String result = HTTPUtils.put(url, file, "plain/text", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "scripting function modify", help = "Modify a filter function script.")
    public boolean modifyFunction(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name,
            @CliOption(key = "file", mandatory = true, help = "The script File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/function/" + URLUtil.encode(name);
        String result = HTTPUtils.put(url, file, "plain/text", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "scripting function delete", help = "Delete a filter function script.")
    public boolean deleteFunction(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/function/" + URLUtil.encode(name);
        boolean result = HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
        return result;
    }

    // WFS TX

    @CliCommand(value = "scripting wfs tx list", help = "List wfs transaction scripts.")
    public String listWfsTx() throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/wfs/tx.json";
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        JSONObject scriptsObject = new JSONObject(result).getJSONObject("scripts");
        JSONArray jsonArray = scriptsObject.getJSONArray("script");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            String script = jsonArray.getJSONObject(i).getString("name");
            builder.append(script).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "scripting wfs tx get", help = "Get a wfs transaction script.")
    public String getWfsTx(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name,
            @CliOption(key = "file", mandatory = true, help = "The script File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/wfs/tx/" + URLUtil.encode(name);
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        if (file != null) {
            FileUtils.write(file, result);
            return file.getAbsolutePath();
        } else {
            return result;
        }
    }

    @CliCommand(value = "scripting wfs tx create", help = "Create a wfs transaction script.")
    public boolean createWfsTx(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name,
            @CliOption(key = "file", mandatory = true, help = "The script File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/wfs/tx/" + URLUtil.encode(name);
        String result = HTTPUtils.put(url, file, "plain/text", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "scripting wfs tx modify", help = "Modify a wfs transaction script.")
    public boolean modifyWfsTx(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name,
            @CliOption(key = "file", mandatory = true, help = "The script File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/wfs/tx/" + URLUtil.encode(name);
        String result = HTTPUtils.put(url, file, "plain/text", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "scripting wfs tx delete", help = "Delete a wfs transaction script.")
    public boolean deleteWfsTx(
            @CliOption(key = "name", mandatory = true, help = "The script name") String name
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/wfs/tx/" + URLUtil.encode(name);
        boolean result = HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
        return result;
    }

    // App

    @CliCommand(value = "scripting app list", help = "List scripting apps.")
    public String listApps() throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/apps.json";
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        JSONObject scriptsObject = new JSONObject(result).getJSONObject("scripts");
        JSONArray jsonArray = scriptsObject.getJSONArray("script");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            String script = jsonArray.getJSONObject(i).getString("name");
            builder.append(script).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "scripting app get", help = "Get an app script.")
    public String getApp(
            @CliOption(key = "name", mandatory = true, help = "The app name") String name,
            @CliOption(key = "type", mandatory = true, help = "The app type (groovy, js, py)") String type,
            @CliOption(key = "file", mandatory = false, help = "The output File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/apps/" + URLUtil.encode(name) + "/main." + type;
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        if (file != null) {
            FileUtils.write(file, result);
            return file.getAbsolutePath();
        } else {
            return result;
        }
    }

    @CliCommand(value = "scripting app create", help = "Create a script app.")
    public boolean createApp(
            @CliOption(key = "name", mandatory = true, help = "The app name") String name,
            @CliOption(key = "type", mandatory = true, help = "The app type (groovy, js, py)") String type,
            @CliOption(key = "file", mandatory = true, help = "The script File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/apps/" + URLUtil.encode(name) + "/main." + type;
        String result = HTTPUtils.put(url, file, "plain/text", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "scripting app modify", help = "Modify a script app.")
    public boolean modifyApp(
            @CliOption(key = "name", mandatory = true, help = "The app name") String name,
            @CliOption(key = "type", mandatory = true, help = "The app type (groovy, js, py)") String type,
            @CliOption(key = "file", mandatory = true, help = "The script File") File file
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/apps/" + URLUtil.encode(name) + "/main." + type;
        String result = HTTPUtils.put(url, file, "plain/text", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "scripting app delete", help = "Delete a script app.")
    public boolean deleteApp(
            @CliOption(key = "name", mandatory = true, help = "The app name") String name,
            @CliOption(key = "type", mandatory = true, help = "The app type (groovy, js, py)") String type
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/scripts/apps/" + URLUtil.encode(name) + "/main." + type;
        boolean result = HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
        return result;
    }

    // Session

    @CliCommand(value = "scripting session list", help = "List scripting sessions.")
    public String listSession(
            @CliOption(key = "ext", mandatory = false, help = "The script extension") String ext
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/sessions";
        if (ext != null) {
            url += "/" + URLUtil.encode(ext);
        }
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        JSONObject rootJsonObject = new JSONObject(result);
        JSONArray jsonArray = rootJsonObject.getJSONArray("sessions");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            builder.append(jsonObject.getString("id")).append(" ")
                    .append(jsonObject.get("engine"))
                    .append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "scripting session get", help = "Get a scripting script.")
    public String getSession(
            @CliOption(key = "ext", mandatory = true, help = "The script extension") String ext,
            @CliOption(key = "name", mandatory = true, help = "The name") String name
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/sessions/" + URLUtil.encode(ext) + "/" + URLUtil.encode(name);
        String result = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        JSONObject jsonObject = new JSONObject(result);
        StringBuilder builder = new StringBuilder();
        builder.append(jsonObject.getString("id")).append(" ")
                .append(jsonObject.get("engine"))
                .append(OsUtils.LINE_SEPARATOR);
        return builder.toString();
    }

    @CliCommand(value = "scripting session create", help = "Create a scripting session.")
    public String createSession(
            @CliOption(key = "ext", mandatory = true, help = "The script extension") String ext
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/sessions/" + URLUtil.encode(ext);
        String result = HTTPUtils.post(url, "", "", geoserver.getUser(), geoserver.getPassword());
        return result;
    }

    @CliCommand(value = "scripting session run", help = "Run a script in a scripting session.")
    public String runSession(
            @CliOption(key = "ext", mandatory = true, help = "The script extension") String ext,
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "script", mandatory = true, help = "The script") String script
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/sessions/" + URLUtil.encode(ext) + "/" + URLUtil.encode(name);
        try {
            File file = new File(script);
            if (file.exists()) {
                script = FileUtils.readFileToString(file);
            }
        } catch (Exception ex) {
            // Do nothing, just treat script as text
        }
        String result = HTTPUtils.put(url, script, "text/plain", geoserver.getUser(), geoserver.getPassword());
        return result;
    }

}
