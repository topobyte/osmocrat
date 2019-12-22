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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import de.topobyte.osmocrat.rendering.config.instructions.AreaInstruction;
import de.topobyte.osmocrat.rendering.config.instructions.Instruction;
import de.topobyte.osmocrat.rendering.config.instructions.WayInstruction;

public class CachedRenderingDataSource implements RenderingDataSource
{

	// We build the geometries to be rendered during construction and store them
	// in these fields so that we don't have to recompute everything when
	// rendering.
	protected Map<AreaInstruction, List<Geometry>> areas = new HashMap<>();
	protected Map<WayInstruction, List<LineString>> ways = new HashMap<>();
	protected Map<LineString, String> names = new HashMap<>();

	public Map<AreaInstruction, List<Geometry>> getAreas()
	{
		return areas;
	}

	public Map<WayInstruction, List<LineString>> getWays()
	{
		return ways;
	}

	public Map<LineString, String> getNames()
	{
		return names;
	}

	@Override
	public List<LineString> getWays(Instruction instruction)
	{
		return ways.get(instruction);
	}

	@Override
	public List<Geometry> getAreas(Instruction instruction)
	{
		return areas.get(instruction);
	}

	@Override
	public String getName(LineString string)
	{
		return names.get(string);
	}

}
