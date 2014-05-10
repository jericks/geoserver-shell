package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.HTTPUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

@Component
public class GeoserverCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({"geoserver reset", "geoserver reload", "geoserver backup",
            "geoserver restore", "geoserver verbose set", "geoserver show", "geoserver getmap",
            "geoserver getfeature", "geoserver getlegend"
    })
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    @CliCommand(value = "geoserver set", help = "Set the url, user, and password for Geoserver.")
    public boolean set(
            @CliOption(key = "url", mandatory = true, help = "The url") String url,
            @CliOption(key = "user", mandatory = false, help = "The user name", unspecifiedDefaultValue = "admin") String user,
            @CliOption(key = "password", mandatory = false, help = "The password", unspecifiedDefaultValue = "geoserver") String password
    ) {
        geoserver.setUrl(url);
        geoserver.setUser(user);
        geoserver.setPassword(password);
        // Ping URL with username and password
        String response = HTTPUtils.get(geoserver.getUrl() + "/rest/about/versions.xml", geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "geoserver verbose set", help = "Show the url, user, and password for Geoserver.")
    public boolean verbose(@CliOption(key = "value", mandatory = true, help = "The verbosity value") boolean verbose) {
        geoserver.setVerbose(verbose);
        return true;
    }

    @CliCommand(value = "geoserver show", help = "Show the url, user, and password for Geoserver.")
    public String show() {
        return geoserver.getUrl() + " " + geoserver.getUser() + " " + geoserver.getPassword();
    }

    @CliCommand(value = "geoserver reset", help = "Reset Geoserver's configuration.")
    public boolean reset() {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.reset();
    }

    @CliCommand(value = "geoserver reload", help = "Reload Geoserver's configuration.")
    public boolean reload() {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.reload();
    }

    @CliCommand(value = "geoserver backup", help = "Backup Geoserver's configuration.")
    public boolean backup(
            @CliOption(key = "directory", mandatory = true, help = "The backup ") String backupDir,
            @CliOption(key = "includedata", mandatory = false, unspecifiedDefaultValue = "false", help = "The include data flag") boolean includeData,
            @CliOption(key = "includegwc", mandatory = false, unspecifiedDefaultValue = "false", help = "The include GWC flag") boolean includeGwc,
            @CliOption(key = "includelog", mandatory = false, unspecifiedDefaultValue = "false", help = "The include log files flag") boolean includeLog
    ) {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        String result = publisher.backup(backupDir, includeData, includeGwc, includeLog);
        return result != null;
    }

    @CliCommand(value = "geoserver restore", help = "Restore Geoserver's configuration from a backup directory.")
    public boolean restore(
            @CliOption(key = "directory", mandatory = true, help = "The backup ") String backupDir
    ) {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        String result = publisher.restore(backupDir);
        return result != null;
    }

    @CliCommand(value = "geoserver getmap", help = "Get a map from the WMS service.")
    public String getMap(
            @CliOption(key = "layers", mandatory = true, help = "The layers to display") String layers,
            @CliOption(key = "file", mandatory = false, unspecifiedDefaultValue = "map.png", help = "The file name") String fileName,
            @CliOption(key = "width", mandatory = false, help = "The width") String width,
            @CliOption(key = "height", mandatory = false, help = "The height") String height,
            @CliOption(key = "format", mandatory = false, help = "The format") String format,
            @CliOption(key = "srs", mandatory = false, help = "The srs") String srs,
            @CliOption(key = "bbox", mandatory = false, help = "The bbox") String bbox
    ) throws Exception {
        String url = geoserver.getUrl() + "/wms/reflect?layers=" + URLUtil.encode(layers);
        if (width != null) {
            url += "&width=" + width;
        }
        if (height != null) {
            url += "&height=" + height;
        }
        if (srs != null) {
            url += "&srs=" + srs;
        }
        if (bbox != null) {
            url += "&bbox=" + bbox;
        }
        String formatMimeType = "image/png";
        String formatImageIO = "png";
        if (format != null) {
            if (format.equalsIgnoreCase("jpeg") || format.equalsIgnoreCase("jpg")
                    || format.equalsIgnoreCase("image/jpeg") || format.equalsIgnoreCase("image/jpg")) {
                formatMimeType = "image/jpeg";
                formatImageIO = "jpeg";
            }
            url += "&format=" + formatMimeType;
        }
        String message = fileName;
        BufferedImage image = ImageIO.read(new URL(url));
        if (image != null) {
            ImageIO.write(image, formatImageIO, new File(fileName));
        } else {
            message = "Unable to read URL (" + url + ")";
        }
        return message;
    }

    @CliCommand(value = "geoserver getfeature", help = "Get features from the WMF service.")
    public String getFeature(
            @CliOption(key = "typeName", mandatory = true, help = "The type name to query") String typeName,
            @CliOption(key = "version", unspecifiedDefaultValue = "1.0.0", mandatory = false, help = "The version") String version,
            @CliOption(key = "maxfeatures", mandatory = false, help = "The version") String maxFeatures,
            @CliOption(key = "sortby", mandatory = false, help = "The version") String sortBy,
            @CliOption(key = "propertyname", mandatory = false, help = "The version") String propertyName,
            @CliOption(key = "featureid", mandatory = false, help = "The version") String featureID,
            @CliOption(key = "bbox", mandatory = false, help = "The version") String bbox,
            @CliOption(key = "srs", mandatory = false, help = "The version") String srsName,
            @CliOption(key = "format", unspecifiedDefaultValue = "csv", mandatory = false, help = "The version") String outputFormat,
            @CliOption(key = "cql", mandatory = false, help = "The CQL query") String cql
    ) throws Exception {
        String url = geoserver.getUrl() + "/wfs?service=wfs&version=" + version + "&request=GetFeature&typeName=" + URLUtil.encode(typeName);
        url += "&outputFormat=" + outputFormat;
        if (maxFeatures != null) {
            url += "&maxFeatures=" + maxFeatures;
        }
        if (sortBy != null) {
            url += "&sortBy=" + sortBy;
        }
        if (propertyName != null) {
            url += "&propertyName=" + propertyName;
        }
        if (featureID != null) {
            url += "&featureID=" + featureID;
        }
        if (bbox != null) {
            url += "&bbox=" + bbox;
        }
        if (srsName != null) {
            url += "&srsName=" + srsName;
        }
        if (cql != null) {
            url += "&cql_filter=" + cql;
        }
        return IOUtils.toString(new URL(url));
    }

    @CliCommand(value = "geoserver getlegend", help = "Get a legend from the WMS service.")
    public String getLegend(
            @CliOption(key = "layer", mandatory = true, help = "The layer") String layer,
            @CliOption(key = "style", mandatory = false, help = "The style") String style,
            @CliOption(key = "featuretype", mandatory = false, help = "The feature type") String featureType,
            @CliOption(key = "rule", mandatory = false, help = "The rule") String rule,
            @CliOption(key = "scale", mandatory = false, help = "The scale") String scale,
            @CliOption(key = "file", mandatory = false, unspecifiedDefaultValue = "legend.png", help = "The file name") String fileName,
            @CliOption(key = "width", mandatory = false, help = "The width") String width,
            @CliOption(key = "height", mandatory = false, help = "The height") String height,
            @CliOption(key = "format", mandatory = false, unspecifiedDefaultValue = "png", help = "The format") String format
    ) throws Exception {
        String url = geoserver.getUrl() + "/wms?REQUEST=GetLegendGraphic&VERSION=1.0.0";
        url += "&LAYER=" + URLUtil.encode(layer);
        if (width != null) {
            url += "&WIDTH=" + width;
        }
        if (height != null) {
            url += "&HEIGHT=" + height;
        }
        if (style != null) {
            url += "&STYLE=" + style;
        }
        if (featureType != null) {
            url += "&FEATURETYPE=" + featureType;
        }
        if (rule != null) {
            url += "&RULE=" + rule;
        }
        if (scale != null) {
            url += "&SCALE=" + scale;
        }
        String formatMimeType = "image/png";
        String formatImageIO = "png";
        if (format.equalsIgnoreCase("jpeg") || format.equalsIgnoreCase("jpg")
                || format.equalsIgnoreCase("image/jpeg") || format.equalsIgnoreCase("image/jpg")) {
            formatMimeType = "image/jpeg";
            formatImageIO = "jpeg";
        }
        url += "&FORMAT=" + formatMimeType;
        String message = fileName;
        BufferedImage image = ImageIO.read(new URL(url));
        if (image != null) {
            ImageIO.write(image, formatImageIO, new File(fileName));
        } else {
            message = "Unable to read URL (" + url + ")";
        }
        return message;
    }
}