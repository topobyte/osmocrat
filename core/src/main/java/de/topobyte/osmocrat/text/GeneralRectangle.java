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

import com.infomatiq.jsi.Rectangle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Rectangles in general orientation.
 * 
 * A rectangle is stored in an float-array of size 8. The array contains the
 * coordinates in the following order: [x1, y1, x2, y2, x3, y3, x4, y4]
 * 
 * This class provides utility methods for manipulating such rectangles.
 */
public class GeneralRectangle
{

	public static Rectangle getBoundingBox(float[] box)
	{
		float x = box[0];
		float y = box[1];
		float minX = x, maxX = x, minY = y, maxY = y;
		for (int i = 2; i < 8; i += 2) {
			x = box[i];
			y = box[i + 1];
			if (x < minX)
				minX = x;
			if (x > maxX)
				maxX = x;
			if (y < minY)
				minY = y;
			if (y > maxY)
				maxY = y;
		}
		return new Rectangle(minX, minY, maxX, maxY);
	}

	public static boolean intersects(float[] box1, float[] box2)
	{
		Polygon polygon1 = createPolygon(box1);
		Polygon polygon2 = createPolygon(box2);
		return polygon1.intersects(polygon2);
	}

	public static Polygon createPolygon(float[] box)
	{
		GeometryFactory factory = new GeometryFactory();
		Coordinate[] coordinates = new Coordinate[5];
		for (int i = 0; i < 4; i++) {
			coordinates[i] = new Coordinate(box[i * 2], box[i * 2 + 1]);
		}
		coordinates[4] = new Coordinate(coordinates[0]);
		LinearRing shell = factory.createLinearRing(coordinates);
		return factory.createPolygon(shell, null);
	}

}
