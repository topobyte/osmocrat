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

import static de.topobyte.chromaticity.WebColors.BLACK;
import static de.topobyte.chromaticity.WebColors.GRAY;
import static de.topobyte.chromaticity.WebColors.WHITE;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.osmocrat.rendering.config.instructions.AreaInstruction;
import de.topobyte.osmocrat.rendering.config.instructions.LineCap;
import de.topobyte.osmocrat.rendering.config.instructions.LineJoin;
import de.topobyte.osmocrat.rendering.config.instructions.WayInstruction;
import de.topobyte.osmocrat.rendering.config.instructions.area.SimpleAreaStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.DashedWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.SimpleWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.TextWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.TwofoldWayStyle;
import de.topobyte.osmocrat.rendering.config.selector.KeySelector;
import de.topobyte.osmocrat.rendering.config.selector.TagSelector;

public class Rendering
{

	public static RenderInstructions style1()
	{
		RenderInstructions ri = new RenderInstructions();

		addArea(ri, "building", new ColorCode(0xFFC2C2));

		addWay(ri, "highway", "footway", 1, new float[] { 2, 2 }, GRAY.color());

		addWay(ri, "highway", "trunk", 6, 10, WHITE.color(), GRAY.color());
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

		addWay(ri, "highway", "footway", 1, new float[] { 2, 2 }, GRAY.color());

		addWay(ri, "highway", "trunk", 10, GRAY.color());
		addWay(ri, "highway", "primary", 10, GRAY.color());
		addWay(ri, "highway", "secondary", 10, GRAY.color());
		addWay(ri, "highway", "tertiary", 10, GRAY.color());
		addWay(ri, "highway", "residential", 10, GRAY.color());
		addWay(ri, "highway", "living_street", 10, GRAY.color());

		addWay(ri, "highway", "trunk", 6, WHITE.color());
		addWay(ri, "highway", "primary", 6, WHITE.color());
		addWay(ri, "highway", "secondary", 6, WHITE.color());
		addWay(ri, "highway", "tertiary", 6, WHITE.color());
		addWay(ri, "highway", "residential", 6, WHITE.color());
		addWay(ri, "highway", "living_street", 6, WHITE.color());

		ri.add(new WayInstruction(new TagSelector("highway", "residential"),
				new TextWayStyle("Arial", 14, BLACK.color(), 3,
						WHITE.color())));

		return ri;
	}

	public static RenderInstructions style3()
	{
		RenderInstructions ri = new RenderInstructions();

		addArea(ri, "waterway", "riverbank", new ColorCode(0xaad3de));

		addWay(ri, "highway", "footway", 1, new float[] { 2, 2 }, GRAY.color());

		addWay(ri, "highway", "trunk", 10, GRAY.color());
		addWay(ri, "highway", "primary", 10, GRAY.color());
		addWay(ri, "highway", "secondary", 10, GRAY.color());
		addWay(ri, "highway", "tertiary", 10, GRAY.color());

		addWay(ri, "highway", "trunk", 6, WHITE.color());
		addWay(ri, "highway", "primary", 6, WHITE.color());
		addWay(ri, "highway", "secondary", 6, WHITE.color());
		addWay(ri, "highway", "tertiary", 6, WHITE.color());

		return ri;
	}

	public static RenderInstructions style4()
	{
		RenderInstructions ri = new RenderInstructions();

		addArea(ri, "waterway", "riverbank", new ColorCode(0xaad3de));
		addArea(ri, "natural", "water", new ColorCode(0xaad3de));

		addWay(ri, "railway", "rail", 1, GRAY.color());
		addWay(ri, "railway", "rail", 1, new float[] { 4, 4 }, BLACK.color());

		addWay(ri, "highway", "footway", 1.6, new float[] { 1, 1 },
				new ColorCode(0xC0C0C0));
		addWay(ri, "highway", "footway", 0.8, new ColorCode(0xD0D0D0));

		addWay(ri, "highway", "path", 1.6, new float[] { 1, 1 },
				new ColorCode(0xC0C0C0));
		addWay(ri, "highway", "path", 0.8, new ColorCode(0xD0D0D0));

		addWay(ri, "highway", "pedestrian", 3, GRAY.color());
		addWay(ri, "highway", "trunk", 3, GRAY.color());
		addWay(ri, "highway", "primary", 3, GRAY.color());
		addWay(ri, "highway", "secondary", 3, GRAY.color());
		addWay(ri, "highway", "tertiary", 3, GRAY.color());
		addWay(ri, "highway", "service", 2.5, GRAY.color());
		addWay(ri, "highway", "residential", 3, GRAY.color());
		addWay(ri, "highway", "living_street", 3, GRAY.color());

		addWay(ri, "highway", "pedestrian", 2, new ColorCode(230, 230, 230));
		addWay(ri, "highway", "trunk", 2, WHITE.color());
		addWay(ri, "highway", "primary", 2, WHITE.color());
		addWay(ri, "highway", "secondary", 2, WHITE.color());
		addWay(ri, "highway", "tertiary", 2, WHITE.color());
		addWay(ri, "highway", "service", 1.5, WHITE.color());
		addWay(ri, "highway", "residential", 2, WHITE.color());
		addWay(ri, "highway", "living_street", 2, WHITE.color());

		return ri;
	}

	private static void addWay(RenderInstructions ri, String key, String value,
			double width, ColorCode color)
	{
		ri.add(new WayInstruction(new TagSelector(key, value),
				new SimpleWayStyle(width, color)));
	}

	private static void addWay(RenderInstructions ri, String key, String value,
			double width, float[] dashes, ColorCode color)
	{
		List<Float> dashArray = new ArrayList<>();
		for (int i = 0; i < dashes.length; i++) {
			dashArray.add(dashes[i]);
		}

		ri.add(new WayInstruction(new TagSelector(key, value),
				new DashedWayStyle(width, color, dashArray, 0, LineCap.BUTT,
						LineJoin.ROUND)));
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
