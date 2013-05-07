package org.geoserver.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class GeoserverCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "geoserver set", help = "Set the url, user, and password for Geoserver.")
    public void set(
        @CliOption(key = {"","url"}, mandatory = true, help = "The url") String url,
        @CliOption(key = {"user"}, mandatory = false, help = "The url", unspecifiedDefaultValue = "admin") String user,
        @CliOption(key = {"password"}, mandatory = false, help = "The url", unspecifiedDefaultValue = "geoserver") String password
    ) {
        geoserver.setUrl(url);
        geoserver.setUser(user);
        geoserver.setPassword(password);
    }

    @CliCommand(value = "geoserver show", help = "Show the url, user, and password for Geoserver.")
    public String show() {
        return geoserver.getUrl() + " " + geoserver.getUser() + " " + geoserver.getPassword();
    }
}