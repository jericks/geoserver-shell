package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.*;
import it.geosolutions.geoserver.rest.encoder.feature.FeatureTypeAttribute;
import it.geosolutions.geoserver.rest.encoder.feature.GSAttributeEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CoverageCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    @CliCommand(value = "coverage list", help = "List coverages.")
    public String list(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coverage store") String coveragestore
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTCoverageList list = reader.getCoverages(workspace, coveragestore);
        List<String> names = list.getNames();
        StringBuilder builder = new StringBuilder();
        for(String name : names) {
            builder.append(name).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "coverage get", help = "Get a coverage.")
    public String get(
            @CliOption(key = {"", "workspace"}, mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coverage store") String coveragestore,
            @CliOption(key = "coverage", mandatory = true, help = "The coverage") String coverage
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTCoverage cov = reader.getCoverage(workspace, coveragestore, coverage);

        String TAB = "   ";
        StringBuilder builder = new StringBuilder();
        builder.append(cov.getName()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Namespace: ").append(cov.getNameSpace()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Title: ").append(cov.getTitle()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Abstract: ").append(cov.getAbstract()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Native Name: ").append(cov.getNativeName()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Native Format: ").append(cov.getNativeFormat()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Store Name: ").append(cov.getStoreName()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Store Type: ").append(cov.getStoreType()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Store URL: ").append(cov.getStoreUrl()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("BBox: ").append(cov.getMinX()).append(",").append(cov.getMinY()).append(",").append(cov.getMaxX()).append(",").append(cov.getMaxY()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("SRS: ").append(cov.getSRS()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Native CRS: ").append(cov.getNativeCRS()).append(OsUtils.LINE_SEPARATOR);

        builder.append("Dimension Info: ").append(OsUtils.LINE_SEPARATOR);
        for(RESTDimensionInfo info : cov.getDimensionInfo()) {
            String key = info.getKey();
            String presentation = info.getPresentation();
            String resolution = info.getResolution();
            builder.append(TAB).append(key).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append(presentation).append(OsUtils.LINE_SEPARATOR);
            builder.append(TAB).append(TAB).append(resolution).append(OsUtils.LINE_SEPARATOR);
        }

        builder.append("Metadata List: ").append(OsUtils.LINE_SEPARATOR);
        RESTMetadataList metadataList = cov.getMetadataList();
        for(int i=0; i<metadataList.size(); i++) {
            RESTMetadataList.RESTMetadataElement elem = metadataList.get(i);
            String key = elem.getKey();
            String value = elem.getMetadataElem().getTextTrim();
            builder.append(TAB).append(key).append(": ").append(value).append(OsUtils.LINE_SEPARATOR);
        }

        // @TODO throws null pointer exception with:
        // acoverage get --workspace nurc --coveragestore mosaic --coverage mosaic
        /*builder.append("Attribute List: ").append(OsUtils.LINE_SEPARATOR);
        if (cov.getAttributeList() != null) {
            for(Map<FeatureTypeAttribute, String> attributes : cov.getAttributeList()) {
                for(Map.Entry<FeatureTypeAttribute, String> attr: attributes.entrySet()) {
                    builder.append(TAB).append(attr.getKey()).append(": ").append(attr.getValue()).append(OsUtils.LINE_SEPARATOR);
                }
            }
        }*/

        return builder.toString();
    }
}
