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

package de.topobyte.osmocrat.rendering.config.instructions.ways;

import de.topobyte.chromaticity.ColorCode;

public class TwofoldWayStyle implements WayStyle
{

	private int widthFG;
	private int widthBG;
	private ColorCode fg;
	private ColorCode bg;

	public TwofoldWayStyle(int widthFG, int widthBG, ColorCode fg, ColorCode bg)
	{
		this.widthFG = widthFG;
		this.widthBG = widthBG;
		this.fg = fg;
		this.bg = bg;
	}

	public int getWidthFG()
	{
		return widthFG;
	}

	public void setWidthFG(int widthFG)
	{
		this.widthFG = widthFG;
	}

	public int getWidthBG()
	{
		return widthBG;
	}

	public void setWidthBG(int widthBG)
	{
		this.widthBG = widthBG;
	}

	public ColorCode getFg()
	{
		return fg;
	}

	public void setFg(ColorCode fg)
	{
		this.fg = fg;
	}

	public ColorCode getBg()
	{
		return bg;
	}

	public void setBg(ColorCode bg)
	{
		this.bg = bg;
	}

}
