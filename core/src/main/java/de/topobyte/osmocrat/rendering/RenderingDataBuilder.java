// Copyright 2019 Sebastian Kuerten
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.topobyte.osmocrat.rendering.config.instructions.Instructions;
import de.topobyte.osmocrat.rendering.config.instructions.WayInstruction;

public class RenderingDataBuilder
{

	final static Logger logger = LoggerFactory.getLogger(RenderingDataBuilder.class);

	private MercatorImage mercatorImage;
	private InMemoryListDataSet data;
	private RenderInstructions instructions;

	private CachedRenderingDataSource renderingData = new CachedRenderingDataSource();

	public RenderingDataBuilder(MercatorImage mercatorImage, InMemoryListDataSet data,
			RenderInstructions instructions)
	{
		this.mercatorImage = mercatorImage;
		this.data = data;
		this.instructions = instructions;
	}

	public CachedRenderingDataSource getRenderingData()
	{
		return renderingData;
	}

	public void buildRenderingData()
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

		Envelope envelope = mercatorImage.getVisibleBoundingBox().toEnvelope();

		List<WayInstruction> wayInstructions = Instructions
				.way(instructions.getInstructions());
		List<AreaInstruction> areaInstructions = Instructions
				.area(instructions.getInstructions());

		for (AreaInstruction instruction : areaInstructions) {
			renderingData.getAreas().put(instruction, new ArrayList<>());
			usedRelationWays.put(instruction, new HashSet<>());
		}

		for (WayInstruction instruction : wayInstructions) {
			renderingData.getWays().put(instruction, new ArrayList<>());
		}

		logger.info("area relations...");
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
				renderingData.getAreas().get(instruction).add(area);
				try {
					wayFinder.findMemberWays(relation,
							usedRelationWays.get(instruction));
				} catch (EntityNotFoundException e) {
					// cannot happen (IGNORE strategy)
				}
			}
		}

		logger.info("area ways...");
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

				renderingData.getAreas().get(instruction).add(area);
			}
		}

		logger.info("ways...");
		// Collect ways
		for (OsmWay way : data.getWays()) {
			Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);

			for (WayInstruction instruction : wayInstructions) {
				if (!instruction.getSelector().matches(tags)) {
					continue;
				}

				Collection<LineString> paths = getLine(way);

				List<LineString> strings = renderingData.getWays()
						.get(instruction);

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
					renderingData.getNames().put(path, name);
				}
			}
		}
	}

	protected WayBuilder wayBuilder = new WayBuilder();
	protected RegionBuilder regionBuilder = new RegionBuilder();

	protected Collection<LineString> getLine(OsmWay way)
	{
		List<LineString> results = new ArrayList<>();
		try {
			WayBuilderResult lines = wayBuilder.build(way, data);
			results.addAll(lines.getLineStrings());
			if (lines.getLinearRing() != null) {
				results.add(lines.getLinearRing());
			}
		} catch (Throwable e) {
			// ignore
		}
		return results;
	}

	protected MultiPolygon getPolygon(OsmWay way)
	{
		try {
			RegionBuilderResult region = regionBuilder.build(way, data);
			return region.getMultiPolygon();
		} catch (Throwable e) {
			return null;
		}
	}

	protected MultiPolygon getPolygon(OsmRelation relation)
	{
		try {
			RegionBuilderResult region = regionBuilder.build(relation, data);
			return region.getMultiPolygon();
		} catch (Throwable e) {
			return null;
		}
	}

}
