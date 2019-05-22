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

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import de.topobyte.adt.geo.BBox;
import de.topobyte.chromaticity.ColorCode;
import de.topobyte.chromaticity.WebColors;
import de.topobyte.inkscape4j.JtsToPath;
import de.topobyte.inkscape4j.Layer;
import de.topobyte.inkscape4j.ShapeToPath;
import de.topobyte.inkscape4j.SvgFile;
import de.topobyte.inkscape4j.path.FillRule;
import de.topobyte.inkscape4j.path.Path;
import de.topobyte.inkscape4j.style.LineCap;
import de.topobyte.inkscape4j.style.LineJoin;
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
import de.topobyte.osmocrat.text.TextIntersectionChecker;
import de.topobyte.osmocrat.text.TextIntersectionCheckerTree;
import de.topobyte.osmocrat.text.TextUtil;
import de.topobyte.osmocrat.text.awt.AwtTextUtil;
import de.topobyte.osmocrat.text.awt.TextPath;

public class InkscapeConfigMapRenderer
{

	// Some fields that define the map colors and street line widths
	private ColorCode cBBox = WebColors.BLUE.color();

	// This will be used to map geometry coordinates to screen coordinates
	private MercatorImage mercatorImage;

	private BBox bbox;

	private boolean drawBoundingBox = true;
	private boolean drawTextBoxes = false;

	private RenderInstructions instructions;

	private Map<AreaInstruction, List<Geometry>> areas;
	private Map<WayInstruction, List<LineString>> ways;
	private Map<LineString, String> names;

	private TextIntersectionChecker textIntersectionChecker;

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

	private Layer layerTextBoxes;

	private String id()
	{
		return Integer.toString(id++);
	}

	public void paint(SvgFile svg)
	{
		transformer = new CoordinateGeometryTransformer(mercatorImage);

		textIntersectionChecker = new TextIntersectionCheckerTree();

		if (drawTextBoxes) {
			layerTextBoxes = new Layer("text-boxes");
			layerTextBoxes.setLabel("Text boxes");
		}

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

		if (drawTextBoxes) {
			svg.getLayers().add(layerTextBoxes);
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
			path.getStyle().setLineCap(LineCap.ROUND);
			path.getStyle().setLineJoin(LineJoin.ROUND);
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
		} else if (style instanceof DashedWayStyle) {
			render(svg, (DashedWayStyle) style, strings);
		} else if (style instanceof TextWayStyle) {
			render(svg, (TextWayStyle) style, strings);
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
			path.getStyle().setLineCap(LineCap.ROUND);
			path.getStyle().setLineJoin(LineJoin.ROUND);
		}
	}

	private void render(SvgFile svg, DashedWayStyle style,
			List<LineString> strings)
	{
		List<Float> dashArray = style.getDashArray();
		float[] dash = new float[dashArray.size()];
		for (int i = 0; i < dashArray.size(); i++) {
			dash[i] = dashArray.get(i);
		}

		for (LineString string : strings) {
			Geometry transformed = transformer.transform(string);
			Path path = JtsToPath.convert(id(), FillRule.EVEN_ODD, transformed);
			layer.getObjects().add(path);
			path.setStyle(
					style(null, style.getColor(), 1, 1, 1, style.getWidth()));
			path.getStyle().setLineCap(LineCap.ROUND);
			path.getStyle().setLineJoin(LineJoin.ROUND);
			path.getStyle().setDashArray(dash);
			path.getStyle().setDashOffset(style.getDashPhase());
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
			path.getStyle().setLineCap(LineCap.ROUND);
			path.getStyle().setLineJoin(LineJoin.ROUND);
		}

		for (LineString string : strings) {
			Geometry transformed = transformer.transform(string);
			Path path = JtsToPath.convert(id(), FillRule.EVEN_ODD, transformed);
			layer.getObjects().add(path);
			path.setStyle(
					style(null, style.getFg(), 1, 1, 1, style.getWidthFG()));
			path.getStyle().setLineCap(LineCap.ROUND);
			path.getStyle().setLineJoin(LineJoin.ROUND);
		}
	}

	private void render(SvgFile svg, TextWayStyle style,
			List<LineString> strings)
	{
		for (LineString string : strings) {
			String name = names.get(string);
			if (name == null) {
				continue;
			}
			renderLabel(svg, string, name, style);
		}
	}

	private void renderLabel(SvgFile svg, LineString string, String label,
			TextWayStyle style)
	{
		Path2D path = Jts2Awt.getPath(string, mercatorImage);
		LineString stringImage = (LineString) new CoordinateGeometryTransformer(
				mercatorImage).transform(string);

		double padding = 5;

		Font font = new Font(style.getFontName(), Font.PLAIN, style.getSize());

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
			for (float[] box : boxes) {
				Polygon polygon = GeneralRectangle.createPolygon(box);
				Path p = JtsToPath.convert(id(), FillRule.EVEN_ODD, polygon);
				layerTextBoxes.getObjects().add(p);
				p.setStyle(style(null, WebColors.GREEN.color(), 1, 1, 1, 1));
				p.getStyle().setLineCap(LineCap.ROUND);
				p.getStyle().setLineJoin(LineJoin.ROUND);
			}
		}

		TextPath line = AwtTextUtil.createLine(path, (float) paddedTextLength,
				(float) offset);
		Path2D p = line.getPath();

		if (isReverse.value) {
			p = AwtTextUtil.reverse(p);
		}

		Shape shape = AwtTextUtil.createStrokedShape(p, font, label);

		Path labelPath1 = ShapeToPath.convert(id(), FillRule.EVEN_ODD, shape);
		Path labelPath2 = ShapeToPath.convert(id(), FillRule.EVEN_ODD, shape);

		// we could avoid the duplication with this:
		// style="paint-order:stroke fill markers", but then it will really only
		// work in Inkscape

		layer.getObjects().add(labelPath1);
		labelPath1.setStyle(style(null, style.getColorOutline(), 1, 1, 1,
				style.getWidthOutline()));
		labelPath1.getStyle().setLineCap(LineCap.ROUND);
		labelPath1.getStyle().setLineJoin(LineJoin.ROUND);

		layer.getObjects().add(labelPath2);
		labelPath2.setStyle(style(style.getColor(), null, 1, 1, 1,
				style.getWidthOutline()));
	}

}
