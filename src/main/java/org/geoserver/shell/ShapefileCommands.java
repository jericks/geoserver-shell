package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ShapefileCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "shapefile publish", help = "Publish a shapefile.")
    public boolean publish(
        @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
        @CliOption(key = "datastore", mandatory = false, help = "The datastore") String datastore,
        @CliOption(key = "layer", mandatory = false, help = "The layer name") String layer,
        @CliOption(key = "file", mandatory = true, help = "The zipped shapefile") File file,
        @CliOption(key = "srs", mandatory = false, unspecifiedDefaultValue = "EPSG:4326", help = "The EPSG srs code") String srs,
        @CliOption(key = "style", mandatory = false, help = "The style") String style
    ) throws Exception {
        String baseName = file.getName().substring(0, file.getName().lastIndexOf(".zip"));
        if (datastore == null) {
            datastore = baseName;
        }
        if (layer == null) {
            layer = baseName;
        }
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        return publisher.publishShp(workspace, datastore, layer, file, srs, style);
    }

    @CliCommand(value = "shapefile zip", help = "Zip a shapefile.")
    public boolean zip(
        @CliOption(key = "shapefile", mandatory = true, help = "The shapefile") File shapefile,
        @CliOption(key = "zipfile", mandatory = true, help = "The output zipfile") File zipfile
    ) throws Exception {
        int filesZipped = 0;
        String baseName = shapefile.getName().substring(0, shapefile.getName().lastIndexOf(".shp"));
        String[] exts = {"shp","dbf","prj","shx","fix","sbn","sbx"};
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipfile));
        zipOut.setMethod(ZipOutputStream.DEFLATED);
        byte[] bytes = new byte[1024];
        for(String ext: exts) {
            File file = new File(shapefile.getParentFile(), baseName + "." + ext);
            if (file.exists()) {
                filesZipped++;
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);
                FileInputStream in = new FileInputStream(file);
                int len;
                while((len = in.read(bytes)) > 0) {
                    zipOut.write(bytes, 0, len);
                }
                zipOut.closeEntry();
                in.close();
            }
        }
        zipOut.close();
        return filesZipped > 0;
    }
}
