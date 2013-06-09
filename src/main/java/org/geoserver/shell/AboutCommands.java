package org.geoserver.shell;

import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AboutCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "version list", help = "Get versions.")
    public String versionList() throws Exception {
        String TAB = "   ";
        String xml = HTTPUtils.get(geoserver.getUrl() + "/rest/about/versions.xml", geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        Element root = JDOMBuilder.buildElement(xml);

        List<Element> resources = root.getChildren("resource");
        for (Element resource : resources) {
            String name = resource.getAttributeValue("name");
            String buildTime = resource.getChildText("Build-Timestamp");
            String gitRevision = resource.getChildText("Git-Revision");
            String version = resource.getChildText("Version");
            builder.append(name).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Version: ").append(version).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Build Time: ").append(buildTime).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Git Revision: ").append(gitRevision).append(OsUtils.LINE_SEPARATOR);
            builder.append(OsUtils.LINE_SEPARATOR);
        }

        return builder.toString();
    }

    @CliCommand(value = "manifest list", help = "List manifest.")
    public String manifestList() throws Exception {
        String xml = HTTPUtils.get(geoserver.getUrl() + "/rest/about/manifests.xml", geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        Element root = JDOMBuilder.buildElement(xml);
        List<Element> resources = root.getChildren("resource");
        for (Element resource : resources) {
            String name = resource.getAttributeValue("name");
            builder.append(name).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "manifest get", help = "Get a manifest.")
    public String manifestGet(
            @CliOption(key = "name", mandatory = true, help = "The name") String name
    ) throws Exception {
        String TAB = "   ";
        String xml = HTTPUtils.get(geoserver.getUrl() + "/rest/about/manifests.xml", geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        Element root = JDOMBuilder.buildElement(xml);
        List<Element> resources = root.getChildren("resource");
        for (Element resource : resources) {
            String n = resource.getAttributeValue("name");
            if (name.equalsIgnoreCase(n)) {
                builder.append(name).append(OsUtils.LINE_SEPARATOR);
                List<Element> children = resource.getChildren();
                for (Element child : children) {
                    builder.append(TAB).append(child.getName()).append(": ").append(child.getTextTrim()).append(OsUtils.LINE_SEPARATOR);
                }
            }
        }
        return builder.toString();
    }
}