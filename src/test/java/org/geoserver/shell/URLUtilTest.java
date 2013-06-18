package org.geoserver.shell;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;

public class URLUtilTest {

    @Test
    public void encode() {
        assertEquals("topp", URLUtil.encode("topp"));
        assertEquals("washington%20state", URLUtil.encode("washington state"));
        assertEquals("make%20sure%20all%20spaces%20are%20escaped", URLUtil.encode("make sure all spaces are escaped"));
    }
}
