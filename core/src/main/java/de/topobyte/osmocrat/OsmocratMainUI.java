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

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmRelationMember;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Relation;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osmocrat.list.EntityModel;
import de.topobyte.osmocrat.list.NodeCellRenderer;
import de.topobyte.osmocrat.list.RelationCellRenderer;
import de.topobyte.osmocrat.list.WayCellRenderer;

public class OsmocratMainUI
{

	private InMemoryListDataSet data;

	public OsmocratMainUI(InMemoryListDataSet data)
	{
		this.data = data;
	}

	public void show()
	{
		JFrame frame = new JFrame("Osmocrat");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Nodes

		JList<OsmNode> listNodes = new JList<>(
				new EntityModel<>(data.getNodes()));
		JScrollPane jspNodes = new JScrollPane(listNodes);

		listNodes.setCellRenderer(new NodeCellRenderer());
		listNodes.setPrototypeCellValue(new Node(1, 2, 3));

		// Ways

		JList<OsmWay> listWays = new JList<>(new EntityModel<>(data.getWays()));
		JScrollPane jspWays = new JScrollPane(listWays);

		listWays.setCellRenderer(new WayCellRenderer());
		listWays.setPrototypeCellValue(new Way(1, new TLongArrayList()));

		// Relations

		JList<OsmRelation> listRelations = new JList<>(
				new EntityModel<>(data.getRelations()));
		JScrollPane jspRelations = new JScrollPane(listRelations);

		listRelations.setCellRenderer(new RelationCellRenderer());
		listRelations.setPrototypeCellValue(
				new Relation(1, new ArrayList<OsmRelationMember>()));

		// Setup tabbed pane

		JTabbedPane tabbed = new JTabbedPane();
		frame.setContentPane(tabbed);

		tabbed.add("nodes", jspNodes);
		tabbed.add("ways", jspWays);
		tabbed.add("relations", jspRelations);

		frame.setSize(800, 600);
		frame.setVisible(true);
	}

}
