package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.geotools.data.DataUtilities;
import org.geotools.referencing.CRS;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FeatureTypeCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "featuretype list", help = "List feature types.")
    public String list(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/datastores/" + datastore + "/featuretypes.xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element element = JDOMBuilder.buildElement(xml);
        List<Element> elements = element.getChildren("featureType");
        List<String> names = new ArrayList<String>();
        for (Element elem : elements) {
            names.add(elem.getChildText("name"));
        }
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name + OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "featuretype create", help = "Create a feature type.")
    public boolean create(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = true, help = "The featuretype") String featuretype,
            @CliOption(key = "schema", mandatory = true, help = "The schema") String schema,
            @CliOption(key = "list", mandatory = false, help = "Determines which feature types are returned (configured, available, available_with_geom, all)", unspecifiedDefaultValue = "configured", specifiedDefaultValue = "configured") String list
    ) throws Exception {
        SimpleFeatureType featureType = DataUtilities.createType(featuretype, schema);
        Document document = new Document();
        Element rootElement = new Element("featureType");
        document.setRootElement(rootElement);
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
            String srs = CRS.lookupIdentifier(featureType.getCoordinateReferenceSystem(), true);
            if (srs != null) {
                rootElement.addContent(new Element("srs").setText(srs));
            }
        }
        XMLOutputter outputter = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
        String xml = outputter.outputString(document);
        System.out.println(xml);
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/datastores/" + datastore + "/featuretypes.xml";
        String response = HTTPUtils.post(url, xml, GeoServerRESTPublisher.Format.XML.getContentType(), geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    // @TODO Support updating more than bbox, values as key=value key2=value2
    @CliCommand(value = "featuretype modify", help = "Modify a feature type.")
    public boolean modify(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = true, help = "The featuretype") String featuretype,
            @CliOption(key = "recalculate", mandatory = false, help = "Recalculate bounding boxes: nativebbox,latlonbbox", unspecifiedDefaultValue = "") String recalculate
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/datastores/" + datastore + "/featuretypes/" + featuretype + ".xml?recalculate=" + recalculate;
        String content = "<featureType><name>" + featuretype + "</name></featureType>";
        String contentType = GeoServerRESTPublisher.Format.XML.getContentType();
        String response = HTTPUtils.put(url, content, contentType, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "featuretype delete", help = "Delete a feature type.")
    public boolean delete(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = true, help = "The featuretype") String featuretype,
            @CliOption(key = "recurse", mandatory = false, help = "Whether to delete all associated layers", unspecifiedDefaultValue = "false", specifiedDefaultValue = "false") boolean recurse
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/datastores/" + datastore + "/featuretypes/" + featuretype + ".xml?recurse=" + recurse;
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "featuretype get", help = "Get a feature type.")
    public String get(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = true, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = true, help = "The featuretype") String featuretype
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + workspace + "/datastores/" + datastore + "/featuretypes/" + featuretype + ".xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element featureTypeElement = JDOMBuilder.buildElement(xml);
        String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(featureTypeElement.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Native Name: ").append(featureTypeElement.getChildText("nativeName")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Title: ").append(featureTypeElement.getChildText("title")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Namespace: ").append(featureTypeElement.getChild("namespace").getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Abstract: ").append(featureTypeElement.getChildText("abstract")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Keywords: ").append(OsUtils.LINE_SEPARATOR);
        List<Element> keywordElements = featureTypeElement.getChild("keywords").getChildren("string");
        for (Element elem : keywordElements) {
            builder.append(TAB).append(TAB).append(elem.getTextTrim()).append(OsUtils.LINE_SEPARATOR);
        }
        builder.append(TAB).append("Native CRS: ").append(featureTypeElement.getChildText("nativeCRS")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("SRS: ").append(featureTypeElement.getChildText("srs")).append(OsUtils.LINE_SEPARATOR);
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
        List<Element> metadataElements = featureTypeElement.getChild("metadata").getChildren("entry");
        builder.append(TAB).append("Metadata: ").append(OsUtils.LINE_SEPARATOR);
        for (Element elem : metadataElements) {
            builder.append(TAB).append(TAB).append(elem.getAttributeValue("key")).append(" = ").append(elem.getTextTrim()).append(OsUtils.LINE_SEPARATOR);
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
        return builder.toString();
    }
}
