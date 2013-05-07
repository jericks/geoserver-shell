package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.decoder.RESTDataStoreList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataStoreCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "datastore list", help = "List data stores.")
    public String list(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTDataStoreList dataStores = reader.getDatastores(workspace);
        List<String> names = dataStores.getNames();
        StringBuilder builder = new StringBuilder();
        for(String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "datastore get", help = "Get a data store.")
    public String getSld(
            @CliOption(key = {"", "name"}, mandatory = true, help = "The name") String name,
            @CliOption(key = {"workspace"}, mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTDataStore dataStore = reader.getDatastore(workspace, name);
        return dataStore.getName() + " " + dataStore.getConnectionParameters();
    }



    /*@CliCommand(value = "datastore create-shp", help = "Create a shapefile data store.")
    public boolean create(
            String workspace,
            String storename, String datasetname, File zipFile
    ) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        publisher.publishShp()
    }*/

}
