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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import de.topobyte.awt.util.GraphicsUtil;

public class ConfigMapRendererPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	// Some fields that define the map colors and street line widths
	private Color cBackground = new Color(0xEEEEEE);

	private ConfigMapRenderer configRenderer;

	public ConfigMapRendererPanel(ConfigMapRenderer configRenderer)
	{
		this.configRenderer = configRenderer;

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
		configRenderer.refreshMercatorImage(getWidth(), getHeight());
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

		configRenderer.paint(graphics);
	}

}
