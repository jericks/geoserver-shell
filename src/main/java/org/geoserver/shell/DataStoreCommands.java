package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.decoder.RESTDataStoreList;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataStoreCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "datastore list", help = "List data stores.")
    public String list(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTDataStoreList dataStores = reader.getDatastores(workspace);
        List<String> names = dataStores.getNames();
        Collections.sort(names);
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "datastore create", help = "Create a new data store.")
    public boolean create(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "connectionParams", mandatory = true, help = "The connection parameters") String connectionParams,
            @CliOption(key = "description", mandatory = false, help = "The description") String description,
            @CliOption(key = "enabled", mandatory = false, unspecifiedDefaultValue = "true", help = "The enabled flag") boolean enabled
    ) throws Exception {
        Element dataStoreElement = new Element("dataStore");
        dataStoreElement.addContent(new Element("name").setText(name));
        if (description != null) {
            dataStoreElement.addContent(new Element("description").setText(description));
        }
        dataStoreElement.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        Map<String, String> params = getParametersFromString(connectionParams);
        Element connectionParamElement = new Element("connectionParameters");
        for (Map.Entry<String, String> param : params.entrySet()) {
            connectionParamElement.addContent(new Element(param.getKey()).setText(param.getValue()));
        }
        dataStoreElement.addContent(connectionParamElement);
        String xml = JDOMUtil.toString(dataStoreElement);
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/datastores.xml";
        String response = HTTPUtils.postXml(url, xml, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "datastore modify", help = "Create a new data store.")
    public boolean modify(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "connectionParams", mandatory = false, help = "The connection parameters") String connectionParams,
            @CliOption(key = "description", mandatory = false, help = "The description") String description,
            @CliOption(key = "enabled", mandatory = false, help = "The enabled flag") String enabled
    ) throws Exception {
        Element dataStoreElement = new Element("dataStore");
        dataStoreElement.addContent(new Element("name").setText(name));
        if (description != null) {
            dataStoreElement.addContent(new Element("description").setText(description));
        }
        if (enabled != null) {
            dataStoreElement.addContent(new Element("enabled").setText(enabled));
        }
        if (connectionParams != null) {
            Map<String, String> params = getParametersFromString(connectionParams);
            Element connectionParamElement = new Element("connectionParameters");
            for (Map.Entry<String, String> param : params.entrySet()) {
                connectionParamElement.addContent(new Element(param.getKey()).setText(param.getValue()));
            }
            dataStoreElement.addContent(connectionParamElement);
        }
        String xml = JDOMUtil.toString(dataStoreElement);
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/datastores/" + URLUtil.encode(name) + ".xml";
        String response = HTTPUtils.putXml(url, xml, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "datastore get", help = "Get a data store.")
    public String getSld(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "name", mandatory = true, help = "The name") String name
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTDataStore dataStore = reader.getDatastore(workspace, name);
        final String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(dataStore.getName()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Enabled? ").append(dataStore.isEnabled()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Description: ").append(dataStore.getDescription()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Store Type: ").append(dataStore.getStoreType()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Type: ").append(dataStore.getType()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Workspace: ").append(dataStore.getWorkspaceName()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Connection Parameters:").append(OsUtils.LINE_SEPARATOR);
        Map<String, String> params = dataStore.getConnectionParameters();
        for (Map.Entry<String, String> param : params.entrySet()) {
            builder.append(TAB).append(TAB).append(param.getKey()).append(": ").append(param.getValue()).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "datastore delete", help = "Delete an existing data store.")
    public boolean update(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "recurse", mandatory = false, help = "Whether to recursively delete all layers (true) or not (false)", unspecifiedDefaultValue = "false", specifiedDefaultValue = "false") boolean recurse
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/datastores/" + URLUtil.encode(name) + ".xml?recurse=" + recurse;
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "datastore upload", help = "Upload a File to a data store.")
    public boolean upload(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "type", mandatory = true, help = "The datastore type (shp, properties, h2, spatialite)") String type,
            @CliOption(key = "file", mandatory = true, help = "The file") File file,
            @CliOption(key = "configure", mandatory = false, help = "The configure parameter can be: first, none, all", unspecifiedDefaultValue = "first", specifiedDefaultValue = "first") String configure,
            @CliOption(key = "target", mandatory = false, help = "The target type (shp, properties, h2, spatialite)") String target,
            @CliOption(key = "update", mandatory = false, help = "The update parameter can be append or overwrite", unspecifiedDefaultValue = "append", specifiedDefaultValue = "append") String update,
            @CliOption(key = "charset", mandatory = false, help = "The charset") String charset
    ) {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/datastores/" + URLUtil.encode(name) + "/file." + type;
        url += "?configure=" + configure + "&update=" + update;
        if (target != null) {
            url += "&target=" + target;
        }
        if (charset != null) {
            url += "&charset=" + charset;
        }
        String response = HTTPUtils.put(url, file, "application/zip", geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    /**
     * Get a Map from a parameter string: "dbtype=h2 database=roads.db"
     *
     * @param str The parameter string is a space delimited collection of key=value parameters.  Use single
     *            quotes around key or values with internal spaces.
     * @return A Map of parameters
     */
    private static Map getParametersFromString(String str) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        if (str.indexOf("=") == -1) {
            if (str.endsWith(".shp")) {
                params.put("url", new File(str).getAbsoluteFile().getParentFile().toURI().toURL().toString());
            } else if (str.endsWith(".properties")) {
                String dir;
                File f = new File(str);
                if (f.exists()) {
                    dir = f.getAbsoluteFile().getParentFile().getAbsolutePath();
                } else {
                    dir = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator));
                }
                params.put("directory", dir);
            } else if (new File(str).isDirectory()) {
                params.put("url", new File(str).getAbsoluteFile().toURI().toURL().toString());
            } else {
                throw new IllegalArgumentException("Unknown Workspace parameter string: " + str);
            }
        } else {
            String[] segments = str.split("[ ]+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String segment : segments) {
                String[] parts = segment.split("=");
                String key = parts[0].trim();
                if ((key.startsWith("'") && key.endsWith("'")) ||
                        (key.startsWith("\"") && key.endsWith("\""))) {
                    key = key.substring(1, key.length() - 1);
                }
                String value = parts[1].trim();
                if ((value.startsWith("'") && value.endsWith("'")) ||
                        (value.startsWith("\"") && value.endsWith("\""))) {
                    value = value.substring(1, value.length() - 1);
                }
                if (key.equalsIgnoreCase("url")) {
                    value = new File(value).getAbsoluteFile().toURI().toURL().toString();
                }
                params.put(key, value);
            }
        }
        return params;
    }

}
