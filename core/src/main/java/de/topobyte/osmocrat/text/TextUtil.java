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

import com.vividsolutions.jts.geom.LineString;

public class TextUtil
{

	/**
	 * Create an array of boxes along the specified line. The creation of boxes
	 * will start at {@code hOffset} along the string, and generate boxes for
	 * the length specified with the {@code textLength} parameter. The method
	 * will return an array of double representing the boxes created. Let
	 * {@code boxes} be that array, then {@code boxes[i]} will contain the i'th
	 * box created.
	 * 
	 * @param string
	 *            the string to create boxes along
	 * @param hOffset
	 *            the amount to skip on the way before starting box creation.
	 * @param textLength
	 *            the length for which to create boxes.
	 * @param textHeight
	 *            the height of the boxes.
	 * @param isReverseX
	 *            an out parameter telling whether the area the boxes have been
	 *            created for is reverse in respect to the x coordinate, i.e.
	 *            the first coordinate is on the right of the last coordinate.
	 * @return an array of double describing boxes.
	 */
	public static float[][] createTextBoxes(LineString string, double hOffset,
			double textLength, double textHeight, BoolResult isReverseX)
	{
		// general stuff
		int num = string.getNumPoints();
		double h = textHeight / 2;

		// place to store previous coordinate
		double lastX = string.getCoordinateN(0).x;
		double lastY = string.getCoordinateN(0).y;

		int i = 1; // loop variable for all loops
		double stillToSkip = hOffset; // remaining skip amount

		// ********************************************************************
		// first skip through segments until hOffset is 0
		// ********************************************************************
		if (hOffset > 0) {
			for (; i < num; i++) {
				double thisX = string.getCoordinateN(i).x;
				double thisY = string.getCoordinateN(i).y;
				double dx = thisX - lastX;
				double dy = thisY - lastY;
				double lengthSegment = Math.sqrt(dx * dx + dy * dy);
				if (lengthSegment < stillToSkip) {
					// easy case, still enough to skip, continue loop
					stillToSkip -= lengthSegment;
					lastX = thisX;
					lastY = thisY;
					continue;
				}
				// we have to stop somewhere in between
				double skip = stillToSkip / lengthSegment;
				lastX = lastX + skip * dx;
				lastY = lastY + skip * dy;
				break;
			}
		}

		// keep reference to first x coordinate
		double firstX = lastX;

		// variables for further iteration
		float[][] boxes = new float[num - i][];
		int k = 0;
		double stillToTake = textLength;

		// ********************************************************************
		// then iterate through the segments
		// ********************************************************************
		for (; i < num; i++) {
			double thisX = string.getCoordinateN(i).x;
			double thisY = string.getCoordinateN(i).y;

			double dx = thisX - lastX;
			double dy = thisY - lastY;
			double lengthSegment = Math.sqrt(dx * dx + dy * dy);

			// test whether we need only part of the current segment
			boolean end = false;
			if (lengthSegment > stillToTake) {
				end = true;
				// do not take the whole segment
				double take = stillToTake / lengthSegment;
				// redefine current point
				thisX = lastX + take * dx;
				thisY = lastY + take * dy;
				// redefine dx, dy, lengthSegment accordingly
				dx = thisX - lastX;
				dy = thisY - lastY;
				lengthSegment = stillToTake;
			}

			// calculate rectangle
			double lambda = h / lengthSegment;
			double ox = -dy * lambda;
			double oy = dx * lambda;

			double r1x = lastX + ox;
			double r1y = lastY + oy;
			double r2x = lastX - ox;
			double r2y = lastY - oy;
			double r3x = thisX - ox;
			double r3y = thisY - oy;
			double r4x = thisX + ox;
			double r4y = thisY + oy;

			lastX = thisX;
			lastY = thisY;
			stillToTake -= lengthSegment;

			float[] box = new float[] { //
					(float) r1x, (float) r1y, //
					(float) r2x, (float) r2y, //
					(float) r3x, (float) r3y, //
					(float) r4x, (float) r4y //
			};
			boxes[k++] = box;

			if (end || stillToTake <= 0) {
				break;
			}
		}

		// we may have used less segments in the end, so cut the array if
		// necessary
		if (k < boxes.length) {
			float[][] shorter = new float[k][];
			System.arraycopy(boxes, 0, shorter, 0, k);
			boxes = shorter;
		}

		// check whether this is reverse
		isReverseX.value = lastX < firstX;

		return boxes;
	}

}
