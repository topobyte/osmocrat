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

package de.topobyte.osmocrat.rendering.config.instructions.ways;

import java.util.List;

import de.topobyte.chromaticity.ColorCode;

public class DashedWayStyle implements WayStyle
{

	private int width;
	private ColorCode color;
	private List<Float> dashArray;
	private float dashPhase;

	public DashedWayStyle(int width, ColorCode color, List<Float> dashArray,
			float dashPhase)
	{
		this.width = width;
		this.color = color;
		this.dashArray = dashArray;
		this.dashPhase = dashPhase;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public ColorCode getColor()
	{
		return color;
	}

	public void setColor(ColorCode color)
	{
		this.color = color;
	}

	public List<Float> getDashArray()
	{
		return dashArray;
	}

	public void setDashArray(List<Float> dashArray)
	{
		this.dashArray = dashArray;
	}

	public float getDashPhase()
	{
		return dashPhase;
	}

	public void setDashPhase(float dashPhase)
	{
		this.dashPhase = dashPhase;
	}

}
