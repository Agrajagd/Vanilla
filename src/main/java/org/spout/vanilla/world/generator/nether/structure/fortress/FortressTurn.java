/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Vanilla is licensed under the Spout License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.vanilla.world.generator.nether.structure.fortress;

import java.util.List;

import com.google.common.collect.Lists;

import org.spout.math.imaginary.Quaternion;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.world.generator.structure.PieceCuboidBuilder;
import org.spout.vanilla.world.generator.structure.SimpleBlockMaterialPicker;
import org.spout.vanilla.world.generator.structure.Structure;
import org.spout.vanilla.world.generator.structure.StructurePiece;
import org.spout.vanilla.world.generator.structure.WeightedNextStructurePiece;

public class FortressTurn extends WeightedNextStructurePiece {
	private static final WeightedNextPieceCache DEFAULT_NEXT = new WeightedNextPieceCache().
			add(FortressBlazeBalcony.class, 1).
			add(FortressNetherWartStairs.class, 1).
			add(FortressStaircase.class, 3).
			add(FortressRoom.class, 3).
			add(FortressStairRoom.class, 4).
			add(FortressBalconyIntersection.class, 4).
			add(FortressGateIntersection.class, 4).
			add(FortressIntersection.class, 6).
			add(FortressCorridor.class, 10);
	private boolean left = false;

	public FortressTurn(Structure parent) {
		super(parent, DEFAULT_NEXT);
	}

	@Override
	public boolean canPlace() {
		return true;
	}

	@Override
	public void place() {
		// Building objects
		final PieceCuboidBuilder box = new PieceCuboidBuilder(this);
		final SimpleBlockMaterialPicker picker = new SimpleBlockMaterialPicker();
		box.setPicker(picker);
		// Floor
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK, VanillaMaterials.NETHER_BRICK);
		box.setMinMax(0, 0, 0, 4, 1, 4).fill();
		// Interior space
		picker.setOuterInnerMaterials(VanillaMaterials.AIR, VanillaMaterials.AIR);
		box.offsetMinMax(0, 2, 0, 0, 4, 0).fill();
		// First wall
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK, VanillaMaterials.NETHER_BRICK);
		if (left) {
			box.offsetMinMax(0, 0, 0, -4, 0, 0);
		} else {
			box.offsetMinMax(4, 0, 0, 0, 0, 0);
		}
		box.fill();
		// Windows for the first wall
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK_FENCE, VanillaMaterials.NETHER_BRICK_FENCE);
		if (left) {
			box.setMinMax(0, 3, 1, 0, 4, 1);
		} else {
			box.setMinMax(4, 3, 1, 4, 4, 1);
		}
		box.fill();
		box.offsetMinMax(0, 0, 2, 0, 0, 2).fill();
		// Second wall
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK, VanillaMaterials.NETHER_BRICK);
		if (left) {
			box.setMinMax(4, 2, 0, 4, 5, 0).fill();
			box.offsetMinMax(-3, 0, 4, 0, 0, 4).fill();
		} else {
			box.setMinMax(0, 2, 0, 0, 5, 0).fill();
			box.offsetMinMax(0, 0, 4, 3, 0, 4).fill();
		}
		// Windows for the second wall
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK_FENCE, VanillaMaterials.NETHER_BRICK_FENCE);
		box.setMinMax(1, 3, 4, 1, 4, 4).fill();
		box.offsetMinMax(2, 0, 0, 2, 0, 0).fill();
		// Roof
		picker.setOuterInnerMaterials(VanillaMaterials.NETHER_BRICK, VanillaMaterials.NETHER_BRICK);
		box.setMinMax(0, 6, 0, 4, 6, 4).fill();
		// Fill down to the ground
		for (int xx = 0; xx <= 4; xx++) {
			for (int zz = 0; zz <= 4; zz++) {
				fillDownwards(xx, -1, zz, 50, VanillaMaterials.NETHER_BRICK);
			}
		}
	}

	@Override
	public void randomize() {
		left = getRandom().nextBoolean();
	}

	@Override
	public List<StructurePiece> getNextPieces() {
		final StructurePiece piece = getNextPiece();
		if (left) {
			piece.setPosition(position.add(rotate(5, 0, 4)));
			piece.setRotation(Quaternion.fromAngleDegAxis(90, 0, 1, 0).mul(rotation));
		} else {
			piece.setPosition(position.add(rotate(-1, 0, 0)));
			piece.setRotation(Quaternion.fromAngleDegAxis(-90, 0, 1, 0).mul(rotation));
		}
		piece.randomize();
		return Lists.newArrayList(piece);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(transform(0, 0, 0), transform(4, 6, 4));
	}
}
