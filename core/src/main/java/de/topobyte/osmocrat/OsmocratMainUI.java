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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.locationtech.jts.geom.Envelope;

import com.slimjars.dist.gnu.trove.list.TDoubleList;
import com.slimjars.dist.gnu.trove.list.array.TDoubleArrayList;
import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.Coordinate;
import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmRelationMember;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Relation;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osmocrat.element.ElementXmlDialog;
import de.topobyte.osmocrat.list.EntityModel;
import de.topobyte.osmocrat.list.NodeCellRenderer;
import de.topobyte.osmocrat.list.RelationCellRenderer;
import de.topobyte.osmocrat.list.WayCellRenderer;

public class OsmocratMainUI
{

	private JFrame frame;

	private InMemoryListDataSet data;

	private JList<OsmNode> listNodes;
	private JList<OsmWay> listWays;
	private JList<OsmRelation> listRelations;

	private BBox bbox = null;
	private Coordinate meanNodes = null;
	private BBox bbox80percent = null;

	public OsmocratMainUI(InMemoryListDataSet data)
	{
		this.data = data;
	}

	public InMemoryListDataSet getData()
	{
		return data;
	}

	public void show()
	{
		frame = new JFrame("Osmocrat");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		OsmocratMainMenu menu = new OsmocratMainMenu(this);
		frame.setJMenuBar(menu);

		// Nodes

		EntityModel<OsmNode> modelNodes = new EntityModel<>(data.getNodes());
		listNodes = new JList<>(modelNodes);
		JScrollPane jspNodes = new JScrollPane(listNodes);

		listNodes.setCellRenderer(new NodeCellRenderer());
		listNodes.setPrototypeCellValue(new Node(1, 2, 3));

		addDoubleClickListener(listNodes);
		addRightClickListener(listNodes);

		// Ways

		EntityModel<OsmWay> modelWays = new EntityModel<>(data.getWays());
		listWays = new JList<>(modelWays);
		JScrollPane jspWays = new JScrollPane(listWays);

		listWays.setCellRenderer(new WayCellRenderer());
		listWays.setPrototypeCellValue(new Way(1, new TLongArrayList()));

		addDoubleClickListener(listWays);
		addRightClickListener(listWays);

		// Relations

		EntityModel<OsmRelation> modelRelations = new EntityModel<>(
				data.getRelations());
		listRelations = new JList<>(modelRelations);
		JScrollPane jspRelations = new JScrollPane(listRelations);

		listRelations.setCellRenderer(new RelationCellRenderer());
		listRelations.setPrototypeCellValue(
				new Relation(1, new ArrayList<OsmRelationMember>()));

		addDoubleClickListener(listRelations);
		addRightClickListener(listRelations);

		// Setup tabbed pane

		JTabbedPane tabbed = new JTabbedPane();

		tabbed.add("nodes", jspNodes);
		tabbed.add("ways", jspWays);
		tabbed.add("relations", jspRelations);

		// Filter panel

		JPanel filter = filterPanel();

		// Main layout

		JPanel main = new JPanel(new GridBagLayout());
		frame.setContentPane(main);

		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		c.gridPos(0, 0).weight(1, 0).fill(GridBagConstraints.HORIZONTAL);
		main.add(filter, c.getConstraints());

		c.gridPos(0, 1).weight(1, 1).fill(GridBagConstraints.BOTH);
		main.add(tabbed, c.getConstraints());

		// Show frame

		frame.setSize(800, 600);
		frame.setVisible(true);
	}

	private JPanel filterPanel()
	{
		JPanel filter = new JPanel(new GridBagLayout());
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		JTextField input = new JTextField();
		JButton button = new JButton("filter");

		c.gridPos(0, 0).weight(1, 0).fill(GridBagConstraints.HORIZONTAL);
		filter.add(input, c.getConstraints());
		c.gridPos(1, 0).weight(0, 0).fill(GridBagConstraints.NONE);
		filter.add(button, c.getConstraints());

		button.addActionListener(e -> filter(input.getText()));

		return filter;
	}

	private void set(List<OsmNode> nodes, List<OsmWay> ways,
			List<OsmRelation> relations)
	{
		EntityModel<OsmNode> modelNodes = new EntityModel<>(nodes);
		listNodes.setModel(modelNodes);

		EntityModel<OsmWay> modelWays = new EntityModel<>(ways);
		listWays.setModel(modelWays);

		EntityModel<OsmRelation> modelRelations = new EntityModel<>(relations);
		listRelations.setModel(modelRelations);
	}

	private void filter(String text)
	{
		String trimmed = text.trim();
		if (trimmed.isEmpty()) {
			noFilter();
			return;
		}
		Pattern pattern = Pattern.compile("(.*)=(.*)");
		Matcher matcher = pattern.matcher(text);
		if (matcher.matches()) {
			String key = matcher.group(1).trim();
			String value = matcher.group(2).trim();
			filter(key, value);
		}
	}

	private void noFilter()
	{
		set(data.getNodes(), data.getWays(), data.getRelations());
	}

	private void filter(String key, String value)
	{
		List<OsmNode> nodes = filter(data.getNodes(), key, value);
		List<OsmWay> ways = filter(data.getWays(), key, value);
		List<OsmRelation> relations = filter(data.getRelations(), key, value);
		set(nodes, ways, relations);
	}

	private <T extends OsmEntity> List<T> filter(List<T> unfiltered, String key,
			String value)
	{
		List<T> filtered = new ArrayList<>();
		for (T element : unfiltered) {
			Map<String, String> tags = OsmModelUtil.getTagsAsMap(element);
			String keyValue = tags.get(key);
			if (keyValue == null) {
				continue;
			}
			if (keyValue.equals(value)) {
				filtered.add(element);
			}
		}
		return filtered;
	}

	private <T extends OsmEntity> void addDoubleClickListener(JList<T> list)
	{
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				OsmEntity element = list.getSelectedValue();
				if (element == null) {
					return;
				}
				if (e.getClickCount() == 2) {
					showElementDialog(element);
				}
			}

		});
	}

	private <T extends OsmEntity> void addRightClickListener(JList<T> list)
	{
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				OsmEntity element = list.getSelectedValue();
				if (element == null) {
					return;
				}
				if (!SwingUtilities.isRightMouseButton(e)) {
					return;
				}

				JPopupMenu popup = new JPopupMenu();

				JMenuItem itemInfo = new JMenuItem("info");
				popup.add(itemInfo);

				itemInfo.addActionListener(event -> {
					showElementDialog(element);
				});

				popup.show(list, e.getX(), e.getY());
			}

		});
	}

	protected void showElementDialog(OsmEntity element)
	{
		ElementXmlDialog dialog = new ElementXmlDialog(frame, element);
		dialog.setSize(500, 400);
		dialog.setVisible(true);
	}

	public BBox getBoundingBox()
	{
		if (bbox == null) {
			calculateBoudingBox();
		}
		return bbox;
	}

	public Coordinate getNodeMedian()
	{
		calculateDataCharacteristics();
		return meanNodes;
	}

	public BBox get80PercentArea()
	{
		calculateDataCharacteristics();
		return bbox80percent;
	}

	private void calculateBoudingBox()
	{
		List<OsmNode> nodes = data.getNodes();

		Envelope envelope = new Envelope();
		for (OsmNode node : nodes) {
			envelope.expandToInclude(node.getLongitude(), node.getLatitude());
		}

		bbox = new BBox(envelope);
	}

	private boolean calculatedCharacteristics = false;

	public void calculateDataCharacteristics()
	{
		if (calculatedCharacteristics) {
			return;
		}

		calculateDataCharacteristicsInternal();
		calculatedCharacteristics = true;
	}

	private void calculateDataCharacteristicsInternal()
	{
		List<OsmNode> nodes = data.getNodes();

		TDoubleList lats = new TDoubleArrayList(nodes.size());
		TDoubleList lons = new TDoubleArrayList(nodes.size());

		for (OsmNode node : nodes) {
			lats.add(node.getLatitude());
			lons.add(node.getLongitude());
		}

		lats.sort();
		lons.sort();

		double meanLat = lats.get(lats.size() / 2);
		double meanLon = lons.get(lons.size() / 2);

		meanNodes = new Coordinate(meanLon, meanLat);

		double d = 0.8;
		int n1 = (int) Math.round(nodes.size() * (1 - d));
		int n2 = (int) Math.round(nodes.size() * d);

		bbox80percent = new BBox(lons.get(n1), lats.get(n1), lons.get(n2),
				lats.get(n2));
	}

}
