package org.geoserver.shell;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.xebialabs.restito.server.StubServer;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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

}
