package org.geoserver.shell;

import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.io.StringReader;

@Component
public class SettingsCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "settings list", help = "List settings.")
    public String list() throws Exception {
        String url = geoserver.getUrl() + "/rest/settings.xml";
        String TAB = "   ";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
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

    @CliCommand(value = "settings modify", help = "Modify settings.")
    public boolean modify(
            // Settings
            @CliOption(key = "charset", mandatory = false, help = "The charset") String charset,
            @CliOption(key = "numdecimals", mandatory = false, help = "The number of decimals") String numberOfDecimals,
            @CliOption(key = "onlineresource", mandatory = false, help = "The oneline resource url") String onlineResource,
            @CliOption(key = "verbose", mandatory = false, help = "The verbose flag (true | false)") String verbose,
            @CliOption(key = "verboseexceptions", mandatory = false, help = "The verbose exceptions flag (true | false)") String verboseExceptions,
            // Global
            @CliOption(key = "updatesequence", mandatory = false, help = "The update sequence") String updateSequence,
            @CliOption(key = "featuretyepcachesize", mandatory = false, help = "The feature type cache size") String featureTypeCacheSize,
            @CliOption(key = "globalservices", mandatory = false, help = "The global service flag (true | false)") String globalServices,
            @CliOption(key = "xmlpostrequestlogbuffersize", mandatory = false, help = "The xml post request log buffer size") String xmlPostRequestLogBufferSize,
            // JAI
            @CliOption(key = "allowinterpolation", mandatory = false, help = "The JAI flag to allow interpolation or not") String allowInterpolation,
            @CliOption(key = "recycling", mandatory = false, help = "The JAI flag to allow recycling") String recycling,
            @CliOption(key = "tilepriority", mandatory = false, help = "The JAI tile priority") String tilePriority,
            @CliOption(key = "tilethreads", mandatory = false, help = "The JAI tile threads") String tileThreads,
            @CliOption(key = "memorycapacity", mandatory = false, help = "The JAI memory capacity") String memoryCapacity,
            @CliOption(key = "memorythreshold", mandatory = false, help = "The JAI memory threshold") String memoryThreshold,
            @CliOption(key = "imageiocache", mandatory = false, help = "The JAI flag to allow image IO cache") String imageIOCache,
            @CliOption(key = "pngacceleration", mandatory = false, help = "The JAI flag to allow PNG acceleration") String pngAcceleration,
            @CliOption(key = "jpegacceleration", mandatory = false, help = "The JAI flag to allow JPEG acceleration") String jpegAcceleration,
            @CliOption(key = "allownativemosaic", mandatory = false, help = "The JAI flag to allow native mosaic") String allowNativeMosaic,
            // Coverage Access
            @CliOption(key = "maxpoolsize", mandatory = false, help = "The coverage access parameter to set the max pool size") String maxPoolSize,
            @CliOption(key = "corepoolsize", mandatory = false, help = "The coverage access parameter to set the core pool size") String corePoolSize,
            @CliOption(key = "keepalivetime", mandatory = false, help = "The coverage access parameter to set the keep alive time") String keepAliveTime,
            @CliOption(key = "queuetype", mandatory = false, help = "The coverage access parameter to set the queue type") String queueType,
            @CliOption(key = "imageiocachethreshold", mandatory = false, help = "The coverage access parameter to set the image io cache threshold") String imageIOCacheThreshold,
            // Contact
            @CliOption(key = "person", mandatory = false, help = "The contact's name") String person,
            @CliOption(key = "position", mandatory = false, help = "The contact's position") String position,
            @CliOption(key = "email", mandatory = false, help = "The contact's email") String email,
            @CliOption(key = "organization", mandatory = false, help = "The contact's organization") String organization,
            @CliOption(key = "city", mandatory = false, help = "The contact's city") String city,
            @CliOption(key = "country", mandatory = false, help = "The contact's country") String country,
            @CliOption(key = "addresstype", mandatory = false, help = "The contact's address type") String addressType
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/settings.xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element element = JDOMBuilder.buildElement(xml);
        // Settings
        Element settingsElement = element.getChild("settings");
        if (charset != null) {
            JDOMUtil.getOrAdd(settingsElement, "charset").setText(charset);
        }
        if (numberOfDecimals != null) {
            JDOMUtil.getOrAdd(settingsElement, "numDecimals").setText(numberOfDecimals);
        }
        if (onlineResource != null) {
            JDOMUtil.getOrAdd(settingsElement, "onlineResource").setText(onlineResource);
        }
        if (verbose != null) {
            JDOMUtil.getOrAdd(settingsElement, "verbose").setText(verbose);
        }
        if (verboseExceptions != null) {
            JDOMUtil.getOrAdd(settingsElement, "verboseExceptions").setText(verboseExceptions);
        }
        // Contacts
        Element contactElement = settingsElement.getChild("contact");
        if (person != null) {
            JDOMUtil.getOrAdd(contactElement,"contactPerson").setText(person);
        }
        if (position != null) {
            JDOMUtil.getOrAdd(contactElement,"contactPosition").setText(position);
        }
        if (email != null) {
            JDOMUtil.getOrAdd(contactElement,"contactEmail").setText(email);
        }
        if (organization != null) {
            JDOMUtil.getOrAdd(contactElement,"contactOrganization").setText(organization);
        }
        if (city != null) {
            JDOMUtil.getOrAdd(contactElement,"addressCity").setText(city);
        }
        if (country != null) {
            JDOMUtil.getOrAdd(contactElement,"addressCountry").setText(country);
        }
        if (addressType != null) {
            JDOMUtil.getOrAdd(contactElement,"addressType").setText(addressType);
        }
        // JAI
        Element jaiElement = element.getChild("jai");
        if (allowInterpolation != null) {
            JDOMUtil.getOrAdd(jaiElement,"allowInterpolation").setText(allowInterpolation);
        }
        if (recycling != null) {
            JDOMUtil.getOrAdd(jaiElement,"recycling").setText(recycling);
        }
        if (tilePriority != null) {
            JDOMUtil.getOrAdd(jaiElement,"tilePriority").setText(tilePriority);
        }
        if (tileThreads != null) {
            JDOMUtil.getOrAdd(jaiElement,"tileThreads").setText(tileThreads);
        }
        if (memoryCapacity != null) {
            JDOMUtil.getOrAdd(jaiElement,"memoryCapacity").setText(memoryCapacity);
        }
        if (memoryThreshold != null) {
            JDOMUtil.getOrAdd(jaiElement,"memoryThreshold").setText(memoryThreshold);
        }
        if (imageIOCache != null) {
            JDOMUtil.getOrAdd(jaiElement,"imageIOCache").setText(imageIOCache);
        }
        if (pngAcceleration != null) {
            JDOMUtil.getOrAdd(jaiElement,"pngAcceleration").setText(pngAcceleration);
        }
        if (jpegAcceleration != null) {
            JDOMUtil.getOrAdd(jaiElement,"jpegAcceleration").setText(jpegAcceleration);
        }
        if (allowNativeMosaic != null) {
            JDOMUtil.getOrAdd(jaiElement,"allowNativeMosaic").setText(allowNativeMosaic);
        }
        // Coverage Access
        Element coverageAccessElement = element.getChild("coverageAccess");
        if (maxPoolSize != null) {
            JDOMUtil.getOrAdd(coverageAccessElement,"maxPoolSize").setText(maxPoolSize);
        }
        if (corePoolSize != null) {
            JDOMUtil.getOrAdd(coverageAccessElement,"corePoolSize").setText(corePoolSize);
        }
        if (keepAliveTime != null) {
            JDOMUtil.getOrAdd(coverageAccessElement,"keepAliveTime").setText(keepAliveTime);
        }
        if (queueType != null) {
            JDOMUtil.getOrAdd(coverageAccessElement,"queueType").setText(queueType);
        }
        if (imageIOCacheThreshold != null) {
            JDOMUtil.getOrAdd(coverageAccessElement,"imageIOCacheThreshold").setText(imageIOCacheThreshold);
        }
        // Global
        if (updateSequence != null) {
            JDOMUtil.getOrAdd(element,"updateSequence").setText(updateSequence);
        }
        if (featureTypeCacheSize != null) {
            JDOMUtil.getOrAdd(element,"featureTypeCacheSize").setText(featureTypeCacheSize);
        }
        if (globalServices != null) {
            JDOMUtil.getOrAdd(element,"globalServices").setText(globalServices);
        }
        if (xmlPostRequestLogBufferSize != null) {
            JDOMUtil.getOrAdd(element,"xmlPostRequestLogBufferSize").setText(xmlPostRequestLogBufferSize);
        }
        String content = new XMLOutputter(Format.getPrettyFormat()).outputString(element);
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "settings contact list", help = "List contact settings.")
    public String listContact() throws Exception {
        String TAB = "   ";
        String xml = HTTPUtils.get(geoserver.getUrl() + "/rest/settings/contact.xml", geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        Element element = JDOMBuilder.buildElement(xml);
        builder.append("City: ").append(element.getChildText("addressCity")).append(OsUtils.LINE_SEPARATOR);
        builder.append("Country: ").append(element.getChildText("addressCountry")).append(OsUtils.LINE_SEPARATOR);
        builder.append("Type: ").append(element.getChildText("addressType")).append(OsUtils.LINE_SEPARATOR);
        builder.append("Email: ").append(element.getChildText("contactEmail")).append(OsUtils.LINE_SEPARATOR);
        builder.append("Organization: ").append(element.getChildText("contactOrganization")).append(OsUtils.LINE_SEPARATOR);
        builder.append("Name: ").append(element.getChildText("contactPerson")).append(OsUtils.LINE_SEPARATOR);
        builder.append("Position: ").append(element.getChildText("contactPosition")).append(OsUtils.LINE_SEPARATOR);
        return builder.toString();
    }

    @CliCommand(value = "settings contact modify", help = "Modify contact settings.")
    public boolean modifyContact(
            @CliOption(key = "person", mandatory = false, help = "The contact's name") String person,
            @CliOption(key = "position", mandatory = false, help = "The contact's position") String position,
            @CliOption(key = "email", mandatory = false, help = "The contact's email") String email,
            @CliOption(key = "organization", mandatory = false, help = "The contact's organization") String organization,
            @CliOption(key = "city", mandatory = false, help = "The contact's city") String city,
            @CliOption(key = "country", mandatory = false, help = "The contact's country") String country,
            @CliOption(key = "addresstype", mandatory = false, help = "The contact's address type") String addressType
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/settings/contact.xml";
        Element element = new Element("contact");
        if (person != null) {
            element.addContent(new Element("contactPerson").setText(person));
        }
        if (position != null) {
            element.addContent(new Element("contactPosition").setText(position));
        }
        if (email != null) {
            element.addContent(new Element("contactEmail").setText(email));
        }
        if (organization != null) {
            element.addContent(new Element("contactOrganization").setText(organization));
        }
        if (city != null) {
            element.addContent(new Element("addressCity").setText(city));
        }
        if (country != null) {
            element.addContent(new Element("addressCountry").setText(country));
        }
        if (addressType != null) {
            element.addContent(new Element("addressType").setText(addressType));
        }
        String content = new XMLOutputter(Format.getPrettyFormat()).outputString(element);
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "settings local list", help = "List settings.")
    public String listLocal(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/settings.xml";
        String TAB = "   ";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        Element settings = JDOMBuilder.buildElement(xml);
        if (settings.getChild("workspace") != null) {
            // Settings
            builder.append("Settings").append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Charset: ").append(settings.getChildText("charset")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Number of Decimals: ").append(settings.getChildText("numDecimals")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Online Resource: ").append(settings.getChildText("onlineResource")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Verbose: ").append(settings.getChildText("verbose")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append("Verbose Exceptions: ").append(settings.getChildText("verboseExceptions")).append(OsUtils.LINE_SEPARATOR);
            // Contact
            Element contact = settings.getChild("contact");
            if (contact != null) {
                builder.append(OsUtils.LINE_SEPARATOR);
                builder.append("Contact").append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append("City: ").append(contact.getChildText("addressCity")).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append("Country: ").append(contact.getChildText("addressCountry")).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append("Type: ").append(contact.getChildText("addressType")).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append("Email: ").append(contact.getChildText("contactEmail")).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append("Organization: ").append(contact.getChildText("contactOrganization")).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append("Name: ").append(contact.getChildText("contactPerson")).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append("Position: ").append(contact.getChildText("contactPosition")).append(OsUtils.LINE_SEPARATOR);
            }
        }
        return builder.toString();
    }

    @CliCommand(value = "settings local create", help = "Create local settings.")
    public boolean createLocal(
            // Workspace
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            // Settings
            @CliOption(key = "charset", mandatory = false, help = "The charset") String charset,
            @CliOption(key = "numdecimals", mandatory = false, help = "The number of decimals") String numberOfDecimals,
            @CliOption(key = "verbose", mandatory = false, help = "The verbose flag (true | false)") String verbose,
            @CliOption(key = "verboseexceptions", mandatory = false, help = "The verbose exceptions flag (true | false)") String verboseExceptions,
            // Contact
            @CliOption(key = "person", mandatory = false, help = "The contact's name") String person,
            @CliOption(key = "position", mandatory = false, help = "The contact's position") String position,
            @CliOption(key = "email", mandatory = false, help = "The contact's email") String email,
            @CliOption(key = "organization", mandatory = false, help = "The contact's organization") String organization,
            @CliOption(key = "city", mandatory = false, help = "The contact's city") String city,
            @CliOption(key = "country", mandatory = false, help = "The contact's country") String country,
            @CliOption(key = "addresstype", mandatory = false, help = "The contact's address type") String addressType
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/settings.xml";
        Element settingsElement = JDOMBuilder.buildElement(HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword()));
        settingsElement.addContent(new Element("workspace").addContent(new Element("name").setText(workspace)));
        // Settings
        if (charset != null) {
            JDOMUtil.getOrAdd(settingsElement, "charset").setText(charset);
        }
        if (numberOfDecimals != null) {
            JDOMUtil.getOrAdd(settingsElement, "numDecimals").setText(numberOfDecimals);
        }
        if (verbose != null) {
            JDOMUtil.getOrAdd(settingsElement, "verbose").setText(verbose);
        }
        if (verboseExceptions != null) {
            JDOMUtil.getOrAdd(settingsElement, "verboseExceptions").setText(verboseExceptions);
        }
        // Contact
        Element contactElement = JDOMUtil.getOrAdd(settingsElement,"contact");
        if (person != null) {
            JDOMUtil.getOrAdd(contactElement, "contactPerson").setText(person);
        }
        if (position != null) {
            JDOMUtil.getOrAdd(contactElement, "contactPosition").setText(position);
        }
        if (email != null) {
            JDOMUtil.getOrAdd(contactElement, "contactEmail").setText(email);
        }
        if (organization != null) {
            JDOMUtil.getOrAdd(contactElement, "contactOrganization").setText(organization);
        }
        if (city != null) {
            JDOMUtil.getOrAdd(contactElement, "addressCity").setText(city);
        }
        if (country != null) {
            JDOMUtil.getOrAdd(contactElement, "addressCountry").setText(country);
        }
        if (addressType != null) {
            JDOMUtil.getOrAdd(contactElement, "addressType").setText(addressType);
        }
        String xml = new XMLOutputter(Format.getPrettyFormat()).outputString(settingsElement);
        String response = HTTPUtils.putXml(url, xml, geoserver.getUser(), geoserver.getPassword());
        System.out.println(url);
        System.out.println(xml);
        System.out.println(response);
        return response != null;
    }

    @CliCommand(value = "settings local modify", help = "Modify local settings.")
    public boolean modifyLocal(
            // Workspace
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            // Settings
            @CliOption(key = "charset", mandatory = false, help = "The charset") String charset,
            @CliOption(key = "numdecimals", mandatory = false, help = "The number of decimals") String numberOfDecimals,
            @CliOption(key = "verbose", mandatory = false, help = "The verbose flag (true | false)") String verbose,
            @CliOption(key = "verboseexceptions", mandatory = false, help = "The verbose exceptions flag (true | false)") String verboseExceptions,
            // Contact
            @CliOption(key = "person", mandatory = false, help = "The contact's name") String person,
            @CliOption(key = "position", mandatory = false, help = "The contact's position") String position,
            @CliOption(key = "email", mandatory = false, help = "The contact's email") String email,
            @CliOption(key = "organization", mandatory = false, help = "The contact's organization") String organization,
            @CliOption(key = "city", mandatory = false, help = "The contact's city") String city,
            @CliOption(key = "country", mandatory = false, help = "The contact's country") String country,
            @CliOption(key = "addresstype", mandatory = false, help = "The contact's address type") String addressType
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/settings.xml";
        Element settingsElement = JDOMBuilder.buildElement(HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword()));
        // Settings
        if (charset != null) {
            JDOMUtil.getOrAdd(settingsElement, "charset").setText(charset);
        }
        if (numberOfDecimals != null) {
            JDOMUtil.getOrAdd(settingsElement, "numDecimals").setText(numberOfDecimals);
        }
        if (verbose != null) {
            JDOMUtil.getOrAdd(settingsElement, "verbose").setText(verbose);
        }
        if (verboseExceptions != null) {
            JDOMUtil.getOrAdd(settingsElement, "verboseExceptions").setText(verboseExceptions);
        }
        // Contact
        Element contactElement = JDOMUtil.getOrAdd(settingsElement,"contact");
        if (person != null) {
            JDOMUtil.getOrAdd(contactElement, "contactPerson").setText(person);
        }
        if (position != null) {
            JDOMUtil.getOrAdd(contactElement, "contactPosition").setText(position);
        }
        if (email != null) {
            JDOMUtil.getOrAdd(contactElement, "contactEmail").setText(email);
        }
        if (organization != null) {
            JDOMUtil.getOrAdd(contactElement, "contactOrganization").setText(organization);
        }
        if (city != null) {
            JDOMUtil.getOrAdd(contactElement, "addressCity").setText(city);
        }
        if (country != null) {
            JDOMUtil.getOrAdd(contactElement, "addressCountry").setText(country);
        }
        if (addressType != null) {
            JDOMUtil.getOrAdd(contactElement, "addressType").setText(addressType);
        }
        String xml = new XMLOutputter(Format.getPrettyFormat()).outputString(settingsElement);
        String response = HTTPUtils.putXml(url, xml, geoserver.getUser(), geoserver.getPassword());
        System.out.println(url);
        System.out.println(xml);
        System.out.println(response);
        return response != null;
    }

    @CliCommand(value = "settings local delete", help = "Delete local settings.")
    public boolean deleteLocal(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/settings.xml";
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }
}
