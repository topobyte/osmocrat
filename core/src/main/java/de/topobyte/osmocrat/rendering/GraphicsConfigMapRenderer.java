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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import de.topobyte.adt.geo.BBox;
import de.topobyte.awt.util.GraphicsUtil;
import de.topobyte.chromaticity.AwtColors;
import de.topobyte.chromaticity.WebColors;
import de.topobyte.jgs.transform.IdentityCoordinateTransformer;
import de.topobyte.jts.utils.transform.CoordinateGeometryTransformer;
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
import de.topobyte.osmocrat.rendering.config.instructions.ways.TextWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.TwofoldWayStyle;
import de.topobyte.osmocrat.rendering.config.instructions.ways.WayStyle;
import de.topobyte.osmocrat.text.BoolResult;
import de.topobyte.osmocrat.text.GeneralRectangle;
import de.topobyte.osmocrat.text.TextIntersectionCheckerTree;
import de.topobyte.osmocrat.text.TextUtil;
import de.topobyte.osmocrat.text.awt.AwtTextUtil;
import de.topobyte.osmocrat.text.awt.TextPath;

public class GraphicsConfigMapRenderer extends BaseConfigMapRenderer
{

	public GraphicsConfigMapRenderer(BBox bbox, MercatorImage mercatorImage,
			RenderInstructions instructions, RenderingDataSource renderingData)
	{
		super(bbox, mercatorImage, instructions, renderingData);
	}

	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		GraphicsUtil.useAntialiasing(g, true);

		textIntersectionChecker = new TextIntersectionCheckerTree();

		for (Instruction instruction : instructions.getInstructions()) {
			if (instruction instanceof WayInstruction) {
				WayInstruction wi = (WayInstruction) instruction;
				List<LineString> strings = renderingData.getWays(instruction);
				render(g, wi, strings);
			} else if (instruction instanceof AreaInstruction) {
				AreaInstruction ai = (AreaInstruction) instruction;
				List<Geometry> geometries = renderingData.getAreas(instruction);
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
		} else if (style instanceof TextWayStyle) {
			render(g, (TextWayStyle) style, strings);
		}
	}

	private void render(Graphics2D g, SimpleWayStyle style,
			List<LineString> strings)
	{
		g.setColor(AwtColors.convert(style.getColor()));
		g.setStroke(new BasicStroke(style.getWidth() * scaleLines,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (LineString string : strings) {
			Path2D path = Jts2Awt.getPath(string, mercatorImage);
			g.draw(path);
		}
	}

	private void render(Graphics2D g, TwofoldWayStyle style,
			List<LineString> strings)
	{
		g.setColor(AwtColors.convert(style.getBg()));
		g.setStroke(new BasicStroke(style.getWidthBG() * scaleLines,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (LineString string : strings) {
			Path2D path = Jts2Awt.getPath(string, mercatorImage);
			g.draw(path);
			// System.out.println("lineto");
		}

		g.setColor(AwtColors.convert(style.getFg()));
		g.setStroke(new BasicStroke(style.getWidthFG() * scaleLines,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
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

		DashArrays.scale(dash, scaleLines);

		g.setColor(AwtColors.convert(style.getColor()));
		g.setStroke(new BasicStroke(style.getWidth() * scaleLines,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash,
				style.getDashPhase() * scaleLines));
		for (LineString string : strings) {
			Path2D path = Jts2Awt.getPath(string, mercatorImage);
			g.draw(path);
		}
	}

	private void render(Graphics2D g, TextWayStyle style,
			List<LineString> strings)
	{
		for (LineString string : strings) {
			String name = renderingData.getName(string);
			if (name == null) {
				continue;
			}
			renderLabel(g, string, name, style);
		}
	}

	private void renderLabel(Graphics2D g, LineString string, String label,
			TextWayStyle style)
	{
		Path2D path = Jts2Awt.getPath(string, mercatorImage);
		LineString stringImage = (LineString) new CoordinateGeometryTransformer(
				mercatorImage).transform(string);

		double padding = 5 * scaleText;

		int fontSize = (int) (style.getSize() * scaleText + 0.5);
		Font font = new Font(style.getFontName(), Font.PLAIN, fontSize);

		float pathLength = AwtTextUtil.measurePathLength(path);
		double textLength = AwtTextUtil.getTextWidth(font, label);
		double paddedTextLength = textLength + 2 * padding;

		if (paddedTextLength > pathLength) {
			return;
		}

		double offset = (pathLength - paddedTextLength) / 2;

		BoolResult isReverse = new BoolResult();
		float[][] boxes = TextUtil.createTextBoxes(stringImage, offset,
				paddedTextLength, style.getSize(), isReverse);

		if (!textIntersectionChecker.isValid(boxes)) {
			return;
		}

		textIntersectionChecker.add(boxes);

		if (drawTextBoxes) {
			g.setColor(AwtColors.convert(WebColors.GREEN.color()));
			g.setStroke(new BasicStroke(1));
			for (float[] box : boxes) {
				Polygon polygon = GeneralRectangle.createPolygon(box);
				Area area = Jts2Awt.toShape(polygon,
						new IdentityCoordinateTransformer());
				g.draw(area);
			}
		}

		TextPath line = AwtTextUtil.createLine(path, (float) paddedTextLength,
				(float) offset);
		Path2D p = line.getPath();

		if (isReverse.value) {
			p = AwtTextUtil.reverse(p);
		}

		Shape shape = AwtTextUtil.createStrokedShape(p, font, label);

		g.setColor(AwtColors.convert(style.getColorOutline()));
		g.setStroke(new BasicStroke(style.getWidthOutline() * getScaleText(),
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.draw(shape);

		g.setColor(AwtColors.convert(style.getColor()));
		g.fill(shape);
	}

}
