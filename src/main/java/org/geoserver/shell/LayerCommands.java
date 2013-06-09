package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
        Collections.sort(names);
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "layer get", help = "Get a layer.")
    public String get(
            @CliOption(key = "name", mandatory = true, help = "The layer name") String name
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTLayer layer = reader.getLayer(name);
        final String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(layer.getName()).append(OsUtils.LINE_SEPARATOR);
            // @TODO RESTLayer looks at layer/resource/title instead of layer/title
            builder.append(TAB).append("Title: ").append(layer.getTitle()).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Type: ").append(layer.getType()).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Abstract: ").append(layer.getAbstract()).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Default Style: ").append(layer.getDefaultStyle()).append(OsUtils.LINE_SEPARATOR);
            // @TODO RESTLayer can't access <styles>
            // @TODO RESTLayer can throw Exception where there is no namespace
            try {
                builder.append(TAB).append("Namespace: ").append(layer.getNameSpace()).append(OsUtils.LINE_SEPARATOR);
            } catch (Exception ex) {
                // Do nothing
            }
            builder.append(TAB).append("Type String: ").append(layer.getTypeString()).append(OsUtils.LINE_SEPARATOR);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    @CliCommand(value = "layer modify", help = "Modify a layer.")
    public boolean modify(
            @CliOption(key = "name", mandatory = true, help = "The layer name") String name,
            @CliOption(key = "title", mandatory = false, help = "The new title") String title,
            @CliOption(key = "abstract", mandatory = false, help = "The new abstract") String abstractStr,
            @CliOption(key = "defaultStyle", mandatory = false, help = "The new default style") String defaultStyle,
            @CliOption(key = "enabled", mandatory = false, help = "Whether the layer is enabled or not") String enabled
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/layers/" + URLUtil.encode(name) + ".xml";
        StringBuilder builder = new StringBuilder();
        builder.append("<layer>");
        builder.append("<name>").append(name).append("</name>");
        if (title != null) {
            builder.append("<title>").append(title).append("</title>");
        }
        if (abstractStr != null) {
            builder.append("<abstract>").append(abstractStr).append("</abstract>");
        }
        if (defaultStyle != null) {
            builder.append("<defaultStyle><name>").append(defaultStyle).append("</name></defaultStyle>");
        }
        if (enabled != null) {
            builder.append("<enabled>").append(enabled).append("</enabled>");
        }
        builder.append("</layer>");
        String content = builder.toString();
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "layer remove", help = "Remove a layer.")
    public boolean delete(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "recurse", mandatory = false, unspecifiedDefaultValue = "false", specifiedDefaultValue = "false", help = "Whether to delete associated styles") boolean recurse
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/layers/" + URLUtil.encode(name) + ".xml?recurse=" + recurse;
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "layer style list", help = "List the Styles for a layer.")
    public String listStyles(
            @CliOption(key = "name", mandatory = true, help = "The name") String name
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/layers/" + URLUtil.encode(name) + "/styles.xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element element = JDOMBuilder.buildElement(xml);
        List<Element> styleElements = element.getChildren("style");
        StringBuilder builder = new StringBuilder();
        for (Element styleElement : styleElements) {
            builder.append(styleElement.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "layer style add", help = "Add a Style to a layer.")
    public boolean addStyle(
            @CliOption(key = "name", mandatory = true, help = "The name") String name,
            @CliOption(key = "style", mandatory = true, help = "The style") String style
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/layers/" + URLUtil.encode(name) + "/styles.xml";
        String xml = "<style><name>" + style + "</name></style>";
        String response = HTTPUtils.postXml(url, xml, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }
}
