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

import static de.topobyte.inkscape4j.Styles.style;

import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import de.topobyte.adt.geo.BBox;
import de.topobyte.chromaticity.ColorCode;
import de.topobyte.chromaticity.WebColors;
import de.topobyte.inkscape4j.JtsToPath;
import de.topobyte.inkscape4j.Layer;
import de.topobyte.inkscape4j.SvgFile;
import de.topobyte.inkscape4j.path.FillRule;
import de.topobyte.inkscape4j.path.Path;
import de.topobyte.jts.utils.transform.CoordinateGeometryTransformer;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osmocrat.rendering.config.RenderInstructions;
import de.topobyte.osmocrat.rendering.config.instructions.AreaInstruction;
import de.topobyte.osmocrat.rendering.config.instructions.Instruction;
import de.topobyte.osmocrat.rendering.config.instructions.WayInstruction;
import de.topobyte.osmocrat.rendering.config.instructions.area.AreaStyle;
import de.topobyte.osmocrat.rendering.config.instructions.area.SimpleAreaStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.SimpleWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.TwofoldWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.WayStyle;

public class InkscapeConfigMapRenderer
{

	// Some fields that define the map colors and street line widths
	private ColorCode cBBox = WebColors.BLUE.color();

	// This will be used to map geometry coordinates to screen coordinates
	private MercatorImage mercatorImage;

	private BBox bbox;

	private boolean drawBoundingBox = true;

	private RenderInstructions instructions;

	private Map<AreaInstruction, List<Geometry>> areas;
	private Map<WayInstruction, List<LineString>> ways;
	private Map<LineString, String> names;

	public InkscapeConfigMapRenderer(BBox bbox, MercatorImage mercatorImage,
			RenderInstructions instructions,
			Map<AreaInstruction, List<Geometry>> areas,
			Map<WayInstruction, List<LineString>> ways,
			Map<LineString, String> names)
	{
		this.bbox = bbox;
		this.mercatorImage = mercatorImage;
		this.instructions = instructions;
		this.areas = areas;
		this.ways = ways;
		this.names = names;
	}

	public boolean isDrawBoundingBox()
	{
		return drawBoundingBox;
	}

	public void setDrawBoundingBox(boolean drawBoundingBox)
	{
		this.drawBoundingBox = drawBoundingBox;
	}

	private CoordinateGeometryTransformer transformer;

	private Layer layer = null;
	private int id = 1;

	private String id()
	{
		return Integer.toString(id++);
	}

	public void paint(SvgFile svg)
	{
		transformer = new CoordinateGeometryTransformer(mercatorImage);

		int layerNum = 1;
		for (Instruction instruction : instructions.getInstructions()) {

			layer = new Layer("layer-" + layerNum);
			svg.getLayers().add(layer);
			layer.setLabel("Layer " + layerNum);

			layerNum += 1;

			if (instruction instanceof WayInstruction) {
				WayInstruction wi = (WayInstruction) instruction;
				List<LineString> strings = ways.get(instruction);
				render(svg, wi, strings);
			} else if (instruction instanceof AreaInstruction) {
				AreaInstruction ai = (AreaInstruction) instruction;
				List<Geometry> geometries = areas.get(instruction);
				render(svg, ai, geometries);
			}
		}

		if (drawBoundingBox) {
			Layer layerBoundingBox = new Layer("bounding-box");
			svg.getLayers().add(layerBoundingBox);
			layerBoundingBox.setLabel("Bounding Box");

			// Also draw a rectangle around the query bounding box
			Geometry queryBox = new GeometryFactory()
					.toGeometry(bbox.toEnvelope());
			Geometry box = transformer.transform(queryBox);

			Path path = JtsToPath.convert("bounding-box", FillRule.EVEN_ODD,
					box);
			layerBoundingBox.getObjects().add(path);
			path.setStyle(style(null, cBBox, 1, 1, 1, 2));
		}
	}

	private void render(SvgFile svg, AreaInstruction ai,
			List<Geometry> geometries)
	{
		AreaStyle style = ai.getStyle();
		if (style instanceof SimpleAreaStyle) {
			render(svg, (SimpleAreaStyle) style, geometries);
		}
	}

	private void render(SvgFile svg, SimpleAreaStyle style,
			List<Geometry> geometries)
	{
		for (Geometry area : geometries) {
			Geometry transformed = transformer.transform(area);
			Path path = JtsToPath.convert(id(), FillRule.EVEN_ODD, transformed);
			layer.getObjects().add(path);
			path.setStyle(style(style.getColor(), null, 1, 1, 1, 0));
		}
	}

	private void render(SvgFile svg, WayInstruction wi,
			List<LineString> strings)
	{
		WayStyle style = wi.getStyle();
		if (style instanceof SimpleWayStyle) {
			render(svg, (SimpleWayStyle) style, strings);
		} else if (style instanceof TwofoldWayStyle) {
			render(svg, (TwofoldWayStyle) style, strings);
		}
	}

	private void render(SvgFile svg, SimpleWayStyle style,
			List<LineString> strings)
	{
		for (LineString string : strings) {
			Geometry transformed = transformer.transform(string);
			Path path = JtsToPath.convert(id(), FillRule.EVEN_ODD, transformed);
			layer.getObjects().add(path);
			path.setStyle(
					style(null, style.getColor(), 1, 1, 1, style.getWidth()));
		}
	}

	private void render(SvgFile svg, TwofoldWayStyle style,
			List<LineString> strings)
	{
		for (LineString string : strings) {
			Geometry transformed = transformer.transform(string);
			Path path = JtsToPath.convert(id(), FillRule.EVEN_ODD, transformed);
			layer.getObjects().add(path);
			path.setStyle(
					style(null, style.getBg(), 1, 1, 1, style.getWidthBG()));
		}

		for (LineString string : strings) {
			Geometry transformed = transformer.transform(string);
			Path path = JtsToPath.convert(id(), FillRule.EVEN_ODD, transformed);
			layer.getObjects().add(path);
			path.setStyle(
					style(null, style.getFg(), 1, 1, 1, style.getWidthFG()));
		}
	}

}
