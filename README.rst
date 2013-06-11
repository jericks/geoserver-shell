GeoServer Shell
===============
GeoServer Shell uses the spring-shell library used by Spring Roo to manage Geoserver.

Usage
-----

Clone the repository::

    git clone git://github.com/jericks/geoserver-shell.git

Build the code::

    mvn clean install

Run the geoserver-shell::

    java -jar target/geoserver-shell-1.0-SNAPSHOT.jar

Enter commands::

    gs-shell>geoserver set --url http://localhost:8080/geoserver --user admin --password geoserver

    gs-shell>workspace list
    cite
    it.geosolutions
    nurc
    sde
    sf
    tiger
    topp

    gs-shell>workspace create --name test
    true

    gs-shell>exit

Libraries
---------
Spring Shell:
    https://github.com/SpringSource/spring-shell

GeoServer Manager:
    https://github.com/geosolutions-it/geoserver-manager

License
-------
GeoServer Shell is open source and licensed under the MIT License.
