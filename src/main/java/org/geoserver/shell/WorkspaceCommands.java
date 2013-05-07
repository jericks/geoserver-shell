package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTNamespace;
import it.geosolutions.geoserver.rest.decoder.RESTWorkspaceList;
import it.geosolutions.geoserver.rest.encoder.GSWorkspaceEncoder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.net.URI;
import java.util.List;

@Component
public class WorkspaceCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "workspace list", help = "List workspaces.")
    public String list() throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        List<String> names = reader.getWorkspaceNames();
        StringBuilder builder = new StringBuilder();
        for(String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "workspace create", help = "Create a workspace.")
    public void create(
            @CliOption(key = {"", "name"}, mandatory = true, help = "The name") String name,
            @CliOption(key = {"uri"}, mandatory = true, help = "The uri") String uri) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        publisher.createWorkspace(name, URI.create(uri));
    }

    @CliCommand(value = "workspace get", help = "Get a workspace.")
    public String get(
            @CliOption(key = {"", "name"}, mandatory = true, help = "The name") String name) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTNamespace nm = reader.getNamespace(name);
        return nm.getPrefix() + " " + nm.getURI();
    }

    @CliCommand(value = "workspace edit", help = "Edt a workspace.")
    public boolean update(
            @CliOption(key = {"", "name"}, mandatory = true, help = "The name") String name,
            @CliOption(key = {"uri"}, mandatory = true, help = "The uri") String uri) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.updateNamespace(name, URI.create(uri));
    }

    @CliCommand(value = "workspace delete", help = "Delete a workspace.")
    public boolean delete(
            @CliOption(key = {"", "name"}, mandatory = true, help = "The name") String name,
            @CliOption(key = {"recurse"}, mandatory = false, unspecifiedDefaultValue = "false", help = "Whether to delete recursively") boolean recurse) {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.removeWorkspace(name, recurse);
    }

    @CliCommand(value = "workspace getdefault", help = "Get the default workspace.")
    public String getDefault() throws Exception {
        String result = HTTPUtils.get(geoserver.getUrl() + "/rest/workspaces/default.xml", geoserver.getUser(), geoserver.getPassword());
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(result));
        Element elem = document.getRootElement();
        RESTWorkspaceList.RESTShortWorkspace w = new RESTWorkspaceList.RESTShortWorkspace(elem);
        return w.getName();
    }

    @CliCommand(value = "workspace setdefault", help = "Set the default workspace.")
    public void setDefault(
            @CliOption(key = {"", "name"}, mandatory = true, help = "The name") String name) throws Exception {
        GSWorkspaceEncoder encoder = new GSWorkspaceEncoder(name);
        String content = encoder.toString();
        String result = HTTPUtils.putXml(geoserver.getUrl() + "/rest/workspaces/default.xml", content, geoserver.getUser(), geoserver.getPassword());
    }
}
