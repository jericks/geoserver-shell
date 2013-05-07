package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroupList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TemplateCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "template list", help = "List templates.")
    public String list(
            @CliOption(key = {"workspace"}, mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());


        return "";
    }

    public static void main(String[] args) throws Exception {
        System.out.println(HTTPUtils.get("http://localhost:8080/geoserver/rest/templates.xml","admin","geoserver"));
    }


}
