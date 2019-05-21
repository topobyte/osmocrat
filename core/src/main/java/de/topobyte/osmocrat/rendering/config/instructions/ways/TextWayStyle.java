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

import de.topobyte.chromaticity.ColorCode;

public class TextWayStyle implements WayStyle
{

	private String fontName;
	private int size;
	private ColorCode color;
	private float widthOutline;
	private ColorCode colorOutline;

	public TextWayStyle(String fontName, int size, ColorCode color,
			float widthOutline, ColorCode colorOutline)
	{
		this.fontName = fontName;
		this.size = size;
		this.color = color;
		this.widthOutline = widthOutline;
		this.colorOutline = colorOutline;
	}

	public String getFontName()
	{
		return fontName;
	}

	public void setFontName(String fontName)
	{
		this.fontName = fontName;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public ColorCode getColor()
	{
		return color;
	}

	public void setColor(ColorCode color)
	{
		this.color = color;
	}

	public float getWidthOutline()
	{
		return widthOutline;
	}

	public void setWidthOutline(float widthOutline)
	{
		this.widthOutline = widthOutline;
	}

	public ColorCode getColorOutline()
	{
		return colorOutline;
	}

	public void setColorOutline(ColorCode colorOutline)
	{
		this.colorOutline = colorOutline;
	}

}
