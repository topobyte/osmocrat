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
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JPanel;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import de.topobyte.adt.geo.BBox;
import de.topobyte.awt.util.GraphicsUtil;
import de.topobyte.jts2awt.Jts2Awt;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.model.iface.OsmNode;

public class OverviewRenderer extends JPanel
{

	private static final long serialVersionUID = 1L;

	private Color cBackground = new Color(0xEEEEEE);
	private Color cNodes = Color.BLACK;
	private Color cBBox = Color.BLUE;;

	// This will be used to map geometry coordinates to screen coordinates
	private MercatorImage mercatorImage;

	// We need to keep the reference to the bounding box, so that we can create
	// a new MercatorImage if the size of our panel changes
	private BBox bbox;

	private InMemoryListDataSet data;

	public OverviewRenderer(BBox bbox, MercatorImage mercatorImage,
			InMemoryListDataSet data)
	{
		this.bbox = bbox;
		this.mercatorImage = mercatorImage;
		this.data = data;

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

		// Render nodes
		g.setColor(cNodes);

		GraphicsUtil.useAntialiasing(g, false);
		List<OsmNode> nodes = data.getNodes();
		for (OsmNode node : nodes) {
			double x = mercatorImage.getX(node.getLongitude());
			double y = mercatorImage.getY(node.getLatitude());
			Rectangle2D rect = new Rectangle2D.Double(x, y, 1, 1);
			g.draw(rect);
		}

		GraphicsUtil.useAntialiasing(g, true);
		// Also draw a rectangle around the query bounding box
		Geometry queryBox = new GeometryFactory().toGeometry(bbox.toEnvelope());
		Shape shape = Jts2Awt.toShape(queryBox, mercatorImage);
		g.setColor(cBBox);
		g.setStroke(new BasicStroke(2));
		g.draw(shape);
	}

}
