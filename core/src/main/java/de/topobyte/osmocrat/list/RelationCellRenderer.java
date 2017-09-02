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

package de.topobyte.osmocrat.list;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.topobyte.osm4j.core.model.iface.OsmRelation;

public class RelationCellRenderer extends BaseCellRenderer<OsmRelation>
		implements ListCellRenderer<OsmRelation>
{

	private static final long serialVersionUID = -6806509850846829355L;

	public RelationCellRenderer()
	{
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends OsmRelation> list, OsmRelation relation, int index,
			boolean isSelected, boolean cellHasFocus)
	{
		setText(String.format("%d: %d members %s", relation.getId(),
				relation.getNumberOfMembers(), formatTags(relation)));

		setBackground(list, isSelected);

		return this;
	}

}
