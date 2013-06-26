package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStoreList;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Component
public class CoverageStoreCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({"coverage store list", "coverage store get", "coverage store create",
            "coverage store modify", "coverage store delete", "coverage store upload"})
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    @CliCommand(value = "coverage store list", help = "List coverage store.")
    public String list(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTCoverageStoreList list = reader.getCoverageStores(workspace);
        List<String> names = list.getNames();
        Collections.sort(names);
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "coverage store get", help = "Get a coverage store.")
    public String get(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coveragestore") String coveragestore
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/coveragestores/" + URLUtil.encode(coveragestore) + ".xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element coverageStoreElement = JDOMBuilder.buildElement(xml);
        String name = coverageStoreElement.getChildText("name");
        String type = coverageStoreElement.getChildText("type");
        String enabled = coverageStoreElement.getChildText("enabled");
        String covUrl = coverageStoreElement.getChildText("url");
        // @TODO RESTCoverageStore doesn't have access to type, enabled
        /*GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTCoverageStore store = reader.getCoverageStore(workspace, coveragestore);*/
        String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(name).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Type: ").append(type).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("URL: ").append(covUrl).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Enabled: ").append(enabled).append(OsUtils.LINE_SEPARATOR);
        return builder.toString();
    }

    @CliCommand(value = "coverage store delete", help = "Delete a coverage store.")
    public boolean delete(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coveragestore") String coveragestore,
            @CliOption(key = "recurse", mandatory = false, help = "Whether to delete all associated layers", unspecifiedDefaultValue = "false", specifiedDefaultValue = "false") boolean recurse
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/coveragestores/" + URLUtil.encode(coveragestore) + ".xml?recurse=" + recurse;
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "coverage store modify", help = "Modify a coverage store.")
    public boolean modify(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coveragestore") String coveragestore,
            @CliOption(key = "name", mandatory = false, help = "The name") String name,
            @CliOption(key = "type", mandatory = false, help = "The type") String type,
            @CliOption(key = "url", mandatory = false, help = "The file url") String fileUrl,
            @CliOption(key = "enabled", mandatory = false, help = "The enabled flag") String enabled
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/coveragestores/" + URLUtil.encode(coveragestore) + ".xml";
        Element element = new Element("coverageStore");
        if (name != null) element.addContent(new Element("name").setText(name));
        if (type != null) element.addContent(new Element("type").setText(type));
        if (fileUrl != null) element.addContent(new Element("url").setText(fileUrl));
        if (enabled != null) element.addContent(new Element("enabled").setText(enabled));
        element.addContent(new Element("workspace").addContent(new Element("name").setText(workspace)));
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "coverage store create", help = "Create a coverage store.")
    public boolean create(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "type", mandatory = true, help = "The type") String type,
            @CliOption(key = "url", mandatory = true, help = "The file url") String fileUrl,
            @CliOption(key = "enabled", mandatory = false, unspecifiedDefaultValue = "true", help = "The enabled flag") boolean enabled
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/coveragestores.xml";
        Element element = new Element("coverageStore");
        element.addContent(new Element("name").setText(name));
        element.addContent(new Element("type").setText(type));
        element.addContent(new Element("url").setText(fileUrl));
        element.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        element.addContent(new Element("workspace").addContent(new Element("name").setText(workspace)));
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.postXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "coverage store upload", help = "Upload a file to create coverage store.")
    public boolean upload(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coveragestore") String coveragestore,
            @CliOption(key = "file", mandatory = true, help = "The file") File file,
            @CliOption(key = "type", mandatory = true, help = "The type (geotiff, worldimage, or imagemosaic)") String type,
            @CliOption(key = "configure", mandatory = false, help = "How to configure (first, none, all)", unspecifiedDefaultValue = "first") String configure,
            @CliOption(key = "coverage", mandatory = false, help = "The name of the coverage") String coverageName,
            @CliOption(key = "recalculate", mandatory = false, help = "How to recalculate bbox (nativebbox,latlonbbox)") String recalculate
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/coveragestores/" + URLUtil.encode(coveragestore) + "/file." + type + "?configure=" + configure;
        if (coverageName != null) {
            url += "&coverageName=" + coverageName;
        }
        if (recalculate != null) {
            url += "&recalculate=" + recalculate;
        }
        String contentType;
        if (type.equalsIgnoreCase("geotiff")) {
            contentType = "image/tiff";
        } else {
            contentType = "application/zip";
        }
        String response = HTTPUtils.put(url, file, contentType, geoserver.getUser(), geoserver.getPassword());
        if (geoserver.isVerbose()) {
            System.out.println("URL: " + url);
            System.out.println("Content Type: " + contentType);
            System.out.println("File: " + file.getAbsolutePath());
            System.out.println("Response: " + response);
        }
        return response != null;
    }
}

