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

package de.topobyte.osmocrat.text;

import java.util.List;

import com.infomatiq.jsi.Rectangle;

import de.topobyte.jsi.GenericRTree;

/**
 * An implementation of the {@link TextIntersectionChecker} interface based on
 * an rtree for efficient retrieval of objects and JTS for primitive
 * intersection testing.
 */
public class TextIntersectionCheckerTree implements TextIntersectionChecker
{

	private GenericRTree<float[]> regions = new GenericRTree<>(2, 8);

	@Override
	public void add(float[][] boxes)
	{
		// add each box to the tree
		for (float[] box : boxes) {
			Rectangle rect = GeneralRectangle.getBoundingBox(box);
			regions.add(rect, box);
		}
	}

	@Override
	public boolean isValid(float[][] boxes)
	{
		// check each box separately
		for (float[] box : boxes) {
			Rectangle rect = GeneralRectangle.getBoundingBox(box);
			List<float[]> candidates = regions.intersectionsAsList(rect);
			// test against each candidate
			for (float[] region : candidates) {
				try {
					if (GeneralRectangle.intersects(box, region)) {
						return false;
					}
				} catch (Exception e) {
					return false;
				}
			}
		}
		return true;
	}

}