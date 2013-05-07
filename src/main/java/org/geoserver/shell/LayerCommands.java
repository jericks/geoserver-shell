package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LayerCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "layer list", help = "List layers.")
    public String list() throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTLayerList layers = reader.getLayers();
        List<String> names = layers.getNames();
        StringBuilder builder = new StringBuilder();
        for(String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "layer get", help = "Get a layer.")
    public String get(
            @CliOption(key = {"", "name"}, mandatory = true, help = "The name") String name
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTLayer layer = reader.getLayer(name);
        return layer.getName() + " " + layer.getTitle();
    }

    public void update() throws Exception {
        //GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "layer remove", help = "Remove a layer.")
    public boolean delete(
            @CliOption(key = {"", "name"}, mandatory = true, help = "The name") String name,
            @CliOption(key = {"workspace"}, mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.removeLayer(workspace, name);
    }

}
