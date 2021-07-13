// Copyright 2021 Sebastian Kuerten
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

package de.topobyte.osmocrat.nodenetworks;

import static de.topobyte.osm4j.core.model.util.OsmModelUtil.getTagsAsMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.topobyte.geomath.WGS84;
import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.core.access.OsmReader;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.dataset.ListDataSetLoader;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.xml.dynsax.OsmXmlReader;
import de.topobyte.overpass.OverpassUtil;

/**
 * This tool analyzes cycle node networks and prints some info and links about
 * nodes within the network that have special properties. Information is given
 * for nodes that have 3 or more nodes with the same ref within 200 meters to
 * one another.
 */
public class TestFindNodes
{

	private static int MIN_REFS = 3;
	private static int MAX_DISTANCE = 200;

	public static void main(String[] args) throws IOException, OsmInputException
	{
		String query = OverpassUtil.query(
				"relation[\"type\"=\"network\"][\"network\"=\"rcn\"][\"network:type\"=\"node_network\"];out;");

		InputStream input = new URL(query).openStream();

		OsmReader reader = new OsmXmlReader(input, false);
		InMemoryListDataSet data = ListDataSetLoader.read(reader, true, true,
				true);

		for (OsmRelation relation : data.getRelations()) {
			Map<String, String> tags = getTagsAsMap(relation);
			Result result = query(relation.getId());
			if (result == null) {
				continue;
			}
			System.out.println(String.format("# Relation %d (%s)",
					relation.getId(), tags.get("name")));
			for (String ref : result.found) {
				Collection<OsmNode> nodes = result.refToNodes.get(ref);
				System.out.println(String.format("## ref %s: %d occurrences",
						ref, nodes.size()));
				OsmNode node = nodes.iterator().next();
				System.out.println(String.format(
						"* <https://www.openstreetmap.org/node/%d>",
						node.getId()));
				System.out.println(String.format(
						"* <https://knooppuntnet.nl/en/analysis/node/%d/map>",
						node.getId()));
			}
		}
	}

	private static class Result
	{

		private List<String> found;
		private Multimap<String, OsmNode> refToNodes;

		public Result(List<String> found, Multimap<String, OsmNode> refToNodes)
		{
			this.found = found;
			this.refToNodes = refToNodes;
		}

	}

	private static Result query(long relationId)
			throws MalformedURLException, IOException, OsmInputException
	{
		String query = OverpassUtil.query(
				String.format("relation(%d);( ._; node(r););out;", relationId));
		InputStream input = new URL(query).openStream();

		OsmReader reader = new OsmXmlReader(input, false);
		InMemoryListDataSet data = ListDataSetLoader.read(reader, true, true,
				true);

		Multimap<String, OsmNode> refToNodes = HashMultimap.create();

		for (OsmNode node : data.getNodes()) {
			Map<String, String> tags = getTagsAsMap(node);
			String ref = tags.get("rcn_ref");
			refToNodes.put(ref, node);
		}

		List<String> found = new ArrayList<>();

		refs: for (String ref : refToNodes.keySet()) {
			Collection<OsmNode> nodes = refToNodes.get(ref);
			if (nodes.size() >= MIN_REFS) {
				for (OsmNode a : nodes) {
					for (OsmNode b : nodes) {
						if (a == b) {
							continue;
						}
						double meters = WGS84.haversineDistance(
								a.getLongitude(), a.getLatitude(),
								b.getLongitude(), b.getLatitude());
						if (meters > MAX_DISTANCE) {
							continue refs;
						}
					}
				}
				found.add(ref);
			}
		}

		if (found.isEmpty()) {
			return null;
		}

		Collections.sort(found);

		return new Result(found, refToNodes);
	}

}
