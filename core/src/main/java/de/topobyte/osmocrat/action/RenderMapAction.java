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

package de.topobyte.osmocrat.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import de.topobyte.adt.geo.BBox;
import de.topobyte.adt.geo.Coordinate;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osmocrat.OsmocratMainUI;
import de.topobyte.osmocrat.rendering.MapRendering;
import de.topobyte.swing.util.EmptyIcon;

public class RenderMapAction extends OsmocratAction
{

	private static final long serialVersionUID = 1L;

	public RenderMapAction(OsmocratMainUI osmocrat)
	{
		super(osmocrat, "Render Map", "Render a map");
		setIcon(new EmptyIcon(24));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Coordinate median = osmocrat.getNodeMedian();

		double dLon = 0.013;
		double dLat = 0.007;

		BBox bbox = new BBox(median.getLongitude() - dLon / 2,
				median.getLatitude() + dLat / 2,
				median.getLongitude() + dLon / 2,
				median.getLatitude() - dLat / 2);

		int width = 800;
		int height = 600;

		MercatorImage mapImage = new MercatorImage(bbox, width, height);

		MapRendering panel = new MapRendering(bbox, mapImage,
				osmocrat.getData());

		panel.setPreferredSize(new Dimension(width, height));

		JFrame frame = new JFrame("Osmocrat Map");
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}

}
