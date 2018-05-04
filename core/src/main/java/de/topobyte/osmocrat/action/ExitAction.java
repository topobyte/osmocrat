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

import java.awt.event.ActionEvent;

import de.topobyte.osmocrat.OsmocratMainUI;

public class ExitAction extends OsmocratAction
{

	private static final long serialVersionUID = 1L;

	public ExitAction(OsmocratMainUI osmocrat)
	{
		super(osmocrat, "Exit", "Quit the application");
		setIcon("res/images/24/gtk-quit.png");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.exit(0);
	}

}
