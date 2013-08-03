package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class GeoserverCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({"geoserver reset", "geoserver reload", "geoserver backup",
            "geoserver restore", "geoserver verbose set", "geoserver show"})
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    @CliCommand(value = "geoserver set", help = "Set the url, user, and password for Geoserver.")
    public void set(
            @CliOption(key = "url", mandatory = true, help = "The url") String url,
            @CliOption(key = "user", mandatory = false, help = "The user name", unspecifiedDefaultValue = "admin") String user,
            @CliOption(key = "password", mandatory = false, help = "The password", unspecifiedDefaultValue = "geoserver") String password
    ) {
        geoserver.setUrl(url);
        geoserver.setUser(user);
        geoserver.setPassword(password);
    }

    @CliCommand(value = "geoserver verbose set", help = "Show the url, user, and password for Geoserver.")
    public boolean verbose(@CliOption(key = "value", mandatory = true, help = "The verbosity value") boolean verbose) {
        geoserver.setVerbose(verbose);
        return true;
    }

    @CliCommand(value = "geoserver show", help = "Show the url, user, and password for Geoserver.")
    public String show() {
        return geoserver.getUrl() + " " + geoserver.getUser() + " " + geoserver.getPassword();
    }

    @CliCommand(value = "geoserver reset", help = "Reset Geoserver's configuration.")
    public boolean reset() {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.reset();
    }

    @CliCommand(value = "geoserver reload", help = "Reload Geoserver's configuration.")
    public boolean reload() {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.reload();
    }

    @CliCommand(value = "geoserver backup", help = "Backup Geoserver's configuration.")
    public boolean backup(
            @CliOption(key = "directory", mandatory = true, help = "The backup ") String backupDir,
            @CliOption(key = "includedata", mandatory = false, unspecifiedDefaultValue = "false", help = "The include data flag") boolean includeData,
            @CliOption(key = "includegwc", mandatory = false, unspecifiedDefaultValue = "false", help = "The include GWC flag") boolean includeGwc,
            @CliOption(key = "includelog", mandatory = false, unspecifiedDefaultValue = "false", help = "The include log files flag") boolean includeLog
    ) {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        String result = publisher.backup(backupDir, includeData, includeGwc, includeLog);
        return result != null;
    }

    @CliCommand(value = "geoserver restore", help = "Restore Geoserver's configuration from a backup directory.")
    public boolean restore(
            @CliOption(key = "directory", mandatory = true, help = "The backup ") String backupDir
    ) {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        String result = publisher.restore(backupDir);
        return result != null;
    }
}