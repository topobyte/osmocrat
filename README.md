# About

This project is a set of tools for working with OpenStreetMap data.

# License

This project is released under the terms of the GNU Lesser General Public
License.

See [LGPL.md](LGPL.md) and [GPL.md](GPL.md) for details.

# Running the main executable

Setup the execution environment:

    ./gradlew createRuntime

Then you can run the main executables from the build directory:

    ./scripts/osmocrat

This executable is designed to support a number of subtasks. Currently
the available tasks are `gui` and `overpass` which can be run like this:

    ./scripts/osmocrat gui --input /path/to/some/osm/data/file.tbo

and like this respectively:

    ./scripts/osmocrat overpass --output /path/to/some/osm/data/file.osm
                                --bbox lon1,lat1,lon2,lat2

    ./scripts/osmocrat overpass --output /path/to/some/osm/data/file.osm
                                --boundary /path/to/some/wkt/polygon.wkt

To convert the downloaded files to other formats than `.osm`, use
[topobyte/osm4j-utils](https://github.com/topobyte/osm4j/tree/master/utils):

    OsmCat --input /path/to/some/osm/data/file.osm
           --input-format xml
           --output /path/to/some/osm/data/file.tbo
           --output-format tbo

# TODO list

* make it possible to find objects that reference an object at hand (as way
  nodes or relation member)
