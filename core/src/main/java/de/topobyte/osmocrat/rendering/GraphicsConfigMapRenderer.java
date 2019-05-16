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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import de.topobyte.adt.geo.BBox;
import de.topobyte.awt.util.GraphicsUtil;
import de.topobyte.chromaticity.AwtColors;
import de.topobyte.chromaticity.ColorCode;
import de.topobyte.chromaticity.WebColors;
import de.topobyte.jts2awt.Jts2Awt;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osmocrat.rendering.config.RenderInstructions;
import de.topobyte.osmocrat.rendering.config.instructions.AreaInstruction;
import de.topobyte.osmocrat.rendering.config.instructions.Instruction;
import de.topobyte.osmocrat.rendering.config.instructions.WayInstruction;
import de.topobyte.osmocrat.rendering.config.instructions.area.AreaStyle;
import de.topobyte.osmocrat.rendering.config.instructions.area.SimpleAreaStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.DashedWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.SimpleWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.TwofoldWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.WayStyle;

public class GraphicsConfigMapRenderer
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

	public GraphicsConfigMapRenderer(BBox bbox, MercatorImage mercatorImage,
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

	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		GraphicsUtil.useAntialiasing(g, true);

		for (Instruction instruction : instructions.getInstructions()) {
			if (instruction instanceof WayInstruction) {
				WayInstruction wi = (WayInstruction) instruction;
				List<LineString> strings = ways.get(instruction);
				render(g, wi, strings);
			} else if (instruction instanceof AreaInstruction) {
				AreaInstruction ai = (AreaInstruction) instruction;
				List<Geometry> geometries = areas.get(instruction);
				render(g, ai, geometries);
			}
		}

		if (drawBoundingBox) {
			// Also draw a rectangle around the query bounding box
			Geometry queryBox = new GeometryFactory()
					.toGeometry(bbox.toEnvelope());
			Shape shape = Jts2Awt.toShape(queryBox, mercatorImage);
			g.setColor(AwtColors.convert(cBBox));
			g.setStroke(new BasicStroke(2));
			g.draw(shape);
		}
	}

	private void render(Graphics2D g, AreaInstruction ai,
			List<Geometry> geometries)
	{
		AreaStyle style = ai.getStyle();
		if (style instanceof SimpleAreaStyle) {
			render(g, (SimpleAreaStyle) style, geometries);
		}
	}

	private void render(Graphics2D g, SimpleAreaStyle style,
			List<Geometry> geometries)
	{
		g.setColor(AwtColors.convert(style.getColor()));
		for (Geometry area : geometries) {
			Shape polygon = Jts2Awt.toShape(area, mercatorImage);
			g.fill(polygon);
		}
	}

	private void render(Graphics2D g, WayInstruction wi,
			List<LineString> strings)
	{
		WayStyle style = wi.getStyle();
		if (style instanceof SimpleWayStyle) {
			render(g, (SimpleWayStyle) style, strings);
		} else if (style instanceof TwofoldWayStyle) {
			render(g, (TwofoldWayStyle) style, strings);
		} else if (style instanceof DashedWayStyle) {
			render(g, (DashedWayStyle) style, strings);
		}
	}

	private void render(Graphics2D g, SimpleWayStyle style,
			List<LineString> strings)
	{
		g.setColor(AwtColors.convert(style.getColor()));
		g.setStroke(new BasicStroke(style.getWidth(), BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		for (LineString string : strings) {
			Path2D path = Jts2Awt.getPath(string, mercatorImage);
			g.draw(path);
		}
	}

	private void render(Graphics2D g, TwofoldWayStyle style,
			List<LineString> strings)
	{
		g.setColor(AwtColors.convert(style.getBg()));
		g.setStroke(new BasicStroke(style.getWidthBG(), BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		for (LineString string : strings) {
			Path2D path = Jts2Awt.getPath(string, mercatorImage);
			g.draw(path);
		}

		g.setColor(AwtColors.convert(style.getFg()));
		g.setStroke(new BasicStroke(style.getWidthFG(), BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		for (LineString string : strings) {
			Path2D path = Jts2Awt.getPath(string, mercatorImage);
			g.draw(path);
		}
	}

	private void render(Graphics2D g, DashedWayStyle style,
			List<LineString> strings)
	{
		List<Float> dashArray = style.getDashArray();
		float[] dash = new float[dashArray.size()];
		for (int i = 0; i < dashArray.size(); i++) {
			dash[i] = dashArray.get(i);
		}

		g.setColor(AwtColors.convert(style.getColor()));
		g.setStroke(new BasicStroke(style.getWidth(), BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND, 10.0f, dash, 0f));
		for (LineString string : strings) {
			Path2D path = Jts2Awt.getPath(string, mercatorImage);
			g.draw(path);
		}
	}

}
