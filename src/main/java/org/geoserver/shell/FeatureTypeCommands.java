package org.geoserver.shell;

import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.geotools.data.DataUtilities;
import org.geotools.referencing.CRS;
import org.jdom.Element;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class FeatureTypeCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({"featuretype list", "featuretype publish", "featuretype get", "featuretype create", "featuretype modify", "featuretype delete"})
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    @CliCommand(value = "featuretype list", help = "List feature types.")
    public String list(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "list", mandatory = false, unspecifiedDefaultValue = "configured", help = "The list parameter (configured, available, available_with_geom, all)") String list
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/datastores/" + URLUtil.encode(datastore) + "/featuretypes.xml?list=" + list;
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element element = JDOMBuilder.buildElement(xml);
        List<String> names = new ArrayList<String>();
        if (element.getName().equalsIgnoreCase("featureTypes")) {
            List<Element> elements = element.getChildren("featureType");
            for (Element elem : elements) {
                names.add(elem.getChildText("name"));
            }
        } else {
            List<Element> elements = element.getChildren("featureTypeName");
            for (Element elem : elements) {
                names.add(elem.getTextTrim());
            }
        }
        Collections.sort(names);
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        if (geoserver.isVerbose()) {
            System.out.println("URL: " + url);
            System.out.println("Response: " + xml);
        }
        return builder.toString();
    }

    @CliCommand(value = "featuretype publish", help = "Publish a feature type from an existing dataset.")
    public boolean publish(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = true, help = "The featuretype") String featuretype
    ) throws Exception {
        Element rootElement = new Element("featureType");
        rootElement.addContent(new Element("name").setText(featuretype));
        String xml = JDOMUtil.toString(rootElement);
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/datastores/" + URLUtil.encode(datastore) + "/featuretypes.xml";
        String response = HTTPUtils.postXml(url, xml, geoserver.getUser(), geoserver.getPassword());
        if (geoserver.isVerbose()) {
            System.out.println("URL: " + url);
            System.out.println("Data: " + xml);
            System.out.println("Response: " + response);
        }
        return response != null;
    }

    @CliCommand(value = "featuretype create", help = "Create a new feature type.")
    public boolean create(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = true, help = "The featuretype") String featuretype,
            @CliOption(key = "schema", mandatory = true, help = "The schema") String schema,
            @CliOption(key = "title", mandatory = false, help = "The title") String title,
            @CliOption(key = "description", mandatory = false, help = "The description") String description,
            @CliOption(key = "keywords", mandatory = false, help = "The comma delimited list of keywords") String keywords,
            @CliOption(key = "srs", mandatory = false, help = "The SRS") String srs,
            @CliOption(key = "projectionpolicy", mandatory = false, help = "The projection policy") String projectionPolicy,
            @CliOption(key = "enabled", mandatory = false, unspecifiedDefaultValue = "true", help = "The enabled flag") boolean enabled,
            @CliOption(key = "advertised", mandatory = false, unspecifiedDefaultValue = "true", help = "The advertised flag") boolean advertised,
            @CliOption(key = "maxfeatures", mandatory = false, unspecifiedDefaultValue = "0", help = "The max number of features") int maxFeatures,
            @CliOption(key = "numdecimals", mandatory = false, unspecifiedDefaultValue = "0", help = "The number of decimals") int numDecimals,
            @CliOption(key = "list", mandatory = false, help = "Determines which feature types are returned (configured, available, available_with_geom, all)", unspecifiedDefaultValue = "configured", specifiedDefaultValue = "configured") String list
    ) throws Exception {
        SimpleFeatureType featureType = DataUtilities.createType(featuretype, schema);
        Element rootElement = new Element("featureType");
        rootElement.addContent(new Element("name").setText(featuretype));
        Element attributesElement = new Element("attributes");
        rootElement.addContent(attributesElement);
        for (AttributeDescriptor attr : featureType.getAttributeDescriptors()) {
            Element attributeElement = new Element("attribute");
            attributeElement.addContent(new Element("name").setText(attr.getLocalName()));
            attributeElement.addContent(new Element("binding").setText(attr.getType().getBinding().getCanonicalName()));
            attributesElement.addContent(attributeElement);
        }
        if (featureType.getCoordinateReferenceSystem() != null) {
            String srsId = CRS.lookupIdentifier(featureType.getCoordinateReferenceSystem(), true);
            if (srsId != null) {
                rootElement.addContent(new Element("srs").setText(srsId));
            }
        } else if (srs != null) {
            rootElement.addContent(new Element("srs").setText(srs));
        }
        if (title != null) rootElement.addContent(new Element("title").setText(title));
        if (description != null) rootElement.addContent(new Element("description").setText(description));
        if (keywords != null) {
            Element keywordsElement = new Element("keywords");
            String[] keys = keywords.split(",");
            for (String key : keys) {
                keywordsElement.addContent(new Element("keyword").setText(key));
            }
            rootElement.addContent(keywordsElement);
        }
        if (projectionPolicy != null) rootElement.addContent(new Element("projectionPolicy").setText(projectionPolicy));
        rootElement.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        rootElement.addContent(new Element("advertised").setText(String.valueOf(advertised)));
        rootElement.addContent(new Element("maxFeatures").setText(String.valueOf(maxFeatures)));
        rootElement.addContent(new Element("numDecimals").setText(String.valueOf(numDecimals)));
        String xml = JDOMUtil.toString(rootElement);
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/datastores/" + URLUtil.encode(datastore) + "/featuretypes.xml";
        String response = HTTPUtils.postXml(url, xml, geoserver.getUser(), geoserver.getPassword());
        if (geoserver.isVerbose()) {
            System.out.println("URL: " + url);
            System.out.println("Data: " + xml);
            System.out.println("Response: " + response);
        }
        return response != null;
    }

    @CliCommand(value = "featuretype modify", help = "Modify a feature type.")
    public boolean modify(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = true, help = "The featuretype") String featuretype,
            @CliOption(key = "name", mandatory = false, help = "The new name") String name,
            @CliOption(key = "title", mandatory = false, help = "The title") String title,
            @CliOption(key = "description", mandatory = false, help = "The description") String description,
            @CliOption(key = "keywords", mandatory = false, help = "The comma delimited list of keywords") String keywords,
            @CliOption(key = "srs", mandatory = false, help = "The SRS") String srs,
            @CliOption(key = "projectionpolicy", mandatory = false, help = "The projection policy") String projectionPolicy,
            @CliOption(key = "enabled", mandatory = false, help = "The enabled flag") String enabled,
            @CliOption(key = "advertised", mandatory = false, help = "The advertised flag") String advertised,
            @CliOption(key = "maxfeatures", mandatory = false, help = "The max number of features") String maxFeatures,
            @CliOption(key = "numdecimals", mandatory = false, help = "The number of decimals") String numDecimals,
            @CliOption(key = "recalculate", mandatory = false, help = "Recalculate bounding boxes: nativebbox,latlonbbox", unspecifiedDefaultValue = "") String recalculate
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/datastores/" + URLUtil.encode(datastore) + "/featuretypes/" + URLUtil.encode(featuretype) + ".xml?recalculate=" + recalculate;
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<featureType>");
        if (name != null) xmlBuilder.append("<name>").append(name).append("</name>");
        if (title != null) xmlBuilder.append("<title>").append(title).append("</title>");
        if (description != null) xmlBuilder.append("<description>").append(description).append("</description>");
        if (keywords != null) {
            xmlBuilder.append("<keywords>");
            String[] keys = keywords.split(",");
            for (String key : keys) {
                xmlBuilder.append("<keyword>").append(key).append("</keyword>");
            }
            xmlBuilder.append("</keywords>");
        }
        if (srs != null) xmlBuilder.append("<srs>").append(srs).append("</srs>");
        if (projectionPolicy != null)
            xmlBuilder.append("<projectionPolicy>").append(projectionPolicy).append("</projectionPolicy>");
        if (enabled != null) xmlBuilder.append("<enabled>").append(enabled).append("</enabled>");
        if (advertised != null) xmlBuilder.append("<advertised>").append(advertised).append("</advertised>");
        if (maxFeatures != null) xmlBuilder.append("<maxFeatures>").append(maxFeatures).append("</maxFeatures>");
        if (numDecimals != null) xmlBuilder.append("<numDecimals>").append(numDecimals).append("</numDecimals>");
        xmlBuilder.append("</featureType>");
        String content = xmlBuilder.toString();
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        if (geoserver.isVerbose()) {
            System.out.println("URL: " + url);
            System.out.println("Data: " + content);
            System.out.println("Response: " + response);
        }
        return response != null;
    }

    @CliCommand(value = "featuretype delete", help = "Delete a feature type.")
    public boolean delete(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = true, help = "The featuretype") String featuretype,
            @CliOption(key = "recurse", mandatory = false, help = "Whether to delete all associated layers", unspecifiedDefaultValue = "false", specifiedDefaultValue = "false") boolean recurse
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/datastores/" + URLUtil.encode(datastore) + "/featuretypes/" + URLUtil.encode(featuretype) + ".xml?recurse=" + recurse;
        if (geoserver.isVerbose()) {
            System.out.println("URL: " + url);
        }
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "featuretype get", help = "Get a feature type.")
    public String get(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = true, help = "The featuretype") String featuretype
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/datastores/" + URLUtil.encode(datastore) + "/featuretypes/" + URLUtil.encode(featuretype) + ".xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element featureTypeElement = JDOMBuilder.buildElement(xml);
        String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(featureTypeElement.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Native Name: ").append(featureTypeElement.getChildText("nativeName")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Title: ").append(featureTypeElement.getChildText("title")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Description: ").append(featureTypeElement.getChildText("description")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Enabled: ").append(featureTypeElement.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Advertised: ").append(featureTypeElement.getChildText("advertised")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Namespace: ").append(featureTypeElement.getChild("namespace").getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Keywords: ").append(OsUtils.LINE_SEPARATOR);
        List<Element> keywordElements = featureTypeElement.getChild("keywords").getChildren("string");
        for (Element elem : keywordElements) {
            builder.append(TAB).append(TAB).append(elem.getTextTrim()).append(OsUtils.LINE_SEPARATOR);
        }
        builder.append(TAB).append("Native CRS: ").append(featureTypeElement.getChildText("nativeCRS")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("SRS: ").append(featureTypeElement.getChildText("srs")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Projection Policy: ").append(featureTypeElement.getChildText("projectionPolicy")).append(OsUtils.LINE_SEPARATOR);
        Element nativeBoundingBoxElement = featureTypeElement.getChild("nativeBoundingBox");
        builder.append(TAB).append("Native Bounding Box: ")
                .append(nativeBoundingBoxElement.getChildText("minx")).append(",")
                .append(nativeBoundingBoxElement.getChildText("miny")).append(",")
                .append(nativeBoundingBoxElement.getChildText("maxx")).append(",")
                .append(nativeBoundingBoxElement.getChildText("maxy")).append(" ")
                .append(nativeBoundingBoxElement.getChildText("crs"))
                .append(OsUtils.LINE_SEPARATOR);
        Element latLonBoundingBoxElement = featureTypeElement.getChild("latLonBoundingBox");
        builder.append(TAB).append("LatLon Bounding Box: ")
                .append(latLonBoundingBoxElement.getChildText("minx")).append(",")
                .append(latLonBoundingBoxElement.getChildText("miny")).append(",")
                .append(latLonBoundingBoxElement.getChildText("maxx")).append(",")
                .append(latLonBoundingBoxElement.getChildText("maxy")).append(" ")
                .append(nativeBoundingBoxElement.getChildText("crs"))
                .append(OsUtils.LINE_SEPARATOR);
        Element metadataElement = featureTypeElement.getChild("metadata");
        if (metadataElement != null) {
            List<Element> metadataElements = metadataElement.getChildren("entry");
            builder.append(TAB).append("Metadata: ").append(OsUtils.LINE_SEPARATOR);
            for (Element elem : metadataElements) {
                builder.append(TAB).append(TAB).append(elem.getAttributeValue("key")).append(" = ").append(elem.getTextTrim()).append(OsUtils.LINE_SEPARATOR);
            }
        }
        builder.append(TAB).append("Store: ").append(featureTypeElement.getChild("store").getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Max Features: ").append(featureTypeElement.getChildText("maxFeatures")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("numDecimals: ").append(featureTypeElement.getChildText("numDecimals")).append(OsUtils.LINE_SEPARATOR);

        List<Element> attributeElements = featureTypeElement.getChild("attributes").getChildren("attribute");
        builder.append(TAB).append("Attributes: ").append(OsUtils.LINE_SEPARATOR);
        for (Element elem : attributeElements) {
            builder.append(TAB).append(TAB).append(elem.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append(TAB).append("Binding: ").append(elem.getChildText("binding")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append(TAB).append("Min Occurs: ").append(elem.getChildText("minOccurs")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append(TAB).append("Max Occurs: ").append(elem.getChildText("maxOccurs")).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append(TAB).append("Nillable: ").append(elem.getChildText("nillable")).append(OsUtils.LINE_SEPARATOR);
            if (elem.getChild("length") != null) {
                builder.append(TAB).append(TAB).append(TAB).append("Length: ").append(elem.getChildText("length")).append(OsUtils.LINE_SEPARATOR);
            }
        }
        if (geoserver.isVerbose()) {
            System.out.println("URL: " + url);
            System.out.println("Response: " + xml);
        }
        return builder.toString();
    }
}
