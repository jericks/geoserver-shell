package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTNamespace;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Component
public class NamespaceCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliCommand(value = "namespace list", help = "List namespaces.")
    public String list() throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        List<String> names = reader.getNamespaceNames();
        Collections.sort(names);
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "namespace create", help = "Create a namespace.")
    public boolean create(
            @CliOption(key = "prefix", mandatory = true, help = "The prefix") String prefix,
            @CliOption(key = "uri", mandatory = true, help = "The uri") String uri) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.createNamespace(prefix, URI.create(uri));
    }

    @CliCommand(value = "namespace get", help = "Get a namespace.")
    public String get(
            @CliOption(key = "prefix", mandatory = true, help = "The prefix") String prefix) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTNamespace nm = reader.getNamespace(prefix);
        StringBuilder builder = new StringBuilder();
        builder.append(nm.getPrefix()).append(OsUtils.LINE_SEPARATOR);
        builder.append(nm.getURI());
        return builder.toString();
    }

    @CliCommand(value = "namespace modify", help = "Modify a namespace.")
    public boolean update(
            @CliOption(key = "prefix", mandatory = true, help = "The prefix") String prefix,
            @CliOption(key = "uri", mandatory = true, help = "The uri") String uri) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.updateNamespace(prefix, URI.create(uri));
    }

    @CliCommand(value = "namespace delete", help = "Delete a namespace.")
    public boolean delete(
            @CliOption(key = "prefix", mandatory = true, help = "The prefix") String prefix,
            @CliOption(key = "recurse", mandatory = false, unspecifiedDefaultValue = "false", help = "Whether to delete recursively") boolean recurse) {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.removeNamespace(prefix, recurse);
    }

    @CliCommand(value = "namespace default get", help = "Get the default namespace.")
    public String getDefault() throws Exception {
        String result = HTTPUtils.get(geoserver.getUrl() + "/rest/namespaces/default.xml", geoserver.getUser(), geoserver.getPassword());
        Element elem = JDOMBuilder.buildElement(result);
        StringBuilder builder = new StringBuilder();
        builder.append(elem.getChildText("prefix")).append(OsUtils.LINE_SEPARATOR);
        builder.append(elem.getChildText("uri"));
        return builder.toString();
    }

    @CliCommand(value = "namespace default set", help = "Set the default namespace.")
    public boolean setDefault(
            @CliOption(key = "prefix", mandatory = true, help = "The prefix") String prefix) throws Exception {
        String content = "<namespace><prefix>" + prefix + "</prefix></namespace>";
        String result = HTTPUtils.putXml(geoserver.getUrl() + "/rest/namespaces/default.xml", content, geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }
}
