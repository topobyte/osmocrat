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

package de.topobyte.osmocrat.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;

import de.topobyte.adt.geo.BBox;
import de.topobyte.awt.util.GraphicsUtil;
import de.topobyte.chromaticity.AwtColors;
import de.topobyte.jts2awt.Jts2Awt;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.core.resolve.EntityFinder;
import de.topobyte.osm4j.core.resolve.EntityFinders;
import de.topobyte.osm4j.core.resolve.EntityNotFoundException;
import de.topobyte.osm4j.core.resolve.EntityNotFoundStrategy;
import de.topobyte.osm4j.geometry.RegionBuilder;
import de.topobyte.osm4j.geometry.RegionBuilderResult;
import de.topobyte.osm4j.geometry.WayBuilder;
import de.topobyte.osm4j.geometry.WayBuilderResult;
import de.topobyte.osmocrat.rendering.config.RenderInstructions;
import de.topobyte.osmocrat.rendering.config.instructions.AreaInstruction;
import de.topobyte.osmocrat.rendering.config.instructions.Instruction;
import de.topobyte.osmocrat.rendering.config.instructions.Instructions;
import de.topobyte.osmocrat.rendering.config.instructions.WayInstruction;

public class ConfigMapRenderer extends JPanel
{

	private static final long serialVersionUID = 1L;

	// Some fields that define the map colors and street line widths
	private Color cBackground = new Color(0xEEEEEE);
	private Color cBBox = Color.BLUE;

	// This will be used to map geometry coordinates to screen coordinates
	private MercatorImage mercatorImage;

	// We need to keep the reference to the bounding box, so that we can create
	// a new MercatorImage if the size of our panel changes
	private BBox bbox;

	// The data set will be used as entity provider when building geometries
	private InMemoryListDataSet data;

	private RenderInstructions instructions;

	// We build the geometries to be rendered during construction and store them
	// in these fields so that we don't have to recompute everything when
	// rendering.
	private Map<AreaInstruction, List<Geometry>> areas = new HashMap<>();
	private Map<WayInstruction, List<LineString>> ways = new HashMap<>();
	private Map<LineString, String> names = new HashMap<>();

	public ConfigMapRenderer(BBox bbox, MercatorImage mercatorImage,
			InMemoryListDataSet data, RenderInstructions instructions)
	{
		this.bbox = bbox;
		this.mercatorImage = mercatorImage;
		this.data = data;
		this.instructions = instructions;

		// When the panel's size changes, define a new MercatorImage and trigger
		// a repaint on our panel
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				refreshMercatorImage();
				repaint();
			}

		});

		System.out.println("building rendering data...");
		buildRenderingData();
		System.out.println("done");
	}

	private void buildRenderingData()
	{
		// We create area geometries from relations and ways. Ways that are
		// part of multipolygon areas may be tagged as areas themselves,
		// however rendering them independently will fill the polygon holes they
		// are cutting out of the relations. Hence we store the ways found in
		// area relations to skip them later on when working on the ways.
		Map<AreaInstruction, Set<OsmWay>> usedRelationWays = new HashMap<>();
		// We use this to find all way members of relations.
		EntityFinder wayFinder = EntityFinders.create(data,
				EntityNotFoundStrategy.IGNORE);

		Envelope envelope = bbox.toEnvelope();

		List<WayInstruction> wayInstructions = Instructions
				.way(instructions.getInstructions());
		List<AreaInstruction> areaInstructions = Instructions
				.area(instructions.getInstructions());

		for (AreaInstruction instruction : areaInstructions) {
			areas.put(instruction, new ArrayList<>());
			usedRelationWays.put(instruction, new HashSet<>());
		}

		for (WayInstruction instruction : wayInstructions) {
			ways.put(instruction, new ArrayList<>());
		}

		System.out.println("area relations...");
		// Collect areas from relation areas...
		for (OsmRelation relation : data.getRelations()) {
			Map<String, String> tags = OsmModelUtil.getTagsAsMap(relation);

			boolean use = false;
			for (AreaInstruction instruction : areaInstructions) {
				if (instruction.getSelector().matches(tags)) {
					use = true;
					break;
				}
			}

			if (!use) {
				continue;
			}

			MultiPolygon area = getPolygon(relation);
			if (area == null) {
				continue;
			}
			if (!envelope.intersects(area.getEnvelopeInternal())) {
				continue;
			}

			for (AreaInstruction instruction : areaInstructions) {
				if (!instruction.getSelector().matches(tags)) {
					continue;
				}
				areas.get(instruction).add(area);
				try {
					wayFinder.findMemberWays(relation,
							usedRelationWays.get(instruction));
				} catch (EntityNotFoundException e) {
					// cannot happen (IGNORE strategy)
				}
			}
		}

		System.out.println("area ways...");
		// ... and also from way areas
		for (OsmWay way : data.getWays()) {
			Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
			for (AreaInstruction instruction : areaInstructions) {
				if (!instruction.getSelector().matches(tags)) {
					continue;
				}
				Set<OsmWay> usedWays = usedRelationWays.get(instruction);
				if (usedWays.contains(way)) {
					continue;
				}

				MultiPolygon area = getPolygon(way);
				if (area == null) {
					continue;
				}
				if (!envelope.intersects(area.getEnvelopeInternal())) {
					continue;
				}

				areas.get(instruction).add(area);
			}
		}

		System.out.println("ways...");
		// Collect ways
		for (OsmWay way : data.getWays()) {
			Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);

			for (WayInstruction instruction : wayInstructions) {
				if (!instruction.getSelector().matches(tags)) {
					continue;
				}

				Collection<LineString> paths = getLine(way);

				List<LineString> strings = ways.get(instruction);

				for (LineString path : paths) {
					if (!envelope.intersects(path.getEnvelopeInternal())) {
						continue;
					}
					strings.add(path);
				}

				// If it has a name, store it for labeling
				String name = tags.get("name");
				if (name == null) {
					continue;
				}
				for (LineString path : paths) {
					if (!envelope.intersects(path.getEnvelopeInternal())) {
						continue;
					}
					names.put(path, name);
				}
			}
		}
	}

	public void refreshMercatorImage()
	{
		mercatorImage = new MercatorImage(bbox, getWidth(), getHeight());
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		GraphicsUtil.useAntialiasing(g, true);

		// Fill the background
		g.setColor(cBackground);
		g.fillRect(0, 0, getWidth(), getHeight());

		for (Instruction instruction : instructions.getInstructions()) {
			if (instruction instanceof WayInstruction) {
				WayInstruction wi = (WayInstruction) instruction;
				List<LineString> strings = ways.get(instruction);

				g.setColor(AwtColors.convert(wi.getBg()));
				g.setStroke(new BasicStroke(wi.getWidthBG(),
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				for (LineString string : strings) {
					Path2D path = Jts2Awt.getPath(string, mercatorImage);
					g.draw(path);
				}

				g.setColor(AwtColors.convert(wi.getFg()));
				g.setStroke(new BasicStroke(wi.getWidthFG(),
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				for (LineString string : strings) {
					Path2D path = Jts2Awt.getPath(string, mercatorImage);
					g.draw(path);
				}
			} else if (instruction instanceof AreaInstruction) {
				AreaInstruction ai = (AreaInstruction) instruction;
				List<Geometry> geometries = areas.get(instruction);
				g.setColor(AwtColors.convert(ai.getColor()));
				for (Geometry area : geometries) {
					Shape polygon = Jts2Awt.toShape(area, mercatorImage);
					g.fill(polygon);
				}
			}
		}

		// Also draw a rectangle around the query bounding box
		Geometry queryBox = new GeometryFactory().toGeometry(bbox.toEnvelope());
		Shape shape = Jts2Awt.toShape(queryBox, mercatorImage);
		g.setColor(cBBox);
		g.setStroke(new BasicStroke(2));
		g.draw(shape);
	}

	private WayBuilder wayBuilder = new WayBuilder();
	private RegionBuilder regionBuilder = new RegionBuilder();

	private Collection<LineString> getLine(OsmWay way)
	{
		List<LineString> results = new ArrayList<>();
		try {
			WayBuilderResult lines = wayBuilder.build(way, data);
			results.addAll(lines.getLineStrings());
			if (lines.getLinearRing() != null) {
				results.add(lines.getLinearRing());
			}
		} catch (EntityNotFoundException e) {
			// ignore
		}
		return results;
	}

	private MultiPolygon getPolygon(OsmWay way)
	{
		try {
			RegionBuilderResult region = regionBuilder.build(way, data);
			return region.getMultiPolygon();
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	private MultiPolygon getPolygon(OsmRelation relation)
	{
		try {
			RegionBuilderResult region = regionBuilder.build(relation, data);
			return region.getMultiPolygon();
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

}
