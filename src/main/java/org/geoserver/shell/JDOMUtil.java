package org.geoserver.shell;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class JDOMUtil {

    public static Element getOrAdd(Element element, String childName) {
        Element child = element.getChild(childName);
        if (child == null) {
            child = new Element(childName);
            element.addContent(child);
        }
        return child;
    }

    public static String toPrettyString(Element element) {
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        return xmlOutputter.outputString(element);
    }

    public static String toString(Element element) {
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getCompactFormat());
        return xmlOutputter.outputString(element);
    }
}
