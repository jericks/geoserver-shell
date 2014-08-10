package org.geoserver.shell;

import com.google.common.base.Strings;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
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
public class WmsStoreCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({"wmsstore list", "wmsstore create", "wmsstore modify", "wmsstore get", "wmsstore delete",
            "wmsstore layer list", "wmsstore available layer list", "wmsstore layer get", "wmsstore layer create",
            "wmsstore layer modify", "wmsstore layer delete"
    })
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    @CliCommand(value = "wmsstore list", help = "List WMS Stores.")
    public String listStores(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace
    ) throws Exception {
        StringBuilder builder = new StringBuilder();
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        // Build up list of Workspaces
        List<String> workspaces = new ArrayList<String>();
        if (workspace != null) {
            workspaces.add(workspace);
        } else {
            List<String> names = reader.getWorkspaceNames();
            Collections.sort(names);
            workspaces.addAll(names);
        }
        int counter = 0;
        for (String w : workspaces) {
            // Display Workspace (but only if we are showing more than one)
            if (workspaces.size() > 1) {
                if (counter > 0) {
                    builder.append(OsUtils.LINE_SEPARATOR);
                }
                builder.append(w).append(OsUtils.LINE_SEPARATOR);
                builder.append(Strings.repeat("-", w.length())).append(OsUtils.LINE_SEPARATOR);
            }
            // Get the WMS Stores
            String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(w) + "/wmsstores.xml";
            String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
            Element element = JDOMBuilder.buildElement(xml);
            List<Element> wmsStoreElements = element.getChildren("wmsStore");
            List<String> names = new ArrayList<String>();
            for (Element wmsStoreElement : wmsStoreElements) {
                names.add(wmsStoreElement.getChildText("name"));
            }
            Collections.sort(names);
            for (String name : names) {
                builder.append(name).append(OsUtils.LINE_SEPARATOR);
            }
            counter++;
        }
        return builder.toString();
    }

    @CliCommand(value = "wmsstore get", help = "Get a WMS Stores.")
    public String getStore(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "store", mandatory = true, help = "The WMSStore") String store
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/wmsstores/" + URLUtil.encode(store) + ".xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        String TAB = "   ";
        Element element = JDOMBuilder.buildElement(xml);
        builder.append(element.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Type: ").append(element.getChildText("type")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Enabled: ").append(element.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
        Element wsElement = element.getChild("workspace");
        if (wsElement != null) {
            builder.append(TAB).append("Workspace: ").append(wsElement.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        }
        builder.append(TAB).append("Capabilities URL: ").append(element.getChildText("capabilitiesURL")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Max Connections: ").append(element.getChildText("maxConnections")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Read Timeout: ").append(element.getChildText("readTimeout")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Connect Timeout: ").append(element.getChildText("connectTimeout")).append(OsUtils.LINE_SEPARATOR);
        Element metadataElement = element.getChild("metadata");
        if (metadataElement != null) {
            List<Element> entryElements = metadataElement.getChildren("entry");
            if (entryElements.size() > 0) {
                builder.append(TAB).append("Metadata: ").append(OsUtils.LINE_SEPARATOR);
                for (Element entryElement : entryElements) {
                    builder.append(TAB).append(TAB).append(entryElement.getAttributeValue("key")).append(": ").append(entryElement.getTextTrim()).append(OsUtils.LINE_SEPARATOR);
                }
            }
        }
        return builder.toString();
    }

    @CliCommand(value = "wmsstore create", help = "Create a WMS Store.")
    public boolean createStore(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "store", mandatory = true, help = "The name") String store,
            @CliOption(key = "url", mandatory = true, help = "The capabilities url") String capabilitiesUrl,
            @CliOption(key = "maxconnections", mandatory = false, unspecifiedDefaultValue = "6", help = "The maximum number of connections") int maxConnections,
            @CliOption(key = "readtimeout", mandatory = false, unspecifiedDefaultValue = "60", help = "The read timeout") int readTimeout,
            @CliOption(key = "connecttimeout", mandatory = false, unspecifiedDefaultValue = "30", help = "The connect timeout") int connectTimeout,
            @CliOption(key = "enabled", mandatory = false, unspecifiedDefaultValue = "true", help = "Whether the store should be enabled") boolean enabled
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/wmsstores.xml";
        Element element = new Element("wmsStore");
        element.addContent(new Element("name").setText(store));
        element.addContent(new Element("type").setText("WMS"));
        element.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        element.addContent(new Element("capabilitiesURL").setText(capabilitiesUrl));
        element.addContent(new Element("maxConnections").setText(String.valueOf(maxConnections)));
        element.addContent(new Element("readTimeout").setText(String.valueOf(readTimeout)));
        element.addContent(new Element("connectTimeout").setText(String.valueOf(connectTimeout)));
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.postXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "wmsstore modify", help = "Modify a WMS Store.")
    public boolean modifyStore(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "store", mandatory = true, help = "The name") String store,
            @CliOption(key = "url", mandatory = false, help = "The capabilities url") String capabilitiesUrl,
            @CliOption(key = "maxconnections", mandatory = false, help = "The maximum number of connections") String maxConnections,
            @CliOption(key = "readtimeout", mandatory = false, help = "The read timeout") String readTimeout,
            @CliOption(key = "connecttimeout", mandatory = false, help = "The connect timeout") String connectTimeout,
            @CliOption(key = "enabled", mandatory = false, help = "Whether the store should be enabled") String enabled
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/wmsstores/" + URLUtil.encode(store) + ".xml";
        Element element = JDOMBuilder.buildElement(HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword()));
        element.removeChild("wmsLayers");
        if (enabled != null) {
            JDOMUtil.getOrAdd(element, "enabled").setText(enabled);
        }
        if (capabilitiesUrl != null) {
            JDOMUtil.getOrAdd(element, "capabilitiesURL").setText(capabilitiesUrl);
        }
        if (maxConnections != null) {
            JDOMUtil.getOrAdd(element, "maxConnections").setText(maxConnections);
        }
        if (readTimeout != null) {
            JDOMUtil.getOrAdd(element, "readTimeout").setText(readTimeout);
        }
        if (connectTimeout != null) {
            JDOMUtil.getOrAdd(element, "connectTimeout").setText(connectTimeout);
        }
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "wmsstore delete", help = "Delete a WMS Store.")
    public boolean deleteStore(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "store", mandatory = true, help = "The WMSStore") String store,
            @CliOption(key = "recurse", mandatory = false, help = "Whether to delete all associated layers", unspecifiedDefaultValue = "false", specifiedDefaultValue = "false") boolean recurse
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/wmsstores/" + URLUtil.encode(store) + ".xml?recurse=" + recurse;
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "wmsstore layer list", help = "List the Layers in a WMS Store.")
    public String listPublishedLayers(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "store", mandatory = false, help = "The WMS Store") String store
    ) throws Exception {
        StringBuilder builder = new StringBuilder();
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        // Build the List of Workspaces
        List<String> workspaces = new ArrayList<String>();
        if (workspace != null) {
            workspaces.add(workspace);
        } else {
            List<String> names = reader.getWorkspaceNames();
            Collections.sort(names);
            workspaces.addAll(names);
        }
        int workspaceCounter = 0;
        for (String w : workspaces) {
            // Build up the List of WMSStores
            List<String> wmsStores = new ArrayList<String>();
            if (store != null) {
                wmsStores.add(store);
            } else {
                // Get the WMS Stores
                String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(w) + "/wmsstores.xml";
                String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
                Element element = JDOMBuilder.buildElement(xml);
                List<Element> wmsStoreElements = element.getChildren("wmsStore");
                List<String> names = new ArrayList<String>();
                for (Element wmsStoreElement : wmsStoreElements) {
                    names.add(wmsStoreElement.getChildText("name"));
                }
                Collections.sort(names);
                wmsStores.addAll(names);
            }
            // Display the Workspace (but only if we are showing more than one)
            if (workspaces.size() > 1) {
                if (workspaceCounter > 0) {
                    builder.append(OsUtils.LINE_SEPARATOR);
                }
                builder.append(w).append(OsUtils.LINE_SEPARATOR);
                builder.append(Strings.repeat("-", w.length())).append(OsUtils.LINE_SEPARATOR);
            }
            for (String wmsStore : wmsStores) {
                String indent = Strings.repeat(" ", workspaces.size() > 1 ? 3 : 0);
                // Display the WMSStore (but only if we are showing more than one)
                if (wmsStores.size() > 1) {
                    builder.append(OsUtils.LINE_SEPARATOR);
                    builder.append(indent).append(wmsStore).append(OsUtils.LINE_SEPARATOR);
                    builder.append(indent).append(Strings.repeat("-", wmsStore.length())).append(OsUtils.LINE_SEPARATOR);
                }
                // Get the List of WMS Layers
                String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(w) + "/wmsstores/" + URLUtil.encode(wmsStore) + "/wmslayers.xml";
                String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
                Element element = JDOMBuilder.buildElement(xml);
                List<Element> wmsStoreElements = element.getChildren("wmsLayer");
                List<String> names = new ArrayList<String>();
                for (Element wmsStoreElement : wmsStoreElements) {
                    names.add(wmsStoreElement.getChildText("name"));
                }
                Collections.sort(names);
                for (String name : names) {
                    builder.append(indent).append(name).append(OsUtils.LINE_SEPARATOR);
                }
            }
            workspaceCounter++;
        }
        return builder.toString();
    }

    @CliCommand(value = "wmsstore available layer list", help = "List the available Layers in a WMS Store.")
    public String listAvailableLayers(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "store", mandatory = false, help = "The WMS Store") String store
    ) throws Exception {
        StringBuilder builder = new StringBuilder();
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        // Build the List of Workspaces
        List<String> workspaces = new ArrayList<String>();
        if (workspace != null) {
            workspaces.add(workspace);
        } else {
            List<String> names = reader.getWorkspaceNames();
            Collections.sort(names);
            workspaces.addAll(names);
        }
        int workspaceCounter = 0;
        for (String w : workspaces) {
            // Build up the List of WMSStores
            List<String> wmsStores = new ArrayList<String>();
            if (store != null) {
                wmsStores.add(store);
            } else {
                // Get the WMS Stores
                String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(w) + "/wmsstores.xml";
                String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
                Element element = JDOMBuilder.buildElement(xml);
                List<Element> wmsStoreElements = element.getChildren("wmsStore");
                List<String> names = new ArrayList<String>();
                for (Element wmsStoreElement : wmsStoreElements) {
                    names.add(wmsStoreElement.getChildText("name"));
                }
                Collections.sort(names);
                wmsStores.addAll(names);
            }
            // Display the Workspace (but only if we are showing more than one)
            if (workspaces.size() > 1) {
                if (workspaceCounter > 0) {
                    builder.append(OsUtils.LINE_SEPARATOR);
                }
                builder.append(w).append(OsUtils.LINE_SEPARATOR);
                builder.append(Strings.repeat("-", w.length())).append(OsUtils.LINE_SEPARATOR);
            }
            for (String wmsStore : wmsStores) {
                String indent = Strings.repeat(" ", workspaces.size() > 1 ? 3 : 0);
                // Display the WMSStore (but only if we are showing more than one)
                if (wmsStores.size() > 1) {
                    builder.append(OsUtils.LINE_SEPARATOR);
                    builder.append(indent).append(wmsStore).append(OsUtils.LINE_SEPARATOR);
                    builder.append(indent).append(Strings.repeat("-", wmsStore.length())).append(OsUtils.LINE_SEPARATOR);
                }
                // Get the List of WMS Layers
                String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(w) + "/wmsstores/" + URLUtil.encode(wmsStore) + "/wmslayers.xml?list=available";
                String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
                Element element = JDOMBuilder.buildElement(xml);
                List<Element> wmsStoreElements = element.getChildren("wmsLayerName");
                List<String> names = new ArrayList<String>();
                for (Element wmsStoreElement : wmsStoreElements) {
                    names.add(wmsStoreElement.getTextTrim());
                }
                Collections.sort(names);
                for (String name : names) {
                    builder.append(indent).append(name).append(OsUtils.LINE_SEPARATOR);
                }
            }
            workspaceCounter++;
        }
        return builder.toString();
    }

    @CliCommand(value = "wmsstore layer get", help = "List the Layers in a WMS Store.")
    public String getLayer(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "store", mandatory = true, help = "The WMS Store") String store,
            @CliOption(key = "layer", mandatory = true, help = "The WMS Layer") String layer
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/wmsstores/" + URLUtil.encode(store) + "/wmslayers/" + URLUtil.encode(layer) + ".xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        Element wmsLayerElement = JDOMBuilder.buildElement(xml);
        String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(wmsLayerElement.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Native Name: ").append(wmsLayerElement.getChildText("nativeName")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Title: ").append(wmsLayerElement.getChildText("title")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Description: ").append(wmsLayerElement.getChildText("description")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Enabled: ").append(wmsLayerElement.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Advertised: ").append(wmsLayerElement.getChildText("advertised")).append(OsUtils.LINE_SEPARATOR);
        Element nsElement = wmsLayerElement.getChild("namespace");
        if (nsElement != null) {
            builder.append(TAB).append("Namespace: ").append(nsElement.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        }
        builder.append(TAB).append("Keywords: ").append(OsUtils.LINE_SEPARATOR);
        Element keywordsElement = wmsLayerElement.getChild("keywords");
        if (keywordsElement != null) {
            List<Element> keywordElements = keywordsElement.getChildren("string");
            for (Element elem : keywordElements) {
                builder.append(TAB).append(TAB).append(elem.getTextTrim()).append(OsUtils.LINE_SEPARATOR);
            }
        }
        builder.append(TAB).append("Native CRS: ").append(wmsLayerElement.getChildText("nativeCRS")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("SRS: ").append(wmsLayerElement.getChildText("srs")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Projection Policy: ").append(wmsLayerElement.getChildText("projectionPolicy")).append(OsUtils.LINE_SEPARATOR);
        Element nativeBoundingBoxElement = wmsLayerElement.getChild("nativeBoundingBox");
        builder.append(TAB).append("Native Bounding Box: ")
                .append(nativeBoundingBoxElement.getChildText("minx")).append(",")
                .append(nativeBoundingBoxElement.getChildText("miny")).append(",")
                .append(nativeBoundingBoxElement.getChildText("maxx")).append(",")
                .append(nativeBoundingBoxElement.getChildText("maxy")).append(" ")
                .append(nativeBoundingBoxElement.getChildText("crs"))
                .append(OsUtils.LINE_SEPARATOR);
        Element latLonBoundingBoxElement = wmsLayerElement.getChild("latLonBoundingBox");
        builder.append(TAB).append("LatLon Bounding Box: ")
                .append(latLonBoundingBoxElement.getChildText("minx")).append(",")
                .append(latLonBoundingBoxElement.getChildText("miny")).append(",")
                .append(latLonBoundingBoxElement.getChildText("maxx")).append(",")
                .append(latLonBoundingBoxElement.getChildText("maxy")).append(" ")
                .append(nativeBoundingBoxElement.getChildText("crs"))
                .append(OsUtils.LINE_SEPARATOR);
        Element metadataElement = wmsLayerElement.getChild("metadata");
        if (metadataElement != null) {
            List<Element> metadataElements = metadataElement.getChildren("entry");
            builder.append(TAB).append("Metadata: ").append(OsUtils.LINE_SEPARATOR);
            for (Element elem : metadataElements) {
                builder.append(TAB).append(TAB).append(elem.getAttributeValue("key")).append(" = ").append(elem.getTextTrim()).append(OsUtils.LINE_SEPARATOR);
            }
        }

        Element storeElement = wmsLayerElement.getChild("store");
        if (storeElement != null) {
            builder.append(TAB).append("Store: ").append(storeElement.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        }

        return builder.toString();
    }

    @CliCommand(value = "wmsstore layer create", help = "Create or publish a Layer in a WMS Store.")
    public boolean createWmsLayer(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "store", mandatory = true, help = "The WMS Store") String store,
            @CliOption(key = "layer", mandatory = true, help = "The WMS Layer") String layer,
            @CliOption(key = "title", mandatory = false, help = "The title") String title,
            @CliOption(key = "description", mandatory = false, help = "The description") String description,
            @CliOption(key = "keywords", mandatory = false, help = "The comma delimited list of keywords") String keywords,
            @CliOption(key = "enabled", mandatory = false, unspecifiedDefaultValue = "true", help = "The enabled flag") boolean enabled,
            @CliOption(key = "advertised", mandatory = false, unspecifiedDefaultValue = "true", help = "The advertised flag") boolean advertised,
            @CliOption(key = "srs", mandatory = false, help = "The SRS") String srs,
            @CliOption(key = "projectionpolicy", mandatory = false, help = "The projection policy") String projectionPolicy,
            @CliOption(key = "recalculate", mandatory = false, help = "Recalculate bounding boxes: nativebbox,latlonbbox", unspecifiedDefaultValue = "") String recalculate
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/wmsstores/" + URLUtil.encode(store) + "/wmslayers.xml";
        if (recalculate != null) {
            url += "?recalculate=" + recalculate;
        }
        Element element = new Element("wmsLayer");
        element.addContent(new Element("name").setText(layer));
        element.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        element.addContent(new Element("advertised").setText(String.valueOf(advertised)));
        if (title != null) {
            element.addContent(new Element("title").setText(title));
        }
        if (description != null) {
            element.addContent(new Element("description").setText(description));
        }
        if (keywords != null) {
            String[] keys = keywords.split(",");
            if (keys.length > 0) {
                Element keywordsElement = new Element("keywords");
                for (String key : keys) {
                    keywordsElement.addContent(new Element("string").setText(key));
                }
                element.addContent(keywordsElement);
            }
        }
        if (srs != null) {
            element.addContent(new Element("srs").setText(srs));
        }
        if (projectionPolicy != null) {
            element.addContent(new Element("projectionPolicy").setText(projectionPolicy));
        }
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.postXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "wmsstore layer modify", help = "Modify a published WMS Store Layer.")
    public boolean modifyWmsLayer(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "store", mandatory = true, help = "The WMS Store") String store,
            @CliOption(key = "layer", mandatory = true, help = "The WMS Layer") String layer,
            @CliOption(key = "title", mandatory = false, help = "The title") String title,
            @CliOption(key = "description", mandatory = false, help = "The description") String description,
            @CliOption(key = "keywords", mandatory = false, help = "The comma delimited list of keywords") String keywords,
            @CliOption(key = "enabled", mandatory = false, help = "The enabled flag") String enabled,
            @CliOption(key = "advertised", mandatory = false, help = "The advertised flag") String advertised,
            @CliOption(key = "srs", mandatory = false, help = "The SRS") String srs,
            @CliOption(key = "projectionpolicy", mandatory = false, help = "The projection policy") String projectionPolicy,
            @CliOption(key = "recalculate", mandatory = false, help = "Recalculate bounding boxes: nativebbox,latlonbbox") String recalculate
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/wmsstores/" + URLUtil.encode(store) + "/wmslayers/" + URLUtil.encode(layer) + ".xml";
        if (recalculate != null) {
            url += "?recalculate=" + recalculate;
        }
        Element element = new Element("wmsLayer");
        element.addContent(new Element("name").setText(layer));
        if (enabled != null) {
            element.addContent(new Element("enabled").setText(enabled));
        }
        if (advertised != null) {
            element.addContent(new Element("advertised").setText(advertised));
        }
        if (title != null) {
            element.addContent(new Element("title").setText(title));
        }
        if (description != null) {
            element.addContent(new Element("description").setText(description));
        }
        if (keywords != null) {
            String[] keys = keywords.split(",");
            if (keys.length > 0) {
                Element keywordsElement = new Element("keywords");
                for (String key : keys) {
                    keywordsElement.addContent(new Element("string").setText(key));
                }
                element.addContent(keywordsElement);
            }
        }
        if (srs != null) {
            element.addContent(new Element("srs").setText(srs));
        }
        if (projectionPolicy != null) {
            element.addContent(new Element("projectionPolicy").setText(projectionPolicy));
        }
        String content = JDOMUtil.toString(element);
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "wmsstore layer delete", help = "Delete a published WMS Store Layer.")
    public boolean deleteWmsLayer(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "store", mandatory = true, help = "The WMS Store") String store,
            @CliOption(key = "layer", mandatory = true, help = "The WMS Layer") String layer,
            @CliOption(key = "recurse", mandatory = false, help = "Whether to delete all associated layers", unspecifiedDefaultValue = "false", specifiedDefaultValue = "false") boolean recurse
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/wmsstores/" + URLUtil.encode(store) + "/wmslayers/" + URLUtil.encode(layer) + ".xml?recurse=" + recurse;
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }
}
