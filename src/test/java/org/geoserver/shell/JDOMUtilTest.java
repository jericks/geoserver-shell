package org.geoserver.shell;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.junit.Test;
import org.springframework.shell.support.util.OsUtils;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class JDOMUtilTest {

    @Test
    public void getOrAdd() throws Exception {
        Element element = JDOMBuilder.buildElement(getResourceString("workspaces.xml"));

        Element workspaceElement = JDOMUtil.getOrAdd(element, "workspace");
        assertNotNull(workspaceElement);

        Element testElement = JDOMUtil.getOrAdd(element, "test");
        assertNotNull(testElement);
    }

    @Test
    public void toPrettyString() throws Exception {
        Element element = JDOMBuilder.buildElement(getResourceString("workspaces.xml"));
        String actual = JDOMUtil.toPrettyString(element);
        String expected = "<workspaces>\r\n" +
                "  <workspace>\r\n" +
                "    <name>it.geosolutions</name>\r\n" +
                "  </workspace>\r\n" +
                "  <workspace>\r\n" +
                "    <name>cite</name>\r\n" +
                "  </workspace>\r\n" +
                "  <workspace>\r\n" +
                "    <name>topp</name>\r\n" +
                "  </workspace>\r\n" +
                "</workspaces>";
        assertEquals(expected, actual);
    }

    @Test
    public void toStringTest() throws Exception {
        Element element = JDOMBuilder.buildElement(getResourceString("workspaces.xml"));
        String actual = JDOMUtil.toString(element);
        String expected = "<workspaces><workspace><name>it.geosolutions</name></workspace><workspace><name>cite</name></workspace><workspace><name>topp</name></workspace></workspaces>";
        assertEquals(expected, actual);
    }

    protected String getResourceString(String path) throws IOException {
        return Resources.toString(Resources.getResource(path), Charsets.UTF_8);
    }
}
