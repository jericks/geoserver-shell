package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStoreList;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class CoverageStoreCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "coverage store list", help = "List coverage store.")
    public String list(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTCoverageStoreList list = reader.getCoverageStores(workspace);
        List<String> names = list.getNames();
        StringBuilder builder = new StringBuilder();
        for(String name : names) {
            builder.append(name).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "coverage store get", help = "Get a coverage store.")
    public String get(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coveragestore") String coveragestore
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/coveragestores/" + coveragestore + ".xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element coverageStoreElement = JDOMBuilder.buildElement(xml);
        String name = coverageStoreElement.getChildText("name");
        String type = coverageStoreElement.getChildText("type");
        String enabled = coverageStoreElement.getChildText("enabled");
        // @TODO RESTCoverageStore doesn't have access to type, enabled
        /*GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTCoverageStore store = reader.getCoverageStore(workspace, coveragestore);*/
        String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(name).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Type: ").append(type).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Enabled: ").append(enabled).append(OsUtils.LINE_SEPARATOR);
        return builder.toString();
    }

    @CliCommand(value = "coverage store delete", help = "Delete a coverage store.")
    public boolean delete(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coveragestore") String coveragestore,
            @CliOption(key = "recurse", mandatory = false, help = "Whether to delete all associated layers", unspecifiedDefaultValue = "false", specifiedDefaultValue = "false") boolean recurse
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/coveragestores/" + coveragestore + ".xml?recurse=" + recurse;
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }

    // @TODO Implement me
    @CliCommand(value = "coverage store modify", help = "Modify a coverage store.")
    public boolean modify(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coveragestore") String coveragestore
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/coveragestores/" + coveragestore + ".xml";
        String content = "";
        String contentType = GeoServerRESTPublisher.Format.XML.getContentType();
        String response  = HTTPUtils.put(url, content, contentType, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    // @TODO Implement me
    @CliCommand(value = "coverage store add", help = "Add a coverage store.")
    public boolean add(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/coveragestores.xml";
        String content = "";
        String contentType = GeoServerRESTPublisher.Format.XML.getContentType();
        String response  = HTTPUtils.post(url, content, contentType, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    // @TODO Implement me
    @CliCommand(value = "coverage store upload", help = "Upload a file to create coverage store.")
    public boolean upload(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coveragestore") String coveragestore,
            @CliOption(key = "file", mandatory = true, help = "The file") File file,
            @CliOption(key = "type", mandatory = true, help = "The type (geotiff, worldimage, or imagemosaic)") String type,
            @CliOption(key = "configure", mandatory = true, help = "How to configure (first, none, all)", unspecifiedDefaultValue = "first") String configure
    ) throws Exception {
        ///workspaces/<ws>/coveragestores/<cs>/file[.<extension>
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/coveragestores/" + coveragestore + "/file." + type + "?configure=" + configure;
        return false;
    }

}

