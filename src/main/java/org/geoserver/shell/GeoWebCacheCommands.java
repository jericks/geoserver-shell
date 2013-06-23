package org.geoserver.shell;

import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONObject;
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
public class GeoWebCacheCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({"gwc layer list", "gwc layer get", "gwc geoserver layer create",
            "gwc geoserver layer modify", "gwc wms layer create", "gwc wms layer modify", "gwc layer delete",
            "gwc status", "gwc kill", "gwc seed", "gwc reseed", "gwc truncate"
    })
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    // @TODO http://docs.geoserver.org/stable/en/user/geowebcache/rest/layers.html#layer-list has /gwc/rest/seed/layers
    // if should be /gwc/rest/layers
    @CliCommand(value = "gwc layer list", help = "List GeoWebCache Layers.")
    public String listLayers() throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/layers";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        Element element = JDOMBuilder.buildElement(xml);
        List<String> names = new ArrayList<String>();
        List<Element> layerElements = element.getChildren("layer");
        for (Element layerElement : layerElements) {
            names.add(layerElement.getChildText("name"));
        }
        Collections.sort(names);
        for (String name : names) {
            builder.append(name).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "gwc layer get", help = "Get a GeoWebCache Layer.")
    public String getLayer(
            @CliOption(key = "name", mandatory = true, help = "The layer name") String name
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/layers/" + URLUtil.encode(name) + ".xml";
        String xml = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        StringBuilder builder = new StringBuilder();
        String TAB = "   ";
        Element element = JDOMBuilder.buildElement(xml);
        builder.append(element.getChildText("name")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Enabled: ").append(element.getChildText("enabled")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Gutter: ").append(element.getChildText("gutter")).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Auto Cache Styles: ").append(element.getChildText("autoCacheStyles")).append(OsUtils.LINE_SEPARATOR);
        Element mimeFormatsElement = element.getChild("mimeFormats");
        if (mimeFormatsElement != null) {
            List<Element> mimeFormatsElements = mimeFormatsElement.getChildren("string");
            if (mimeFormatsElements.size() > 0) {
                builder.append(TAB).append("Mime Formats:").append(OsUtils.LINE_SEPARATOR);
                for (Element mimeFormatElement : mimeFormatsElements) {
                    builder.append(TAB).append(TAB).append(mimeFormatElement.getTextTrim()).append(OsUtils.LINE_SEPARATOR);
                }
            }
        }
        Element gridSubsetsElement = element.getChild("gridSubsets");
        if (gridSubsetsElement != null) {
            List<Element> gridSubsetElements = gridSubsetsElement.getChildren("gridSubset");
            if (gridSubsetElements.size() > 0) {
                builder.append(TAB).append("Grid Subsets:").append(OsUtils.LINE_SEPARATOR);
                for (Element gridSubsetElement : gridSubsetElements) {
                    builder.append(TAB).append(TAB).append(gridSubsetElement.getChildText("gridSetName")).append(OsUtils.LINE_SEPARATOR);
                }
            }
        }
        Element metaWidthHeightElement = element.getChild("metaWidthHeight");
        if (metaWidthHeightElement != null) {
            List<Element> metaWidthHeightElements = metaWidthHeightElement.getChildren("int");
            builder.append(TAB).append("Meta Dimensions:").append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append("Width: ").append(metaWidthHeightElements.get(0).getText()).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append("Height: ").append(metaWidthHeightElements.get(1).getText()).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "gwc geoserver layer create", help = "Create a GeoWebCache Layer from a GeoServer Layer.")
    public boolean createGeoServerLayer(
            @CliOption(key = "name", mandatory = true, help = "The GeoServer layer name") String name,
            @CliOption(key = "enabled", mandatory = false, help = "The enabled flag", unspecifiedDefaultValue = "true") boolean enabled,
            @CliOption(key = "mimetype", mandatory = false, unspecifiedDefaultValue = "image/png", help = "The mime type") String mimeType,
            @CliOption(key = "gridsetname", mandatory = false, unspecifiedDefaultValue = "EPSG:900913", help = "The grid set name") String gridSetName,
            @CliOption(key = "gutter", mandatory = false, help = "The gutter") String gutter,
            @CliOption(key = "metawidth", mandatory = false, help = "The meta width") String metaWidth,
            @CliOption(key = "metaheight", mandatory = false, help = "The meta height") String metaHeight
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/layers/" + URLUtil.encode(name) + ".xml";
        Element element = new Element("GeoServerLayer");
        element.addContent(new Element("name").setText(name));
        element.addContent(new Element("mimeFormats").addContent(new Element("string").setText(mimeType)));
        element.addContent(new Element("gridSubsets").addContent(new Element("gridSubset").addContent(new Element("gridSetName").setText(gridSetName))));
        element.addContent(new Element("enabled").setText(String.valueOf(enabled)));
        if (gutter != null) {
            element.addContent(new Element("gutter").setText(gutter));
        }
        if (metaWidth != null && metaHeight != null) {
            Element metaWidthHeightElement = new Element("metaWidthHeight");
            metaWidthHeightElement.addContent(new Element("int").setText(metaWidth));
            metaWidthHeightElement.addContent(new Element("int").setText(metaHeight));
            element.addContent(metaWidthHeightElement);
        }
        String contents = JDOMUtil.toString(element);
        String response = HTTPUtils.putXml(url, contents, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "gwc geoserver layer modify", help = "Modify a GeoWebCache Layer from a GeoServer Layer.")
    public boolean modifyGeoServerLayer(
            @CliOption(key = "name", mandatory = true, help = "The GeoServer layer name") String name,
            @CliOption(key = "enabled", mandatory = false, help = "The enabled flag") String enabled,
            @CliOption(key = "mimetype", mandatory = false, help = "The mime type") String mimeType,
            @CliOption(key = "gridsetname", mandatory = false, help = "The grid set name") String gridSetName,
            @CliOption(key = "gutter", mandatory = false, help = "The gutter") String gutter,
            @CliOption(key = "metawidth", mandatory = false, help = "The meta width") String metaWidth,
            @CliOption(key = "metaheight", mandatory = false, help = "The meta height") String metaHeight
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/layers/" + URLUtil.encode(name) + ".xml";
        Element element = new Element("GeoServerLayer");
        element.addContent(new Element("name").setText(name));
        if (mimeType != null) {
            element.addContent(new Element("mimeFormats").addContent(new Element("string").setText(mimeType)));
        }
        if (gridSetName != null) {
            element.addContent(new Element("gridSubsets").addContent(new Element("gridSubset").addContent(new Element("gridSetName").setText(gridSetName))));
        }
        if (enabled != null) {
            element.addContent(new Element("enabled").setText(enabled));
        }
        if (gutter != null) {
            element.addContent(new Element("gutter").setText(gutter));
        }
        if (metaWidth != null && metaHeight != null) {
            Element metaWidthHeightElement = new Element("metaWidthHeight");
            metaWidthHeightElement.addContent(new Element("int").setText(metaWidth));
            metaWidthHeightElement.addContent(new Element("int").setText(metaHeight));
            element.addContent(metaWidthHeightElement);
        }
        String contents = JDOMUtil.toString(element);
        String response = HTTPUtils.postXml(url, contents, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "gwc wms layer create", help = "Create a GeoWebCache Layer from a WMS Layer.")
    public boolean createWMSLayer(
            @CliOption(key = "name", mandatory = true, help = "The layer name") String name,
            @CliOption(key = "wmsurl", mandatory = true, help = "The WMS URL") String wmsUrl,
            @CliOption(key = "wmslayers", mandatory = true, help = "The WMS layer names") String wmsLayers,
            @CliOption(key = "mimetype", mandatory = false, unspecifiedDefaultValue = "image/png", help = "The mime type") String mimeType,
            @CliOption(key = "gridsetname", mandatory = false, unspecifiedDefaultValue = "EPSG:900913", help = "The grid set name") String gridSetName,
            @CliOption(key = "transparent", mandatory = false, unspecifiedDefaultValue = "false", help = "The transparency flag") boolean transparent,
            @CliOption(key = "backgroundColor", mandatory = false, help = "The background color") String backgroundColor,
            @CliOption(key = "gutter", mandatory = false, help = "The gutter") String gutter,
            @CliOption(key = "metawidth", mandatory = false, help = "The meta width") String metaWidth,
            @CliOption(key = "metaheight", mandatory = false, help = "The meta height") String metaHeight
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/layers/" + URLUtil.encode(name) + ".xml";
        Element element = new Element("wmsLayer");
        element.addContent(new Element("name").setText(name));
        // Mime Formats
        String[] mimeTypes = mimeType.split(",");
        Element mimeFormatsElement = new Element("mimeFormats");
        for (String mime : mimeTypes) {
            mimeFormatsElement.addContent(new Element("string").setText(mime));
        }
        element.addContent(mimeFormatsElement);
        // Grid Set Names
        String[] gridSetNames = gridSetName.split(",");
        Element gridSubsetsElement = new Element("gridSubsets");
        for (String grid : gridSetNames) {
            gridSubsetsElement.addContent(new Element("gridSubset").addContent(new Element("gridSetName").setText(grid)));
        }
        element.addContent(gridSubsetsElement);
        // Other
        element.addContent(new Element("transparent").setText(String.valueOf(transparent)));
        if (backgroundColor != null) {
            element.addContent(new Element("bgColor").setText(backgroundColor));
        }
        if (gutter != null) {
            element.addContent(new Element("gutter").setText(gutter));
        }
        if (metaWidth != null && metaHeight != null) {
            Element metaWidthHeightElement = new Element("metaWidthHeight");
            metaWidthHeightElement.addContent(new Element("int").setText(metaWidth));
            metaWidthHeightElement.addContent(new Element("int").setText(metaHeight));
            element.addContent(metaWidthHeightElement);
        }
        // WMS
        element.addContent(new Element("wmsUrl").addContent(new Element("string").setText(wmsUrl)));
        element.addContent(new Element("wmsLayers").setText(wmsLayers));
        String contents = JDOMUtil.toString(element);
        String response = HTTPUtils.putXml(url, contents, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "gwc wms layer modify", help = "Modify a GeoWebCache Layer from a WMS Layer.")
    public boolean modifyWMSLayer(
            @CliOption(key = "name", mandatory = true, help = "The layer name") String name,
            @CliOption(key = "wmsurl", mandatory = false, help = "The WMS URL") String wmsUrl,
            @CliOption(key = "wmslayers", mandatory = false, help = "The WMS layer names") String wmsLayers,
            @CliOption(key = "mimetype", mandatory = false, help = "The mime type") String mimeType,
            @CliOption(key = "gridsetname", mandatory = false, help = "The grid set name") String gridSetName,
            @CliOption(key = "transparent", mandatory = false, help = "The transparency flag") String transparent,
            @CliOption(key = "backgroundColor", mandatory = false, help = "The background color") String backgroundColor,
            @CliOption(key = "gutter", mandatory = false, help = "The gutter") String gutter,
            @CliOption(key = "metawidth", mandatory = false, help = "The meta width") String metaWidth,
            @CliOption(key = "metaheight", mandatory = false, help = "The meta height") String metaHeight
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/layers/" + URLUtil.encode(name) + ".xml";
        // @TODO This is strange, when creating the root element is <wmslayer> but when modifying it is <GeoServerLayer>
        Element element = new Element("GeoServerLayer");
        element.addContent(new Element("name").setText(name));
        if (mimeType != null) {
            String[] mimeTypes = mimeType.split(",");
            Element mimeFormatsElement = new Element("mimeFormats");
            for (String mime : mimeTypes) {
                mimeFormatsElement.addContent(new Element("string").setText(mime));
            }
            element.addContent(mimeFormatsElement);
        }
        if (gridSetName != null) {
            String[] gridSetNames = gridSetName.split(",");
            Element gridSubsetsElement = new Element("gridSubsets");
            for (String grid : gridSetNames) {
                gridSubsetsElement.addContent(new Element("gridSubset").addContent(new Element("gridSetName").setText(grid)));
            }
            element.addContent(gridSubsetsElement);
        }
        if (wmsUrl != null) {
            element.addContent(new Element("wmsUrl").addContent(new Element("string").setText(wmsUrl)));
        }
        if (wmsLayers != null) {
            element.addContent(new Element("wmsLayers").setText(wmsLayers));
        }
        if (transparent != null) {
            element.addContent(new Element("transparent").setText(String.valueOf(transparent)));
        }
        if (backgroundColor != null) {
            element.addContent(new Element("bgColor").setText(backgroundColor));
        }
        if (gutter != null) {
            element.addContent(new Element("gutter").setText(gutter));
        }
        if (metaWidth != null && metaHeight != null) {
            Element metaWidthHeightElement = new Element("metaWidthHeight");
            metaWidthHeightElement.addContent(new Element("int").setText(metaWidth));
            metaWidthHeightElement.addContent(new Element("int").setText(metaHeight));
            element.addContent(metaWidthHeightElement);
        }
        String contents = JDOMUtil.toString(element);
        String response = HTTPUtils.postXml(url, contents, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "gwc layer delete", help = "Delete a GeoWebCache Layer.")
    public boolean deleteLayer(
            @CliOption(key = "name", mandatory = true, help = "The layer name") String name
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/layers/" + URLUtil.encode(name) + ".xml";
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }

    @CliCommand(value = "gwc status", help = "Check the status of a seeding operation.")
    public String status(
            @CliOption(key = "name", mandatory = false, help = "The layer name") String name
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/seed";
        if (name != null) {
            url += "/" + URLUtil.encode(name);
        }
        url += ".json";
        String response = HTTPUtils.get(url, geoserver.getUser(), geoserver.getPassword());
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("long-array-array");
        StringBuilder builder = new StringBuilder();
        String TAB = "   ";
        builder.append("Tasks:").append(OsUtils.LINE_SEPARATOR);
        int len = jsonArray.length();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                JSONArray taskArray = jsonArray.getJSONArray(i);
                long tilesProcessed = taskArray.getLong(0);
                long totalTilesToProcess = taskArray.getLong(1);
                long numberRemainingTiles = taskArray.getLong(2);
                long taskId = taskArray.getLong(3);
                int status = taskArray.getInt(4);
                String statusStr = "";
                if (status == -1) {
                    statusStr = "Aborted";
                } else if (status == 0) {
                    statusStr = "Pending";
                } else if (status == 1) {
                    statusStr = "Running";
                } else if (status == 2) {
                    statusStr = "Done";
                }
                builder.append(TAB).append("Task ID: ").append(taskId).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append(TAB).append("Status: ").append(statusStr).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append(TAB).append("# Tile Processed: ").append(tilesProcessed).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append(TAB).append("Total # of Tiles to Process: ").append(totalTilesToProcess).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append(TAB).append("# of Remaining Tiles: ").append(numberRemainingTiles).append(OsUtils.LINE_SEPARATOR);
            }
        } else {
            builder.append(TAB).append("None").append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "gwc kill", help = "Kill a seeding operation.")
    public boolean kill(
            @CliOption(key = "name", mandatory = false, help = "The layer name") String name,
            @CliOption(key = "tasks", mandatory = false, unspecifiedDefaultValue = "all", help = "Which tasks to kill (all, running, pending)") String tasks
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/seed";
        if (name != null) {
            url += "/" + URLUtil.encode(name);
        }
        PostMethod postMethod = new PostMethod(url);
        postMethod.addParameter("kill_all", tasks);
        String response = HTTPUtils.post(url, postMethod.getRequestEntity(), geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "gwc seed", help = "Start a seeding operation.")
    public boolean seed(
            @CliOption(key = "name", mandatory = true, help = "The layer name") String name,
            @CliOption(key = "bounds", mandatory = false, help = "The bounds (minX,minY,maxX,maxY)") String bounds,
            @CliOption(key = "gridset", mandatory = true, help = "The grid set") String gridSet,
            @CliOption(key = "start", mandatory = true, help = "The start zoom level") int zoomStart,
            @CliOption(key = "stop", mandatory = true, help = "The stop zoom level") int zoomStop,
            @CliOption(key = "format", mandatory = false, unspecifiedDefaultValue = "image/png", help = "The image format") String format,
            @CliOption(key = "threads", mandatory = false, unspecifiedDefaultValue = "1", help = "The number of threads") int threads
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/seed/" + URLUtil.encode(name) + ".xml";
        String contents = createSeedXml("seed", name, bounds, gridSet, zoomStart, zoomStop, format, threads);
        String response = HTTPUtils.postXml(url, contents, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "gwc reseed", help = "Start a reseeding operation.")
    public boolean reseed(
            @CliOption(key = "name", mandatory = true, help = "The layer name") String name,
            @CliOption(key = "bounds", mandatory = false, help = "The bounds (minX,minY,maxX,maxY)") String bounds,
            @CliOption(key = "gridset", mandatory = true, help = "The grid set") String gridSet,
            @CliOption(key = "start", mandatory = true, help = "The start zoom level") int zoomStart,
            @CliOption(key = "stop", mandatory = true, help = "The stop zoom level") int zoomStop,
            @CliOption(key = "format", mandatory = false, unspecifiedDefaultValue = "image/png", help = "The image format") String format,
            @CliOption(key = "threads", mandatory = false, unspecifiedDefaultValue = "1", help = "The number of threads") int threads
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/seed/" + URLUtil.encode(name) + ".xml";
        String contents = createSeedXml("reseed", name, bounds, gridSet, zoomStart, zoomStop, format, threads);
        String response = HTTPUtils.postXml(url, contents, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "gwc truncate", help = "Start a truncate operation.")
    public boolean truncate(
            @CliOption(key = "name", mandatory = true, help = "The layer name") String name,
            @CliOption(key = "bounds", mandatory = false, help = "The bounds (minX,minY,maxX,maxY)") String bounds,
            @CliOption(key = "gridset", mandatory = true, help = "The grid set") String gridSet,
            @CliOption(key = "start", mandatory = true, help = "The start zoom level") int zoomStart,
            @CliOption(key = "stop", mandatory = true, help = "The stop zoom level") int zoomStop,
            @CliOption(key = "format", mandatory = false, unspecifiedDefaultValue = "image/png", help = "The image format") String format
    ) throws Exception {
        String url = geoserver.getUrl() + "/gwc/rest/seed/" + URLUtil.encode(name) + ".xml";
        String contents = createSeedXml("truncate", name, bounds, gridSet, zoomStart, zoomStop, format, 1);
        String response = HTTPUtils.postXml(url, contents, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    protected String createSeedXml(String type, String name, String bounds, String gridSet, int zoomStart, int zoomStop, String format, int threads) {
        Element element = new Element("seedRequest");
        element.addContent(new Element("name").setText(name));
        if (bounds != null) {
            String[] coords = bounds.split(",");
            if (coords.length == 4) {
                Element boundsElement = new Element("bounds");
                element.addContent(boundsElement);
                Element coordsElements = new Element("coords");
                boundsElement.addContent(coordsElements);
                coordsElements.addContent(new Element("double").setText(coords[0]));
                coordsElements.addContent(new Element("double").setText(coords[2]));
                coordsElements.addContent(new Element("double").setText(coords[1]));
                coordsElements.addContent(new Element("double").setText(coords[3]));
            }
        }
        element.addContent(new Element("gridSetId").setText(gridSet));
        element.addContent(new Element("zoomStart").setText(String.valueOf(zoomStart)));
        element.addContent(new Element("zoomStop").setText(String.valueOf(zoomStop)));
        element.addContent(new Element("format").setText(format));
        element.addContent(new Element("type").setText(type));
        element.addContent(new Element("threadCount").setText(String.valueOf(threads)));
        String str = JDOMUtil.toString(element);
        return str;
    }
}
