package org.geoserver.shell;

public class URLUtil {
    
    public static String encode(String str) {
        return str.replaceAll(" ","%20");
    }

}
