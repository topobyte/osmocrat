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

import java.util.ArrayList;
import java.util.List;

import de.topobyte.osmocrat.rendering.config.instructions.Instruction;

public class RenderInstructions
{

	private List<Instruction> instructions = new ArrayList<>();

	public void add(Instruction instruction)
	{
		instructions.add(instruction);
	}

	public List<Instruction> getInstructions()
	{
		return instructions;
	}

	public void setInstructions(List<Instruction> instructions)
	{
		this.instructions = instructions;
	}

}
