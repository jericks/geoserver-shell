package org.geoserver.shell;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.xebialabs.restito.server.StubServer;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BaseTest {

    protected StubServer server;

    @Before
    public void start() {
        server = new StubServer(8888).run();

    }

    @After
    public void stop() {
        server.stop();
    }

    protected File getResourceFile(String path) throws Exception {
        return new File(Resources.getResource(path).toURI());
    }

    protected String getResourceString(String path) throws IOException {
        return Resources.toString(Resources.getResource(path), Charsets.UTF_8);
    }

    protected List<String> getFileNamesFromZip(File zipFile) throws IOException {
        List<String> names = new ArrayList<String>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> zipEntries = zip.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = zipEntries.nextElement();
            String name = zipEntry.getName();
            names.add(name);
        }
        return names;
    }

}
