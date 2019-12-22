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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import de.topobyte.adt.geo.BBox;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.core.access.OsmReader;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.dataset.ListDataSetLoader;
import de.topobyte.osm4j.xml.dynsax.OsmXmlReader;
import de.topobyte.osmocrat.rendering.ConfigMapRenderer;
import de.topobyte.osmocrat.rendering.GraphicsConfigMapRenderer;
import de.topobyte.osmocrat.rendering.config.RenderInstructions;
import de.topobyte.osmocrat.rendering.config.Rendering;
import de.topobyte.overpass.OverpassUtil;

public class TestMapRenderingConfigToPng
{

	public static void main(String[] args) throws IOException, OsmInputException
	{
		// This is the region we would like to render
		BBox bbox = new BBox(13.45546, 52.51229, 13.46642, 52.50761);
		int width = 800;
		int height = 600;

		OverpassUtil.cache(bbox);
		Path cacheFile = OverpassUtil.cacheFile(bbox);
		InputStream input = Files.newInputStream(cacheFile);

		// Create a reader and read all data into a data set
		OsmReader reader = new OsmXmlReader(input, false);
		InMemoryListDataSet data = ListDataSetLoader.read(reader, true, true,
				true);

		MercatorImage mapImage = new MercatorImage(bbox, width, height);
		RenderInstructions instructions = Rendering.style2();

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D graphics = image.createGraphics();

		Color cBackground = new Color(0xEEEEEE);
		graphics.setColor(cBackground);
		graphics.fillRect(0, 0, width, height);

		GraphicsConfigMapRenderer configRenderer = ConfigMapRenderer
				.setupGraphicsRenderer(bbox, mapImage, data, instructions);
		configRenderer.setScaleLines(1);
		configRenderer.setScaleText(1);

		configRenderer.paint(graphics);

		Path file = Files.createTempFile("map", ".png");
		System.out.println(file);

		ImageIO.write(image, "png", file.toFile());
	}

}
