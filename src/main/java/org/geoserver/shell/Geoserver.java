package org.geoserver.shell;

public class Geoserver {

    private String url;

    private String user;

    private String password;

    public Geoserver() {
    }

    public Geoserver(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public boolean isSet() {
        if (this.url != null && this.user != null && this.password != null) {
            return true;
        } else {
            return false;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
