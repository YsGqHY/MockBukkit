package org.mockbukkit.mockbukkit.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CoordinateTest
{

	private Coordinate coordinate;

	@BeforeEach
	void setUp()
	{
		coordinate = new Coordinate();
	}

	@Test
	void constructor_NoParameters_AllZeroes()
	{
		coordinate = new Coordinate();
		assertEquals(0, coordinate.x);
		assertEquals(0, coordinate.y);
		assertEquals(0, coordinate.z);
	}

	@Test
	void construct_Parameters_ValuesSet()
	{
		coordinate = new Coordinate(1, 2, 3);
		assertEquals(1, coordinate.x);
		assertEquals(2, coordinate.y);
		assertEquals(3, coordinate.z);
	}

	@Test
	void hashCode_SameObject_SameHash()
	{
		assertEquals(coordinate.hashCode(), coordinate.hashCode());
	}

	@Test
	void hashCode_DifferentObjectWithSameCoordinates_SameHash()
	{
		Coordinate c1 = new Coordinate(1, 2, 3);
		Coordinate c2 = new Coordinate(1, 2, 3);
		assertEquals(c1.hashCode(), c2.hashCode());
	}

	@Test
	void hashCode_DifferentObjectWithDifferentCoordinates_DifferentHash()
	{
		Coordinate c1 = new Coordinate(1, 2, 3);
		Coordinate c2 = new Coordinate(4, 5, 6);
		assertNotEquals(c1.hashCode(), c2.hashCode());
	}

	@Test
	void equals_Null_False()
	{
		assertNotEquals(null, coordinate);
	}

	@Test
	void equals_Same_True()
	{
		assertEquals(coordinate, coordinate);
	}

	@Test
	void equals_DifferentObjectWithSameCoordinates_True()
	{
		Coordinate c1 = new Coordinate(1, 2, 3);
		Coordinate c2 = new Coordinate(1, 2, 3);
		assertEquals(c1, c2);
	}

	@Test
	void equals_DifferentObjectWithDifferentCoordinates_False()
	{
		Coordinate c1 = new Coordinate(1, 2, 3);
		Coordinate c2 = new Coordinate(4, 5, 6);
		assertNotEquals(c1, c2);
	}

	@Test
	void toChunkCoordinate()
	{
		Coordinate coordinate = new Coordinate(83, -15, -150);
		ChunkCoordinate chunk = coordinate.toChunkCoordinate();
		assertEquals(5, chunk.x);
		assertEquals(-10, chunk.z);
	}

	@Test
	void toLocalCoordinate()
	{
		Coordinate coordinate = new Coordinate(83, -15, -150);
		Coordinate local = coordinate.toLocalCoordinate();
		assertEquals(new Coordinate(3, -15, 10), local);
	}

}
