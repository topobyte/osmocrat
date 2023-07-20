// Copyright 2023 Sebastian Kuerten
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

import static de.topobyte.inkscape4j.Styles.color;
import static de.topobyte.inkscape4j.Styles.style;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.topobyte.adt.geo.BBox;
import de.topobyte.inkscape4j.Layer;
import de.topobyte.inkscape4j.SvgFile;
import de.topobyte.inkscape4j.SvgFileWriting;
import de.topobyte.inkscape4j.shape.Rect;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.core.access.OsmReader;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.dataset.ListDataSetLoader;
import de.topobyte.osm4j.xml.dynsax.OsmXmlReader;
import de.topobyte.osmocrat.rendering.ConfigMapRenderer;
import de.topobyte.osmocrat.rendering.InkscapeConfigMapRenderer;
import de.topobyte.osmocrat.rendering.config.RenderInstructions;
import de.topobyte.osmocrat.rendering.config.Rendering;
import de.topobyte.overpass.OverpassUtil;

public class TestForstSvg
{

	public static void main(String[] args) throws IOException, OsmInputException
	{
		// This is the region we would like to render
		BBox bbox = new BBox(14.632630, 51.748262, 14.656663, 51.736039);
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
		RenderInstructions instructions = Rendering.style4();

		SvgFile svg = new SvgFile();

		svg.setWidth(String.format("%dpx", width));
		svg.setHeight(String.format("%dpx", height));

		Layer layerBackground = new Layer("background");
		svg.getLayers().add(layerBackground);
		layerBackground.setLabel("Background");

		Rect rect = new Rect("rect1", 0, 0, width, height);
		rect.setStyle(style(color(0xEEEEEE), null, 1, 1, 1, 0));
		layerBackground.getObjects().add(rect);

		InkscapeConfigMapRenderer configRenderer = ConfigMapRenderer
				.setupInkscapeRenderer(bbox, mapImage, data, instructions);
		configRenderer.setScaleLines(2);
		configRenderer.setScaleText(2);
		configRenderer.paint(svg);

		Path file = Files.createTempFile("map", ".svg");
		System.out.println(file);

		OutputStream fos = Files.newOutputStream(file);
		SvgFileWriting.write(svg, fos);
		fos.close();
	}

}
