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

package de.topobyte.osmocrat.rendering.config;

import static de.topobyte.chromaticity.WebColors.GRAY;
import static de.topobyte.chromaticity.WebColors.WHITE;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.osmocrat.rendering.config.instructions.AreaInstruction;
import de.topobyte.osmocrat.rendering.config.instructions.WayInstruction;
import de.topobyte.osmocrat.rendering.config.instructions.area.SimpleAreaStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.SimpleWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.TwofoldWayStyle;
import de.topobyte.osmocrat.rendering.config.selector.KeySelector;
import de.topobyte.osmocrat.rendering.config.selector.TagSelector;

public class Rendering
{

	public static RenderInstructions style1()
	{
		RenderInstructions ri = new RenderInstructions();

		addArea(ri, "building", new ColorCode(0xFFC2C2));

		addWay(ri, "highway", "primary", 6, 10, WHITE.color(), GRAY.color());
		addWay(ri, "highway", "secondary", 6, 10, WHITE.color(), GRAY.color());
		addWay(ri, "highway", "tertiary", 6, 10, WHITE.color(), GRAY.color());
		addWay(ri, "highway", "residential", 6, 10, WHITE.color(),
				GRAY.color());
		addWay(ri, "highway", "living_street", 6, 10, WHITE.color(),
				GRAY.color());

		return ri;
	}

	public static RenderInstructions style2()
	{
		RenderInstructions ri = new RenderInstructions();

		addArea(ri, "building", new ColorCode(0xFFC2C2));

		addWay(ri, "highway", "primary", 10, GRAY.color());
		addWay(ri, "highway", "secondary", 10, GRAY.color());
		addWay(ri, "highway", "tertiary", 10, GRAY.color());
		addWay(ri, "highway", "residential", 10, GRAY.color());
		addWay(ri, "highway", "living_street", 10, GRAY.color());

		addWay(ri, "highway", "primary", 6, WHITE.color());
		addWay(ri, "highway", "secondary", 6, WHITE.color());
		addWay(ri, "highway", "tertiary", 6, WHITE.color());
		addWay(ri, "highway", "residential", 6, WHITE.color());
		addWay(ri, "highway", "living_street", 6, WHITE.color());

		return ri;
	}

	public static RenderInstructions style3()
	{
		RenderInstructions ri = new RenderInstructions();

		addArea(ri, "waterway", "riverbank", new ColorCode(0xaad3de));

		addWay(ri, "highway", "primary", 10, GRAY.color());
		addWay(ri, "highway", "secondary", 10, GRAY.color());
		addWay(ri, "highway", "tertiary", 10, GRAY.color());

		addWay(ri, "highway", "primary", 6, WHITE.color());
		addWay(ri, "highway", "secondary", 6, WHITE.color());
		addWay(ri, "highway", "tertiary", 6, WHITE.color());

		return ri;
	}

	private static void addWay(RenderInstructions ri, String key, String value,
			int width, ColorCode color)
	{
		ri.add(new WayInstruction(new TagSelector(key, value),
				new SimpleWayStyle(width, color)));
	}

	private static void addWay(RenderInstructions ri, String key, String value,
			int widthFG, int widthBG, ColorCode fg, ColorCode bg)
	{
		ri.add(new WayInstruction(new TagSelector(key, value),
				new TwofoldWayStyle(widthFG, widthBG, fg, bg)));
	}

	private static void addArea(RenderInstructions ri, String key,
			ColorCode color)
	{
		ri.add(new AreaInstruction(new KeySelector(key),
				new SimpleAreaStyle(color)));
	}

	private static void addArea(RenderInstructions ri, String key, String value,
			ColorCode color)
	{
		ri.add(new AreaInstruction(new TagSelector(key, value),
				new SimpleAreaStyle(color)));
	}

}
