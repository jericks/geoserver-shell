package org.geoserver.shell;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.xebialabs.restito.server.StubServer;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
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
        // Hack for Windows, which isn't shutting dow the server
        // fast enough
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private List<String> getLines(StringReader reader) {
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<String> strings = new ArrayList<String>();
        try {
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                strings.add(str);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return strings;
    }

    protected void assertStringsEquals(String expected, String actual, boolean trim) {
        StringReader expectedReader = new StringReader(expected);
        StringReader actualReader = new StringReader(actual);
        List<String> expectedLines = getLines(expectedReader);
        List<String> actualLines = getLines(actualReader);
        assertEquals("Difference number of lines!", expectedLines.size(), actualLines.size());
        for (int i = 0; i < expectedLines.size(); i++) {
            String exp = expectedLines.get(i);
            String act = actualLines.get(i);
            if (trim) {
                exp = exp.trim();
                act = act.trim();
            }
            assertEquals(exp, act);
        }
    }

}
