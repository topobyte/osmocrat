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

package de.topobyte.osmocrat.rendering.config.instructions.area;

import de.topobyte.chromaticity.ColorCode;

public class SimpleAreaStyle implements AreaStyle
{

	private ColorCode color;

	public SimpleAreaStyle(ColorCode color)
	{
		this.color = color;
	}

	public ColorCode getColor()
	{
		return color;
	}

	public void setColor(ColorCode color)
	{
		this.color = color;
	}

}
