// Copyright 2017 Sebastian Kuerten
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

package de.topobyte.osmocrat;

import de.topobyte.utilities.apache.commons.cli.commands.RunnerException;

public class TestOsmocratGuiBerlin
{

	public static void main(String[] args) throws RunnerException
	{
		Osmocrat.main(new String[] { "gui", "--input", "/tmp/Berlin.tbo" });
	}

}
