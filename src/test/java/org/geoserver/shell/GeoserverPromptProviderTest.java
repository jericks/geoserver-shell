package org.geoserver.shell;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GeoserverPromptProviderTest {

    @Test
    public void getPrompt() {
        GeoserverPromptProvider promptProvider = new GeoserverPromptProvider();
        assertNotNull(promptProvider.getPrompt());
        assertEquals("gs-shell>", promptProvider.getPrompt());
    }

    @Test
    public void getProviderName() {
        GeoserverPromptProvider promptProvider = new GeoserverPromptProvider();
        assertNotNull(promptProvider.getProviderName());
        assertEquals("geoserver shell prompt provider", promptProvider.getProviderName());
    }

}
