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

package de.topobyte.osmocrat.rendering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.geo.BBox;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osmocrat.rendering.config.RenderInstructions;

public class ConfigMapRenderer
{

	final static Logger logger = LoggerFactory
			.getLogger(ConfigMapRenderer.class);

	public static GraphicsConfigMapRenderer setupGraphicsRenderer(BBox bbox,
			MercatorImage mercatorImage, InMemoryListDataSet data,
			RenderInstructions instructions)
	{
		CachedRenderingDataSource renderingData = buildRenderingData(
				mercatorImage, data, instructions);

		GraphicsConfigMapRenderer renderer = new GraphicsConfigMapRenderer(bbox,
				mercatorImage, instructions, renderingData);

		return renderer;
	}

	public static InkscapeConfigMapRenderer setupInkscapeRenderer(BBox bbox,
			MercatorImage mercatorImage, InMemoryListDataSet data,
			RenderInstructions instructions)
	{
		CachedRenderingDataSource renderingData = buildRenderingData(
				mercatorImage, data, instructions);

		InkscapeConfigMapRenderer renderer = new InkscapeConfigMapRenderer(bbox,
				mercatorImage, instructions, renderingData);

		return renderer;
	}

	private static CachedRenderingDataSource buildRenderingData(
			MercatorImage mercatorImage, InMemoryListDataSet data,
			RenderInstructions instructions)
	{
		logger.info("building rendering data...");
		RenderingData dataBuilder = new RenderingData(mercatorImage, data,
				instructions);
		dataBuilder.buildRenderingData();
		CachedRenderingDataSource renderingData = dataBuilder
				.getRenderingData();
		logger.info("done");

		return renderingData;
	}

}
