package org.geoserver.shell;

import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
public class SettingsCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "settings list", help = "List settings.")
    public String list() throws Exception {
        String TAB = "   ";
        String xml = HTTPUtils.get(geoserver.getUrl() + "/rest/settings.xml", geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        Element root = JDOMBuilder.buildElement(xml);

        Element settings = root.getChild("settings");
        builder.append("Settings").append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Charset: ").append(settings.getChildText("charset")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Number of Decimals: ").append(settings.getChildText("numDecimals")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Online Resource: ").append(settings.getChildText("onlineResource")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Verbose: ").append(settings.getChildText("verbose")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Verbose Exceptions: ").append(settings.getChildText("verboseExceptions")).append(OsUtils.LINE_SEPARATOR);
        builder.append(OsUtils.LINE_SEPARATOR);

        Element contact = settings.getChild("contact");
        builder.append("Contact").append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("City: ").append(contact.getChildText("addressCity")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Country: ").append(contact.getChildText("addressCountry")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Type: ").append(contact.getChildText("addressType")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Email: ").append(contact.getChildText("contactEmail")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Organization: ").append(contact.getChildText("contactOrganization")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Name: ").append(contact.getChildText("contactPerson")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Position: ").append(contact.getChildText("contactPosition")).append(OsUtils.LINE_SEPARATOR);
        builder.append(OsUtils.LINE_SEPARATOR);

        Element jai = root.getChild("jai");
        builder.append("Java Advanced Imaging (JAI)").append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Allow Interpolation: ").append(jai.getChildText("allowInterpolation")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Recycling: ").append(jai.getChildText("recycling")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Allow Interpolation: ").append(jai.getChildText("allowInterpolation")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Tile Threads: ").append(jai.getChildText("tilePriority")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Memory Capacity: ").append(jai.getChildText("memoryCapacity")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Memory Threshold: ").append(jai.getChildText("memoryThreshold")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Image IO Cache: ").append(jai.getChildText("imageIOCache")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Png Acceleration: ").append(jai.getChildText("pngAcceleration")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Jpeg Acceleration: ").append(jai.getChildText("jpegAcceleration")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Allow Native Mosaic: ").append(jai.getChildText("allowNativeMosaic")).append(OsUtils.LINE_SEPARATOR);
        builder.append(OsUtils.LINE_SEPARATOR);

        Element coverageAccess = root.getChild("coverageAccess");
        builder.append("Coverage Access").append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("maxPoolSize: ").append(coverageAccess.getChildText("maxPoolSize")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("corePoolSize: ").append(coverageAccess.getChildText("corePoolSize")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("keepAliveTime: ").append(coverageAccess.getChildText("keepAliveTime")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("queueType: ").append(coverageAccess.getChildText("queueType")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("imageIOCacheThreshold: ").append(coverageAccess.getChildText("imageIOCacheThreshold")).append(OsUtils.LINE_SEPARATOR);
        builder.append(OsUtils.LINE_SEPARATOR);

        builder.append("Other").append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Update Sequence: ").append(root.getChildText("updateSequence")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Feature Type Cache Size: ").append(root.getChildText("featureTypeCacheSize")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Global Services: ").append(root.getChildText("globalServices")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("XML Post Request Log Buffer Size: ").append(root.getChildText("xmlPostRequestLogBufferSize")).append(OsUtils.LINE_SEPARATOR);
        return builder.toString();
    }

}
