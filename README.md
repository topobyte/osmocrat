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
the only task is `gui` which can be run like this:

    ./scripts/osmocrat gui --input /path/to/some/osm/data/file.tbo
