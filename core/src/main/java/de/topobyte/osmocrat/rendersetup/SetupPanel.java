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

package de.topobyte.osmocrat.rendersetup;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;

import de.topobyte.adt.geo.BBox;
import de.topobyte.jeography.tools.bboxaction.BboxPanel;
import de.topobyte.jeography.tools.bboxaction.PanelButton;
import de.topobyte.jeography.tools.bboxaction.SelectBboxAction;
import de.topobyte.swing.util.DocumentAdapter;

public class SetupPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	private BBox boundingBox = null;
	private int width = 0;
	private int height = 0;

	private SelectBboxAction selectBboxAction;
	private JTextField fieldWidth, fieldHeight;

	public SetupPanel(BBox boundingBox, int width, int height)
	{
		this.boundingBox = boundingBox;
		this.width = width;
		this.height = height;

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;

		JPanel panelBbox = createBboxPanel();
		panelBbox.setBorder(BorderFactory.createTitledBorder("Bounding box"));
		add(panelBbox, c);

		JPanel panelOptions = createOptionsPanel();
		panelOptions.setBorder(BorderFactory.createTitledBorder("Options"));
		add(panelOptions, c);
	}

	private JPanel createBboxPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		JLabel label = new JLabel("Select a bounding box:");
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(label, c);

		final BboxPanel bboxPanel = new BboxPanel(boundingBox);
		setLabelBorder(bboxPanel, false);
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(bboxPanel, c);

		selectBboxAction = new SelectBboxAction(boundingBox, panel) {

			private static final long serialVersionUID = 1L;

			@Override
			public void bboxSelected(BBox bbox)
			{
				boundingBox = bbox;
				bboxPanel.setBoundingBox(bbox);
				selectBboxAction.setBbox(bbox);
			}

		};

		PanelButton button = new PanelButton(selectBboxAction);
		setButtonBorder(button);
		c.gridx = 1;
		c.weightx = 0.0;
		panel.add(button, c);

		return panel;
	}

	private JPanel createOptionsPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		JLabel label = new JLabel("Set the scale factor:");
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(label, c);

		JTextField field = new JTextField("1.0");
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(field, c);

		JLabel labelWidth = new JLabel("Set the image width:");
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(labelWidth, c);

		fieldWidth = new JTextField("" + width);
		c.gridy = 3;
		c.gridwidth = 1;
		panel.add(fieldWidth, c);

		JLabel labelHeight = new JLabel("Set the image height:");
		c.gridy = 4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(labelHeight, c);

		fieldHeight = new JTextField("" + height);
		c.gridy = 5;
		c.gridwidth = 1;
		panel.add(fieldHeight, c);

		fieldWidth.getDocument().addDocumentListener(new DocumentAdapter() {

			@Override
			public void update(DocumentEvent e)
			{
				String text = fieldWidth.getText();
				try {
					width = Integer.parseInt(text);
				} catch (NumberFormatException ex) {
					// TODO: set red border
				}
			}
		});

		fieldHeight.getDocument().addDocumentListener(new DocumentAdapter() {

			@Override
			public void update(DocumentEvent e)
			{
				String text = fieldHeight.getText();
				try {
					height = Integer.parseInt(text);
				} catch (NumberFormatException ex) {
					// TODO: set red border
				}
			}
		});

		return panel;
	}

	private void setButtonBorder(JComponent component)
	{
		component.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	private void setLabelBorder(JComponent component, boolean drop)
	{
		if (drop) {
			component.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createEmptyBorder(5, 5, 5, 5),
							BorderFactory
									.createBevelBorder(BevelBorder.LOWERED)),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		} else {
			component.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createEmptyBorder(5, 5, 5, 5),
							BorderFactory
									.createEtchedBorder(EtchedBorder.LOWERED)),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		}
	}

	public BBox getBoundingBox()
	{
		return boundingBox;
	}

	public int getImageWidth()
	{
		return width;
	}

	public int getImageHeight()
	{
		return width;
	}

}
