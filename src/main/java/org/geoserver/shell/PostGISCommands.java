package org.geoserver.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class PostGISCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({"postgis datastore create", "postgis featuretype publish"})
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    @CliCommand(value = "postgis datastore create", help = "Create a PostGIS DataStore.")
    public boolean createDataStore(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "host", mandatory = true, help = "The host") String host,
            @CliOption(key = "port", mandatory = false, unspecifiedDefaultValue = "5432", help = "The port") String port,
            @CliOption(key = "database", mandatory = true, help = "The database name") String database,
            @CliOption(key = "schema", mandatory = false, unspecifiedDefaultValue = "public", help = "The schema") String schema,
            @CliOption(key = "user", mandatory = true, help = "The user name") String user,
            @CliOption(key = "password", mandatory = true, help = "The password") String password
    ) throws Exception {
        StringBuilder connectionStringBuilder = new StringBuilder();
        connectionStringBuilder.append("dbtype=postgis").append(" ");
        connectionStringBuilder.append("host=").append(host).append(" ");
        connectionStringBuilder.append("port=").append(port).append(" ");
        connectionStringBuilder.append("database='").append(database).append("' ");
        connectionStringBuilder.append("schema='").append(schema).append("' ");
        connectionStringBuilder.append("user='").append(user).append("' ");
        connectionStringBuilder.append("password='").append(password).append("'");
        DataStoreCommands dataStoreCommands = new DataStoreCommands();
        dataStoreCommands.setGeoserver(geoserver);
        return dataStoreCommands.create(workspace, datastore, connectionStringBuilder.toString(), null, true);
    }

    @CliCommand(value = "postgis featuretype publish", help = "Publish a PostGIS Table.")
    public boolean publishLayer(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "table", mandatory = true, help = "The table") String table
    ) throws Exception {
        FeatureTypeCommands featureTypeCommands = new FeatureTypeCommands();
        featureTypeCommands.setGeoserver(geoserver);
        return featureTypeCommands.publish(workspace, datastore, table);
    }
}
