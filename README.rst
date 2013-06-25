GeoServer Shell
===============
GeoServer Shell uses the spring-shell library used by Spring Roo to manage Geoserver in a command line interface (CLI).

You can use Geoserver Shell interactively by typing **gs-shell** at the command line
or you can write scripts and then execute them from the command line by typing **gs-shell --cmdfile script.gs**
or by using the **script --file script.gs** command within a Geoserver Shell session.

Build
-----

Clone the repository::

    git clone git://github.com/jericks/geoserver-shell.git

Build the code::

    mvn clean install

Run the geoserver-shell::

    target/gs-shell-app/gs-shell-1.0-SNAPSHOT/bin/gs-shell

Usage
-----

Create a Workspace::

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

Publish a shapefile::

    gs-shell>geoserver set --url http://localhost:8080/geoserver
    gs-shell>workspace create --name naturalearth
    gs-shell>shapefile zip --shapefile ~/Projects/NaturalEarth/SmallScale/110m_cultural/110m_admin_0_countries.shp
    gs-shell>shapefile --workspace naturalearth publish --file ~/Projects/NaturalEarth/SmallScale/110m_cultural/110m_admin_0_countries.zip

Upload a style::

    gs-shell>style create --file ~/Projects/NaturalEarth/SmallScale/110m_cultural/110m_admin_0_countries.sld --name 110m_admin_0_countries
    gs-shell>layer style add --name 110m_admin_0_countries --style 110m_admin_0_countries
    gs-shell>layer modify --name 110m_admin_0_countries --defaultStyle 110m_admin_0_countries

Start seeding tiles::

    gs-shell>gwc seed --name naturalearth:110m_admin_0_countries --gridset EPSG:4326 --start 0 --stop 4

Commands
--------
* geoserver

    * geoserver set --url http://localhost:8080/geoserver --user admin --password geoserver

    * geoserver show

    * geoserver reload

    * geoserver reset

* about

    * version list

    * manifest list

    * manifest get

Libraries
---------
Spring Shell:
    https://github.com/SpringSource/spring-shell

GeoServer Manager:
    https://github.com/geosolutions-it/geoserver-manager

GeoTools:
    http://www.geotools.org/

License
-------
GeoServer Shell is open source and licensed under the MIT License.
