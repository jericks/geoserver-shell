package org.geoserver.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class WorldImageCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({"worldimage publish", "worldimage zip"})
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    @CliCommand(value = "worldimage publish", help = "Publish a World Image.")
    public boolean publish(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = false, help = "The coveragestore") String coveragestore,
            @CliOption(key = "file", mandatory = true, help = "The zipped worldimage file") File file,
            @CliOption(key = "coverage", mandatory = false, help = "The coverage name") String coverage
    ) throws Exception {
        CoverageStoreCommands coverageStoreCommands = new CoverageStoreCommands();
        coverageStoreCommands.setGeoserver(geoserver);
        return coverageStoreCommands.upload(workspace, coveragestore, file, "worldimage", "first", coverage, null);
    }

    @CliCommand(value = "worldimage zip", help = "Zip a worldimage.")
    public boolean zip(
            @CliOption(key = "file", mandatory = true, help = "The worldimage file") File worldImageFile,
            @CliOption(key = "zipfile", mandatory = false, help = "The output zipfile") File zipfile
    ) throws Exception {
        int filesZipped = 0;
        int lastPeriod =  worldImageFile.getName().lastIndexOf(".");
        String fileExt = worldImageFile.getName().substring(lastPeriod + 1);
        String baseName = worldImageFile.getName().substring(0, lastPeriod);
        List<String> extentions = new ArrayList<String>();
        extentions.add(fileExt);
        extentions.add("wld");
        extentions.add("prj");
        if (fileExt.equalsIgnoreCase("jpeg") || fileExt.equalsIgnoreCase("jpg")) {
            extentions.add("jgw");
        } else if (fileExt.equalsIgnoreCase("tif") || fileExt.equalsIgnoreCase("tiff")) {
            extentions.add("tfw");
        } else if (fileExt.equalsIgnoreCase("png")) {
            extentions.add("pgw");
        } else if (fileExt.equalsIgnoreCase("gif")) {
            extentions.add("gfw");
        }
        if (zipfile == null) {
            zipfile = new File(worldImageFile.getParentFile(), baseName + ".zip");
        }
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipfile));
        zipOut.setMethod(ZipOutputStream.DEFLATED);
        byte[] bytes = new byte[1024];
        for (String ext : extentions) {
            File file = new File(worldImageFile.getParentFile(), baseName + "." + ext);
            if (file.exists()) {
                filesZipped++;
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);
                FileInputStream in = new FileInputStream(file);
                int len;
                while ((len = in.read(bytes)) > 0) {
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
