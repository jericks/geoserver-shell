package org.geoserver.shell;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GeoserverBannerProviderTest {

    @Test
    public void getBanner() {
        GeoserverBannerProvider bannerProvider = new GeoserverBannerProvider();
        assertNotNull(bannerProvider.getBanner());
    }

    @Test
    public void getVersion() {
        GeoserverBannerProvider bannerProvider = new GeoserverBannerProvider();
        assertNotNull(bannerProvider.getVersion());
    }

    @Test
    public void getWelcomeMessage() {
        GeoserverBannerProvider bannerProvider = new GeoserverBannerProvider();
        assertNotNull(bannerProvider.getWelcomeMessage());
        assertEquals("Welcome to the Geoserver Shell!", bannerProvider.getWelcomeMessage());
    }

    @Test
    public void getProviderName() {
        GeoserverBannerProvider bannerProvider = new GeoserverBannerProvider();
        assertNotNull(bannerProvider.getProviderName());
        assertEquals("geoserver shell", bannerProvider.getProviderName());
    }
}
