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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class FontCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "font list", help = "List fonts.")
    public String list(
            @CliOption(key = "search", mandatory = false, help = "The font name search string") String search
    ) throws Exception {
        String fonts = HTTPUtils.get(geoserver.getUrl() + "/rest/fonts.xml", geoserver.getUser(), geoserver.getPassword());
        List<String> names = getFontNames(fonts);
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            if (search == null || name.startsWith(search)) {
                builder.append(name).append(OsUtils.LINE_SEPARATOR);
            }
        }
        return builder.toString();
    }

    private List<String> getFontNames(String xml) {
        Element root = JDOMBuilder.buildElement(xml);
        List<Element> children = root.getChild("fonts").getChildren("entry");
        List<String> fonts = new ArrayList<String>();
        for (Element elem : children) {
            fonts.add(elem.getTextTrim());
        }
        Collections.sort(fonts);
        return fonts;
    }

}
