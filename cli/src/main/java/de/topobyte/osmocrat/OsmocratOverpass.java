// Copyright 2019 Sebastian Kuerten
//
// This file is part of osmocrat.
//
// osmocrat is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// osmocrat is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with osmocrat. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.osmocrat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.BBoxString;
import de.topobyte.overpass.OverpassUtil;
import de.topobyte.utilities.apache.commons.cli.CliTool;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class OsmocratOverpass
{

	final static Logger logger = LoggerFactory
			.getLogger(OsmocratOverpass.class);

	private static final String OPTION_OUTPUT = "output";
	private static final String OPTION_BBOX = "bbox";
	private static final String OPTION_RAW = "raw";
	private static final String OPTION_BOUNDARY = "boundary";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			// @formatter:off
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "an OSM data file");
			OptionHelper.addL(options, OPTION_BBOX, true, false, "bbox", "a bounding box to download");
			OptionHelper.addL(options, OPTION_RAW, true, false, "query", "a query to download data for");
			OptionHelper.addL(options, OPTION_BOUNDARY, true, false, "file", "a boundary to download (WKT)");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	private static CliTool tool;

	public static void main(String name, CommonsCliArguments arguments)
	{
		CommandLine line = arguments.getLine();

		String argOutput = line.getOptionValue(OPTION_OUTPUT);
		Path pathOutput = Paths.get(argOutput);

		String argBbox = line.getOptionValue(OPTION_BBOX);
		String argRaw = line.getOptionValue(OPTION_RAW);
		String argBoundary = line.getOptionValue(OPTION_BOUNDARY);

		tool = new CliTool(name, arguments.getOptions());

		List<String> exclusiveArgs = Arrays.asList(argBbox, argBoundary,
				argRaw);
		long numExclusiveArgs = exclusiveArgs.stream().filter(e -> e != null)
				.count();

		if (numExclusiveArgs == 0) {
			tool.printMessageAndExit(
					String.format("Please specify '%s', '%s' or '%s'",
							OPTION_BBOX, OPTION_BOUNDARY, OPTION_RAW));
		} else if (numExclusiveArgs > 1) {
			tool.printMessageAndExit(String.format(
					"Please specify either '%s', '%s' or '%s', not multiple of them",
					OPTION_BBOX, OPTION_BOUNDARY, OPTION_RAW));
		}

		if (argBbox != null) {
			BBox bbox = BBoxString.parse(argBbox).toBbox();
			if (bbox.getLat1() == 0 && bbox.getLat2() == 0
					&& bbox.getLon1() == 0 && bbox.getLon2() == 0) {
				logger.error("Please specify a valid bounding box");
				System.exit(1);
			}
			load(pathOutput, bbox);
		} else if (argBoundary != null) {
			Path pathBoundary = Paths.get(argBoundary);
			load(pathOutput, pathBoundary);
		} else if (argRaw != null) {
			load(pathOutput, argRaw);
		}
	}

	private static void load(Path pathOutput, BBox bbox)
	{
		String query = OverpassUtil.query(bbox);
		try {
			InputStream input = new URL(query).openStream();
			Files.copy(input, pathOutput);
		} catch (IOException e) {
			logger.error("Error while requesting data", e);
			System.exit(1);
		}
	}

	private static void load(Path pathOutput, Path pathBoundary)
	{
		Geometry boundary = null;
		try {
			WKTReader wktReader = new WKTReader();
			boundary = wktReader.read(Files.newBufferedReader(pathBoundary));
		} catch (ParseException | IOException e) {
			logger.error("Error while reading boundary", e);
			System.exit(1);
		}

		if (!(boundary instanceof Polygon)) {
			logger.error("Boundary is not a Polygon");
			System.exit(1);
		}

		String query = OverpassUtil.query((Polygon) boundary);
		try {
			InputStream input = new URL(query).openStream();
			Files.copy(input, pathOutput);
		} catch (IOException e) {
			logger.error("Error while requesting data", e);
			System.exit(1);
		}
	}

	private static void load(Path pathOutput, String rawQuery)
	{
		String query = OverpassUtil.query(rawQuery);
		try {
			InputStream input = new URL(query).openStream();
			Files.copy(input, pathOutput);
		} catch (IOException e) {
			logger.error("Error while requesting data", e);
			System.exit(1);
		}
	}
}
