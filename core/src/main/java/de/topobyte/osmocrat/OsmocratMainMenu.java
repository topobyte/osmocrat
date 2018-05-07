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

package de.topobyte.osmocrat;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.topobyte.osmocrat.action.AboutAction;
import de.topobyte.osmocrat.action.ExitAction;
import de.topobyte.osmocrat.action.RenderMapAction;
import de.topobyte.osmocrat.action.RenderMapOfMedianRegionAction;
import de.topobyte.osmocrat.action.RenderOverviewAction;

public class OsmocratMainMenu extends JMenuBar
{

	private static final long serialVersionUID = 1L;

	public OsmocratMainMenu(OsmocratMainUI osmocrat)
	{
		JMenu menuFile = new JMenu("File");
		add(menuFile);

		menuFile.add(new JMenuItem(new ExitAction(osmocrat)));

		JMenu menuTools = new JMenu("Tools");
		add(menuTools);

		menuTools.add(new JMenuItem(new RenderOverviewAction(osmocrat)));
		menuTools.add(
				new JMenuItem(new RenderMapOfMedianRegionAction(osmocrat)));
		menuTools.add(new JMenuItem(new RenderMapAction(osmocrat)));

		JMenu menuHelp = new JMenu("Help");
		add(menuHelp);

		menuHelp.add(new JMenuItem(new AboutAction(osmocrat)));
	}

}
