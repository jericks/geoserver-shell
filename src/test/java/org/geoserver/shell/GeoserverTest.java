package org.geoserver.shell;

import org.junit.Test;

import static org.junit.Assert.*;

public class GeoserverTest {

    @Test
    public void contructors() {
        Geoserver gs = new Geoserver();
        assertNull(gs.getUrl());
        assertNull(gs.getUser());
        assertNull(gs.getPassword());
        assertFalse(gs.isVerbose());

        gs.setUrl("http://localhost:8080/geoserver");
        assertEquals("http://localhost:8080/geoserver", gs.getUrl());
        gs.setUser("admin");
        assertEquals("admin", gs.getUser());
        gs.setPassword("geoserver");
        assertEquals("geoserver", gs.getPassword());
        gs.setVerbose(true);
        assertTrue(gs.isVerbose());

        gs = new Geoserver("http://localhost:8080/geoserver", "admin", "geoserver");
        assertEquals("http://localhost:8080/geoserver", gs.getUrl());
        assertEquals("admin", gs.getUser());
        assertEquals("geoserver", gs.getPassword());
        assertFalse(gs.isVerbose());
    }

}
