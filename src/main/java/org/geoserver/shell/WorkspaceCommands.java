package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTWorkspaceList;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import it.geosolutions.geoserver.rest.encoder.GSWorkspaceEncoder;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WorkspaceCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({"workspace list", "workspace get", "workspace create", "workspace delete",
            "workspace default get", "workspace default set"})
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    @CliCommand(value = "workspace list", help = "List workspaces.")
    public String list() throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        List<String> names = reader.getWorkspaceNames();
        Collections.sort(names);
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "workspace create", help = "Create a workspace.")
    public boolean create(
            @CliOption(key = "name", mandatory = true, help = "The name") String name
    ) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.createWorkspace(name);
    }

    @CliCommand(value = "workspace get", help = "Get a workspace.")
    public String get(
            @CliOption(key = "name", mandatory = true, help = "The name") String name) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + name + ".xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element workspaceElement = JDOMBuilder.buildElement(xml);
        String nm = workspaceElement.getChildText("name");
        return nm;
    }

    @CliCommand(value = "workspace delete", help = "Delete a workspace.")
    public boolean delete(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "recurse", mandatory = false, unspecifiedDefaultValue = "false", help = "Whether to delete recursively") boolean recurse) {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.removeWorkspace(name, recurse);
    }

    @CliCommand(value = "workspace default get", help = "Get the default workspace.")
    public String getDefault() throws Exception {
        String result = HTTPUtils.get(geoserver.getUrl() + "/rest/workspaces/default.xml", geoserver.getUser(), geoserver.getPassword());
        Element elem = JDOMBuilder.buildElement(result);
        RESTWorkspaceList.RESTShortWorkspace w = new RESTWorkspaceList.RESTShortWorkspace(elem);
        return w.getName();
    }

    @CliCommand(value = "workspace default set", help = "Set the default workspace.")
    public boolean setDefault(
            @CliOption(key = "name", mandatory = true, help = "The name") String name) throws Exception {
        GSWorkspaceEncoder encoder = new GSWorkspaceEncoder(name);
        String content = encoder.toString();
        String result = HTTPUtils.putXml(geoserver.getUrl() + "/rest/workspaces/default.xml", content, geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }
}
