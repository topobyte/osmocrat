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

package de.topobyte.osmocrat.rendering;

import de.topobyte.adt.geo.BBox;
import de.topobyte.chromaticity.ColorCode;
import de.topobyte.chromaticity.WebColors;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osmocrat.rendering.config.RenderInstructions;
import de.topobyte.osmocrat.text.TextIntersectionChecker;

public class BaseConfigMapRenderer
{

	// Some fields that define the map colors and street line widths
	protected ColorCode cBBox = WebColors.BLUE.color();

	// This will be used to map geometry coordinates to screen coordinates
	protected MercatorImage mercatorImage;

	protected BBox bbox;

	protected boolean drawBoundingBox = true;
	protected boolean drawTextBoxes = false;

	protected RenderInstructions instructions;

	protected RenderingDataSource renderingData;

	protected TextIntersectionChecker textIntersectionChecker;

	protected float scaleLines = 1;
	protected float scaleText = 1;

	public BaseConfigMapRenderer(BBox bbox, MercatorImage mercatorImage,
			RenderInstructions instructions, RenderingDataSource renderingData)
	{
		this.bbox = bbox;
		this.mercatorImage = mercatorImage;
		this.instructions = instructions;
		this.renderingData = renderingData;
	}

	public boolean isDrawBoundingBox()
	{
		return drawBoundingBox;
	}

	public void setDrawBoundingBox(boolean drawBoundingBox)
	{
		this.drawBoundingBox = drawBoundingBox;
	}

	public boolean isDrawTextBoxes()
	{
		return drawTextBoxes;
	}

	public void setDrawTextBoxes(boolean drawTextBoxes)
	{
		this.drawTextBoxes = drawTextBoxes;
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

}
