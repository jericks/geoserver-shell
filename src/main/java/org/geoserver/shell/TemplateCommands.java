package org.geoserver.shell;

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

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TemplateCommands implements CommandMarker {

    @Autowired
    private Geoserver geoserver;

    public void setGeoserver(Geoserver gs) {
        this.geoserver = gs;
    }

    @CliAvailabilityIndicator({"template list", "template create", "template modify", "template get", "template delete"})
    public boolean isCommandAvailable() {
        return geoserver.isSet();
    }

    // @ToDo Guard against bad combinations (workspace is required if datastore, if featureType then workspace and datastore are required)
    protected StringBuilder buildUrl(String base, String workspace, String datastore, String featureType, String coverageStore, String coverage) {
        StringBuilder urlBuilder = new StringBuilder(base).append("/rest");
        if (workspace != null) {
            urlBuilder.append("/workspaces/").append(URLUtil.encode(workspace));
            if (datastore != null) {
                urlBuilder.append("/datastores/").append(URLUtil.encode(datastore));
                if (featureType != null) {
                    urlBuilder.append("/featuretypes/").append(URLUtil.encode(featureType));
                }
            } else if (coverageStore != null) {
                urlBuilder.append("/coveragestores/").append(URLUtil.encode(coverageStore));
                if (coverage != null) {
                    urlBuilder.append("/coverages/").append(URLUtil.encode(coverage));
                }
            }
        }
        return urlBuilder;
    }

    @CliCommand(value = "template list", help = "List templates.")
    public String list(
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = false, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = false, help = "The featuretype") String featureType,
            @CliOption(key = "coveragestore", mandatory = false, help = "The coveragestore") String coverageStore,
            @CliOption(key = "coverage", mandatory = false, help = "The coverage") String coverage
    ) throws Exception {
        StringBuilder urlBuilder = buildUrl(geoserver.getUrl(), workspace, datastore, featureType, coverageStore, coverage);
        urlBuilder.append("/templates.xml");
        String xml = HTTPUtils.get(urlBuilder.toString(), geoserver.getUser(), geoserver.getPassword());
        Element rootElement = JDOMBuilder.buildElement(xml);
        List<Element> templateElements = rootElement.getChildren("template");
        List<String> names = new ArrayList<String>();
        Collections.sort(names);
        for (Element templateElement : templateElements) {
            names.add(templateElement.getChildText("name"));
        }
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name).append(OsUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    @CliCommand(value = "template create", help = "Create a template.")
    public boolean create(
            @CliOption(key = "file", mandatory = true, help = "The template file") File file,
            @CliOption(key = "name", mandatory = false, help = "The name of the template") String name,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = false, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = false, help = "The featuretype") String featureType,
            @CliOption(key = "coveragestore", mandatory = false, help = "The coveragestore") String coverageStore,
            @CliOption(key = "coverage", mandatory = false, help = "The coverage") String coverage
    ) throws Exception {
        StringBuilder urlBuilder = buildUrl(geoserver.getUrl(), workspace, datastore, featureType, coverageStore, coverage);
        urlBuilder.append("/templates/");
        if (name != null) {
            urlBuilder.append(URLUtil.encode(name)).append(".ftl");
        } else {
            urlBuilder.append(URLUtil.encode(file.getName()));
        }
        // @ToDo Should this be post?
        String result = HTTPUtils.put(urlBuilder.toString(), file, "plain/text", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "template modify", help = "Modify a template.")
    public boolean modify(
            @CliOption(key = "name", mandatory = true, help = "The name of the template") String name,
            @CliOption(key = "file", mandatory = true, help = "The template file") File file,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = false, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = false, help = "The featuretype") String featureType,
            @CliOption(key = "coveragestore", mandatory = false, help = "The coveragestore") String coverageStore,
            @CliOption(key = "coverage", mandatory = false, help = "The coverage") String coverage
    ) throws Exception {
        StringBuilder urlBuilder = buildUrl(geoserver.getUrl(), workspace, datastore, featureType, coverageStore, coverage);
        urlBuilder.append("/templates/");
        if (name != null) {
            urlBuilder.append(URLUtil.encode(name)).append(".ftl");
        } else {
            urlBuilder.append(URLUtil.encode(file.getName()));
        }
        String result = HTTPUtils.put(urlBuilder.toString(), file, "plain/text", geoserver.getUser(), geoserver.getPassword());
        return result != null;
    }

    @CliCommand(value = "template get", help = "Get a template.")
    public String get(
            @CliOption(key = "name", mandatory = true, help = "The name of the template") String name,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = false, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = false, help = "The featuretype") String featureType,
            @CliOption(key = "coveragestore", mandatory = false, help = "The coveragestore") String coverageStore,
            @CliOption(key = "coverage", mandatory = false, help = "The coverage") String coverage,
            @CliOption(key = "file", mandatory = false, help = "The output file") File file
    ) throws Exception {
        StringBuilder urlBuilder = buildUrl(geoserver.getUrl(), workspace, datastore, featureType, coverageStore, coverage);
        urlBuilder.append("/templates/").append(URLUtil.encode(name)).append(name.endsWith(".ftl") ? "" : ".ftl");
        String result = HTTPUtils.get(urlBuilder.toString(), geoserver.getUser(), geoserver.getPassword());
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            writer.write(result);
            writer.close();
            return file.getAbsolutePath();
        } else {
            return result;
        }
    }

    @CliCommand(value = "template delete", help = "Delete a template.")
    public boolean delete(
            @CliOption(key = "name", mandatory = true, help = "The name of the template") String name,
            @CliOption(key = "workspace", mandatory = false, help = "The workspace") String workspace,
            @CliOption(key = "datastore", mandatory = false, help = "The datastore") String datastore,
            @CliOption(key = "featuretype", mandatory = false, help = "The featuretype") String featureType,
            @CliOption(key = "coveragestore", mandatory = false, help = "The coveragestore") String coverageStore,
            @CliOption(key = "coverage", mandatory = false, help = "The coverage") String coverage
    ) throws Exception {
        StringBuilder urlBuilder = buildUrl(geoserver.getUrl(), workspace, datastore, featureType, coverageStore, coverage);
        urlBuilder.append("/templates/").append(URLUtil.encode(name)).append(name.endsWith(".ftl") ? "" : ".ftl");
        boolean deleted = HTTPUtils.delete(urlBuilder.toString(), geoserver.getUser(), geoserver.getPassword());
        return deleted;
    }

}
