package org.geoserver.shell;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GeoserverHistoryFileNameProviderTest {

    @Test
    public void getHistoryFileName() {
        GeoserverHistoryFileNameProvider historyFileNameProvider = new GeoserverHistoryFileNameProvider();
        assertNotNull(historyFileNameProvider.getHistoryFileName());
        assertEquals("geoserver-shell.log", historyFileNameProvider.getHistoryFileName());
    }

    @Test
    public void getProviderName() {
        GeoserverHistoryFileNameProvider historyFileNameProvider = new GeoserverHistoryFileNameProvider();
        assertNotNull(historyFileNameProvider.getProviderName());
        assertEquals("geoserver shell history file name provider", historyFileNameProvider.getProviderName());
    }
}
