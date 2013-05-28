package org.geoserver.shell;

import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OWSCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "ows wcs list", help = "List OWS WCS Settings.")
    public String wcsList() throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wcs/settings.xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element element = JDOMBuilder.buildElement(xml);
        String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(element.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Enabled: ").append(element.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Cite Compliant: ").append(element.getChildText("citeCompliant")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Schema Base URL: ").append(element.getChildText("schemaBaseURL")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Verbose: ").append(element.getChildText("verbose")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("GML Prefixing: ").append(element.getChildText("gmlPrefixing")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("LatLon: ").append(element.getChildText("latLon")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Max Input Memory: ").append(element.getChildText("maxInputMemory")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Max Output Memory: ").append(element.getChildText("maxOutputMemory")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Subsampling Enabled: ").append(element.getChildText("subsamplingEnabled")).append(OsUtils.LINE_SEPARATOR);
        List<Element> versionElements = element.getChild("versions").getChildren("org.geotools.util.Version");
        builder.append(TAB).append("Versions:").append(OsUtils.LINE_SEPARATOR);
        for(Element versionElement : versionElements) {
            builder.append(TAB).append(TAB).append(versionElement.getChildText("version")).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "ows wms list", help = "List OWS WMS Settings.")
    public String wmsList() throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wms/settings.xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element element = JDOMBuilder.buildElement(xml);
        String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(element.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Enabled: ").append(element.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Cite Compliant: ").append(element.getChildText("citeCompliant")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Schema Base URL: ").append(element.getChildText("schemaBaseURL")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Verbose: ").append(element.getChildText("verbose")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Interpolation: ").append(element.getChildText("interpolation")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Max Buffer: ").append(element.getChildText("maxBuffer")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Max Request Memory: ").append(element.getChildText("maxRequestMemory")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Max Rendering Time: ").append(element.getChildText("maxRenderingTime")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Max Rendering Errors: ").append(element.getChildText("maxRenderingErrors")).append(OsUtils.LINE_SEPARATOR);
        Element waterMarkElement = element.getChild("watermark");
        builder.append(TAB).append("Watermark:").append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append(TAB).append("Enabled: ").append(waterMarkElement.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append(TAB).append("Position: ").append(waterMarkElement.getChildText("position")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append(TAB).append("Transparency: ").append(waterMarkElement.getChildText("transparency")).append(OsUtils.LINE_SEPARATOR);
        List<Element> versionElements = element.getChild("versions").getChildren("org.geotools.util.Version");
        builder.append(TAB).append("Versions:").append(OsUtils.LINE_SEPARATOR);
        for(Element versionElement : versionElements) {
            builder.append(TAB).append(TAB).append(versionElement.getChildText("version")).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "ows wfs list", help = "List OWS WFS Settings.")
    public String wfsList() throws Exception {
        String url = geoserver.getUrl() + "/rest/services/wfs/settings.xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element element = JDOMBuilder.buildElement(xml);
        String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(element.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Enabled: ").append(element.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Cite Compliant: ").append(element.getChildText("citeCompliant")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Schema Base URL: ").append(element.getChildText("schemaBaseURL")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Verbose: ").append(element.getChildText("verbose")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("serviceLevel: ").append(element.getChildText("serviceLevel")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("maxFeatures: ").append(element.getChildText("maxFeatures")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("featureBounding: ").append(element.getChildText("featureBounding")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("canonicalSchemaLocation: ").append(element.getChildText("canonicalSchemaLocation")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("encodeFeatureMember: ").append(element.getChildText("encodeFeatureMember")).append(OsUtils.LINE_SEPARATOR);
        Element gmlElement = element.getChild("gml");
        builder.append(TAB).append("GML:").append(OsUtils.LINE_SEPARATOR);
        List<Element> entryElements = gmlElement.getChildren("entry");
        for(Element entryElement : entryElements) {
            builder.append(TAB).append(TAB).append("Version: ").append(entryElement.getChildText("version")).append(OsUtils.LINE_SEPARATOR);
            Element entryGmlElement = entryElement.getChild("gml");
            if (entryGmlElement != null) {
                builder.append(TAB).append(TAB).append(TAB).append("SRS Name Style: ").append(entryGmlElement.getChildText("srsNameStyle")).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append(TAB).append(TAB).append("Override GML Attributes: ").append(entryGmlElement.getChildText("overrideGMLAttributes")).append(OsUtils.LINE_SEPARATOR);
            }
        }
        List<Element> versionElements = element.getChild("versions").getChildren("org.geotools.util.Version");
        builder.append(TAB).append("Versions:").append(OsUtils.LINE_SEPARATOR);
        for(Element versionElement : versionElements) {
            builder.append(TAB).append(TAB).append(versionElement.getChildText("version")).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

}
