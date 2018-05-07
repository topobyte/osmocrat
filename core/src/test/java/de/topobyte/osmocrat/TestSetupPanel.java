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

package de.topobyte.osmocrat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.topobyte.adt.geo.BBox;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osmocrat.rendersetup.SetupPanel;

public class TestSetupPanel extends JPanel
{

	private static final long serialVersionUID = -8099819323319869874L;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Test SetupPanel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 500);

		TestSetupPanel panel = new TestSetupPanel();

		frame.setContentPane(panel);
		frame.setVisible(true);
	}

	private SetupPanel panel;

	public TestSetupPanel()
	{
		setLayout(new GridBagLayout());

		BBox bbox = new BBox(13.38529, 52.51434, 13.40641, 52.52259);
		panel = new SetupPanel(bbox, 700, 500);

		JButton button = new JButton("Print Info");

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		add(panel, c);

		c.gridy = 1;
		add(button, c);

		button.addActionListener(e -> {
			printInfo();
		});
	}

	public void printInfo()
	{
		BBox boundingBox = panel.getBoundingBox();
		int width = panel.getImageWidth();
		int height = panel.getImageHeight();

		MercatorImage mapImage = new MercatorImage(boundingBox, width, height);
		BBox box = mapImage.getVisibleBoundingBox();

		double scale = mapImage.getWorldSize();
		double base = Math.log(scale) / Math.log(2);
		double zoom = base - 8;

		System.out.println("Bounding Box: " + box);
		System.out.println("Zoom: " + zoom);
	}

}
