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

/**
 * An interface defining the methods necessary to implement a intersection based
 * rendering of text.
 */
public interface TextIntersectionChecker
{

	/**
	 * Add the specified boxes to the set of boxes maintained as occupied by
	 * text.
	 * 
	 * @param boxes
	 *            the boxes to mark as occupied.
	 */
	public void add(float[][] boxes);

	/**
	 * Test whether is would be valid to paint something that covers the
	 * specified boxes.
	 * 
	 * @param boxes
	 *            the boxes an object would occupy.
	 * @return whether the checker would allow to paint this.
	 */
	public boolean isValid(float[][] boxes);

}
