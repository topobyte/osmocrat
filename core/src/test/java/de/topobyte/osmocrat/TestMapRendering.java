// Copyright 2018 Sebastian Kuerten
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

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JFrame;

import de.topobyte.adt.geo.BBox;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.core.access.OsmReader;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.dataset.ListDataSetLoader;
import de.topobyte.osm4j.xml.dynsax.OsmXmlReader;
import de.topobyte.osmocrat.rendering.MapRenderer;

public class TestMapRendering
{

	public static void main(String[] args) throws IOException, OsmInputException
	{
		// This is the region we would like to render
		BBox bbox = new BBox(13.45546, 52.51229, 13.46642, 52.50761);
		int width = 800;
		int height = 600;

		// Define a query to retrieve some data
		String queryTemplate = "http://overpass-api.de/api/interpreter?data=(node(%f,%f,%f,%f);<;>;);out;";
		String query = String.format(queryTemplate, bbox.getLat2(),
				bbox.getLon1(), bbox.getLat1(), bbox.getLon2());

		// Open a stream
		InputStream input = new URL(query).openStream();

		// Create a reader and read all data into a data set
		OsmReader reader = new OsmXmlReader(input, false);
		InMemoryListDataSet data = ListDataSetLoader.read(reader, true, true,
				true);

		MercatorImage mapImage = new MercatorImage(bbox, width, height);

		MapRenderer panel = new MapRenderer(bbox, mapImage, data);
		panel.setPreferredSize(new Dimension(width, height));

		JFrame frame = new JFrame("Osmocrat Map");
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

}
