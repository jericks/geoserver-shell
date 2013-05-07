package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroupList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LayerGroupCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "layergroup list", help = "List layer groups.")
    public String list(
        @CliOption(key = {"workspace"}, mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTLayerGroupList layerGroups = workspace == null ? reader.getLayerGroups() : reader.getLayerGroups(workspace);
        List<String> names = layerGroups.getNames();
        StringBuilder builder = new StringBuilder();
        for(String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    public void add() throws Exception {
    }

    @CliCommand(value = "layergroup get", help = "Get layer group.")
    public String get(
            @CliOption(key = {"", "name"}, mandatory = true, help = "The name") String name,
            @CliOption(key = {"workspace"}, mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTLayerGroup layerGroup = workspace == null ? layerGroup = reader.getLayerGroup(name) : reader.getLayerGroup(name, workspace);
        return layerGroup.getName() + " " + layerGroup.getLayerList();
    }

    public void update(String name) throws Exception {
    }

    public void delete(String name) throws Exception {
    }


}
