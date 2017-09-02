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

import de.topobyte.osm4j.core.model.iface.OsmNode;

public class NodeCellRenderer extends BaseCellRenderer<OsmNode>
		implements ListCellRenderer<OsmNode>
{

	private static final long serialVersionUID = -2034252073227837573L;

	public NodeCellRenderer()
	{
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends OsmNode> list,
			OsmNode node, int index, boolean isSelected, boolean cellHasFocus)
	{
		setText(String.format("%d: %.6f, %.6f %s", node.getId(),
				node.getLatitude(), node.getLongitude(), formatTags(node)));

		setBackground(list, isSelected);

		return this;
	}

}
