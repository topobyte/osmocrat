// Copyright 2017 Sebastian Kuerten
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

package de.topobyte.osmocrat.list;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

public abstract class BaseCellRenderer<T> extends JLabel
		implements ListCellRenderer<T>
{

	private static final long serialVersionUID = -6782327786582569692L;

	public BaseCellRenderer()
	{
		setOpaque(true);
	}

	protected String formatTags(OsmEntity entity)
	{
		List<? extends OsmTag> tags = OsmModelUtil.getTagsAsList(entity);
		Function<OsmTag, String> converter = new Function<OsmTag, String>() {

			@Override
			public String apply(OsmTag tag)
			{
				return tag.getKey() + "=" + tag.getValue();
			}
		};
		return Joiner.on(", ").join(Lists.transform(tags, converter));
	}

	protected void setBackground(JList<?> list, boolean isSelected)
	{
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
	}

}
