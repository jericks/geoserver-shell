package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ShapefileCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "shapefile publish", help = "Publish a shapefile.")
    public boolean publish(
        @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
        @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
        @CliOption(key = "layer", mandatory = true, help = "The layer name") String layer,
        @CliOption(key = "file", mandatory = true, help = "The zipped shapefile") File file,
        @CliOption(key = "srs", mandatory = false, unspecifiedDefaultValue = "EPSG:4326", help = "The EPSG srs code") String srs,
        @CliOption(key = "style", mandatory = false, help = "The style") String style
    ) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.publishShp(workspace, datastore, layer, file, srs, style);
    }

}
