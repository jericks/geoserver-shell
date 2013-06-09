package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroupList;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.RESTPublishedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class LayerGroupCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "layer group list", help = "List layer groups.")
    public String list(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTLayerGroupList layerGroups = workspace == null ? reader.getLayerGroups() : reader.getLayerGroups(workspace);
        List<String> names = layerGroups.getNames();
        Collections.sort(names);
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "layer group add", help = "Add a layer group.")
    public boolean add(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "layers", mandatory = true, help = "The comma delimited list of layer names") String layers,
            @CliOption(key = "styles", mandatory = true, help = "The comma delimited list of style names") String styles,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        StringBuilder builder = new StringBuilder("<layerGroup>");
        builder.append("<name>").append(name).append("</name>");
        builder.append("<layers>");
        for (String layer : layers.split(",")) {
            builder.append("<layer>").append(layer).append("</layer>");
        }
        builder.append("</layers>");
        if (styles != null) {
            builder.append("<styles>");
            for (String style : styles.split(",")) {
                builder.append("<style>").append(style).append("</style>");
            }
            builder.append("</styles>");
        }
        builder.append("</layerGroup>");
        String xml = builder.toString();
        String url = geoserver.getUrl() + "/rest";
        if (workspace != null) {
            url += "/workspaces/" + URLUtil.encode(workspace);
        }
        url += "/layergroups.xml";
        String response = HTTPUtils.postXml(url, xml, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "layer group get", help = "Get layer group.")
    public String get(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTLayerGroup layerGroup = workspace == null ? layerGroup = reader.getLayerGroup(name) : reader.getLayerGroup(name, workspace);
        String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(layerGroup.getName()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Title: ").append(layerGroup.getTitle()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Abstract: ").append(layerGroup.getAbstract()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Workspace: ").append(layerGroup.getWorkspace()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Mode: ").append(layerGroup.getMode()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("CRS: ").append(layerGroup.getCRS()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Bounds: ").append(layerGroup.getMinX() + ", " + layerGroup.getMinY() + ", " + layerGroup.getMaxX() + ", " + layerGroup.getMaxY()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Layers: ").append(OsUtils.LINE_SEPARATOR);
        if (layerGroup.getLayerList() != null) {
            RESTLayerList layerList = layerGroup.getLayerList();
            List<String> names = layerList.getNames();
            for (String n : names) {
                builder.append(TAB).append(TAB).append(n).append(OsUtils.LINE_SEPARATOR);
            }
        } else {
            builder.append(TAB).append(TAB).append("None").append(OsUtils.LINE_SEPARATOR);
        }
        builder.append(TAB).append("Publishables: ").append(OsUtils.LINE_SEPARATOR);
        if (layerGroup.getPublishedList() != null) {
            RESTPublishedList layerList = layerGroup.getPublishedList();
            List<String> names = layerList.getNames();
            for (String n : names) {
                builder.append(TAB).append(TAB).append(n).append(OsUtils.LINE_SEPARATOR);
            }
        } else {
            builder.append(TAB).append(TAB).append("None").append(OsUtils.LINE_SEPARATOR);
        }
        // @TODO styles
        return builder.toString();
    }

    @CliCommand(value = "layer group modify", help = "Modify a layer group.")
    public boolean modify(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "layers", mandatory = true, help = "The comma delimited list of layer names") String layers,
            @CliOption(key = "styles", mandatory = true, help = "The comma delimited list of style names") String styles,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        StringBuilder builder = new StringBuilder("<layerGroup>");
        builder.append("<name>").append(name).append("</name>");
        builder.append("<layers>");
        for (String layer : layers.split(",")) {
            builder.append("<layer>").append(layer).append("</layer>");
        }
        builder.append("</layers>");
        if (styles != null) {
            builder.append("<styles>");
            for (String style : styles.split(",")) {
                builder.append("<style>").append(style).append("</style>");
            }
            builder.append("</styles>");
        }
        builder.append("</layerGroup>");
        String xml = builder.toString();
        String url = geoserver.getUrl() + "/rest";
        if (workspace != null) {
            url += "/workspaces/" + URLUtil.encode(workspace);
        }
        url += "/layergroups/" + URLUtil.encode(name) + ".xml";
        String response = HTTPUtils.putXml(url, xml, geoserver.getUser(), geoserver.getPassword());
        return response != null;

    }

    @CliCommand(value = "layer group delete", help = "Delete a layer group.")
    public boolean delete(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest";
        if (workspace != null) {
            url += "/workspaces/" + URLUtil.encode(workspace);
        }
        url += "/layergroups/" + URLUtil.encode(name) + ".xml";
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }
}
