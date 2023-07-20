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
import de.topobyte.osmocrat.rendering.config.instructions.LineCap;
import de.topobyte.osmocrat.rendering.config.instructions.LineJoin;

public class TwofoldWayStyle implements WayStyle
{

	private double widthFG;
	private double widthBG;
	private ColorCode fg;
	private ColorCode bg;
	private LineCap lineCap = LineCap.ROUND;
	private LineJoin lineJoin = LineJoin.ROUND;

	public TwofoldWayStyle(double widthFG, double widthBG, ColorCode fg,
			ColorCode bg)
	{
		this.widthFG = widthFG;
		this.widthBG = widthBG;
		this.fg = fg;
		this.bg = bg;
	}

	public TwofoldWayStyle(double widthFG, double widthBG, ColorCode fg,
			ColorCode bg, LineCap lineCap, LineJoin lineJoin)
	{
		this.widthFG = widthFG;
		this.widthBG = widthBG;
		this.fg = fg;
		this.bg = bg;
		this.lineCap = lineCap;
		this.lineJoin = lineJoin;
	}

	public double getWidthFG()
	{
		return widthFG;
	}

	public void setWidthFG(double widthFG)
	{
		this.widthFG = widthFG;
	}

	public double getWidthBG()
	{
		return widthBG;
	}

	public void setWidthBG(double widthBG)
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

	public LineCap getLineCap()
	{
		return lineCap;
	}

	public void setLineCap(LineCap lineCap)
	{
		this.lineCap = lineCap;
	}

	public LineJoin getLineJoin()
	{
		return lineJoin;
	}

	public void setLineJoin(LineJoin lineJoin)
	{
		this.lineJoin = lineJoin;
	}

}
