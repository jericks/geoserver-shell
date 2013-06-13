package org.geoserver.shell;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageList;
import it.geosolutions.geoserver.rest.decoder.RESTDimensionInfo;
import it.geosolutions.geoserver.rest.decoder.RESTMetadataList;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.FeatureTypeAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class CoverageCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliCommand(value = "coverage list", help = "List coverages.")
    public String list(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coverage store") String coveragestore
    ) throws Exception {
        GeoServerRESTReader reader = new GeoServerRESTReader(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        RESTCoverageList list = reader.getCoverages(workspace, coveragestore);
        List<String> names = list.getNames();
        Collections.sort(names);
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "coverage get", help = "Get a coverage.")
    public String get(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
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
        builder.append(TAB).append("BBox: ").append(cov.getMinX()).append(",").append(cov.getMinY()).append(",").append(cov.getMaxX()).append(",").append(cov.getMaxY()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("SRS: ").append(cov.getSRS()).append(OsUtils.LINE_SEPARATOR);
        builder.append(TAB).append("Native CRS: ").append(cov.getNativeCRS()).append(OsUtils.LINE_SEPARATOR);
        try {
            builder.append(TAB).append("Dimension Info: ").append(OsUtils.LINE_SEPARATOR);
            for (RESTDimensionInfo info : cov.getDimensionInfo()) {
                String key = info.getKey();
                String presentation = info.getPresentation();
                String resolution = info.getResolution();
                builder.append(TAB).append(TAB).append(key).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append(TAB).append(TAB).append(presentation).append(OsUtils.LINE_SEPARATOR);
                builder.append(TAB).append(TAB).append(TAB).append(resolution).append(OsUtils.LINE_SEPARATOR);
            }
        } catch (Exception ex) {
            // @TODO Fix RESTCoverage so it doesn't throw an exception if no XML element exists
        }
        try {
            builder.append(TAB).append("Metadata List: ").append(OsUtils.LINE_SEPARATOR);
            RESTMetadataList metadataList = cov.getMetadataList();
            for (int i = 0; i < metadataList.size(); i++) {
                RESTMetadataList.RESTMetadataElement elem = metadataList.get(i);
                String key = elem.getKey();
                String value = elem.getMetadataElem().getTextTrim();
                builder.append(TAB).append(TAB).append(key).append(": ").append(value).append(OsUtils.LINE_SEPARATOR);
            }
        } catch (Exception ex) {
            // @TODO Fix RESTCoverage so it doesn't throw an exception if no XML element exists
        }
        try {
            builder.append(TAB).append("Attribute List: ").append(OsUtils.LINE_SEPARATOR);
            for (Map<FeatureTypeAttribute, String> attributes : cov.getAttributeList()) {
                for (Map.Entry<FeatureTypeAttribute, String> attr : attributes.entrySet()) {
                    builder.append(TAB).append(TAB).append(attr.getKey()).append(": ").append(attr.getValue()).append(OsUtils.LINE_SEPARATOR);
                }
            }
        } catch (Exception ex) {
            // @TODO Fix RESTCoverage so it doesn't throw an exception if no XML element exists
        }
        return builder.toString();
    }

    @CliCommand(value = "coverage create", help = "Create a coverage.")
    public boolean create(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coverage store") String coveragestore,
            @CliOption(key = "coverage", mandatory = true, help = "The coverage") String coverage,
            @CliOption(key = "title", mandatory = false, help = "The title") String title,
            @CliOption(key = "description", mandatory = false, help = "The description") String description,
            @CliOption(key = "keywords", mandatory = false, help = "The keywords") String keywords,
            @CliOption(key = "srs", mandatory = false, help = "The srs") String srs,
            @CliOption(key = "enabled", mandatory = false, unspecifiedDefaultValue = "true", help = "The enabled flag") boolean enabled
    ) throws Exception {
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(geoserver.getUrl(), geoserver.getUser(), geoserver.getPassword());
        GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
        coverageEncoder.setName(coverage);
        coverageEncoder.setEnabled(enabled);
        if (keywords != null) {
            String[] keys = keywords.split(",");
            for (String key : keys) {
                coverageEncoder.addKeyword(key);
            }
        }
        if (title != null) coverageEncoder.setTitle(title);
        if (description != null) coverageEncoder.setDescription(title);
        if (srs != null) coverageEncoder.setSRS(srs);
        return publisher.createCoverage(workspace, coveragestore, coverageEncoder);
    }

    @CliCommand(value = "coverage modify", help = "Modify a coverage.")
    public boolean modify(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coverage store") String coveragestore,
            @CliOption(key = "coverage", mandatory = true, help = "The coverage") String coverage,
            @CliOption(key = "title", mandatory = false, help = "The title") String title,
            @CliOption(key = "description", mandatory = false, help = "The description") String description,
            @CliOption(key = "keywords", mandatory = false, help = "The keywords") String keywords,
            @CliOption(key = "srs", mandatory = false, help = "The srs") String srs,
            @CliOption(key = "enabled", mandatory = false, help = "The enabled flag") String enabled
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/coveragestores/" + URLUtil.encode(coveragestore) + "/coverages/" + URLUtil.encode(coverage) + ".xml";
        GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
        coverageEncoder.setName(coverage);
        if (enabled != null) coverageEncoder.setEnabled(Boolean.valueOf(enabled));
        if (keywords != null) {
            String[] keys = keywords.split(",");
            for (String key : keys) {
                coverageEncoder.addKeyword(key);
            }
        }
        if (title != null) coverageEncoder.setTitle(title);
        if (description != null) coverageEncoder.setDescription(title);
        if (srs != null) coverageEncoder.setSRS(srs);
        String content = coverageEncoder.toString();
        String response = HTTPUtils.putXml(url, content, geoserver.getUser(), geoserver.getPassword());
        return response != null;
    }

    @CliCommand(value = "coverage delete", help = "Delete a coverage.")
    public boolean delete(
            @CliOption(key = "workspace", mandatory = true, help = "The workspace") String workspace,
            @CliOption(key = "coveragestore", mandatory = true, help = "The coverage store") String coveragestore,
            @CliOption(key = "coverage", mandatory = true, help = "The coverage") String coverage,
            @CliOption(key = "recurse", mandatory = false, unspecifiedDefaultValue = "false", help = "Whether to also delete associated Layer") boolean recurse
    ) throws Exception {
        String url = geoserver.getUrl() + "/rest/workspaces/" + URLUtil.encode(workspace) + "/coveragestores/" + URLUtil.encode(coveragestore) + "/coverages/" + URLUtil.encode(coverage) + ".xml?recurse=" + recurse;
        return HTTPUtils.delete(url, geoserver.getUser(), geoserver.getPassword());
    }
}
