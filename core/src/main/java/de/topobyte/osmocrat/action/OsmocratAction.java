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

package de.topobyte.osmocrat.action;

import de.topobyte.osmocrat.OsmocratMainUI;
import de.topobyte.swing.util.action.SimpleAction;

public abstract class OsmocratAction extends SimpleAction
{

	private static final long serialVersionUID = 1L;

	protected OsmocratMainUI osmocrat;

	public OsmocratAction(OsmocratMainUI osmocrat, String name,
			String description)
	{
		super(name, description);
		this.osmocrat = osmocrat;
	}

}
