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

import java.awt.Graphics;
import java.awt.Graphics2D;

import de.topobyte.adt.geo.BBox;
import de.topobyte.inkscape4j.SvgFile;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osmocrat.rendering.config.RenderInstructions;

public class ConfigMapRenderer
{

	// This will be used to map geometry coordinates to screen coordinates
	protected MercatorImage mercatorImage;

	// We need to keep the reference to the bounding box, so that we can create
	// a new MercatorImage if the size of our panel changes
	protected BBox bbox;

	// The data set will be used as entity provider when building geometries
	protected InMemoryListDataSet data;

	protected boolean drawBoundingBox = true;

	protected RenderInstructions instructions;

	protected float scaleLines = 1;
	protected float scaleText = 1;

	private CachedRenderingDataSource renderingData;

	public ConfigMapRenderer(BBox bbox, MercatorImage mercatorImage,
			InMemoryListDataSet data, RenderInstructions instructions)
	{
		this.bbox = bbox;
		this.mercatorImage = mercatorImage;
		this.data = data;
		this.instructions = instructions;

		System.out.println("building rendering data...");
		RenderingData dataBuilder = new RenderingData(mercatorImage, data,
				instructions);
		dataBuilder.buildRenderingData();
		renderingData = dataBuilder.getRenderingData();
		System.out.println("done");
	}

	public boolean isDrawBoundingBox()
	{
		return drawBoundingBox;
	}

	public void setDrawBoundingBox(boolean drawBoundingBox)
	{
		this.drawBoundingBox = drawBoundingBox;
	}

	public float getScaleLines()
	{
		return scaleLines;
	}

	public void setScaleLines(float scaleLines)
	{
		this.scaleLines = scaleLines;
	}

	public float getScaleText()
	{
		return scaleText;
	}

	public void setScaleText(float scaleText)
	{
		this.scaleText = scaleText;
	}

	public void refreshMercatorImage(int width, int height)
	{
		mercatorImage = new MercatorImage(bbox, width, height);
	}

	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		GraphicsConfigMapRenderer renderer = new GraphicsConfigMapRenderer(bbox,
				mercatorImage, instructions, renderingData);
		renderer.setScaleLines(scaleLines);
		renderer.setScaleText(scaleText);
		renderer.setDrawBoundingBox(drawBoundingBox);
		renderer.paint(g);
	}

	public void paint(SvgFile svg)
	{
		InkscapeConfigMapRenderer renderer = new InkscapeConfigMapRenderer(bbox,
				mercatorImage, instructions, renderingData);
		renderer.setScaleLines(scaleLines);
		renderer.setScaleText(scaleText);
		renderer.paint(svg);
	}

}
