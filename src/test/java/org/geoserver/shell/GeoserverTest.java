package org.geoserver.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class GeoserverTest {

    @Test
    public void contructors() {
        Geoserver gs = new Geoserver();
        assertNull(gs.getUrl());
        assertNull(gs.getUser());
        assertNull(gs.getPassword());

        gs.setUrl("http://localhost:8080/geoserver");
        assertEquals("http://localhost:8080/geoserver", gs.getUrl());
        gs.setUser("admin");
        assertEquals("admin", gs.getUser());
        gs.setPassword("geoserver");
        assertEquals("geoserver", gs.getPassword());

        gs = new Geoserver("http://localhost:8080/geoserver", "admin", "geoserver");
        assertEquals("http://localhost:8080/geoserver", gs.getUrl());
        assertEquals("admin", gs.getUser());
        assertEquals("geoserver", gs.getPassword());
    }

}
