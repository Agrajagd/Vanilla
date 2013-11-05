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
package org.spout.vanilla.world.generator.structure;

import java.util.List;
import java.util.Random;

import org.spout.api.generator.WorldGeneratorObject;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;

import org.spout.math.imaginary.Quaternionf;
import org.spout.math.matrix.Matrix3f;
import org.spout.math.vector.Vector3f;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.material.block.Directional;
import org.spout.vanilla.material.block.DoorBlock;
import org.spout.vanilla.material.block.Liquid;
import org.spout.vanilla.material.block.attachable.Attachable;

public abstract class StructurePiece {
	protected final Structure parent;
	protected Point position = Point.invalid;
	protected Quaternionf rotation = Quaternionf.IDENTITY;
	protected Vector3f rotationPoint = Vector3f.ZERO;

	public StructurePiece(Structure parent) {
		this.parent = parent;
	}

	public Random getRandom() {
		return parent.getRandom();
	}

	public Structure getParent() {
		return parent;
	}

	public Block getBlock(int xx, int yy, int zz) {
		return position.getWorld().getBlock(transform(xx, yy, zz));
	}

	public BlockMaterial getBlockMaterial(int xx, int yy, int zz) {
		final Vector3f transformed = transform(xx, yy, zz);
		return position.getWorld().getBlockMaterial(transformed.getFloorX(), transformed.getFloorY(), transformed.getFloorZ());
	}

	public void setBlockMaterial(int xx, int yy, int zz, BlockMaterial material) {
		setBlockMaterial(xx, yy, zz, material, material.getData());
	}

	public void setBlockMaterial(int xx, int yy, int zz, BlockMaterial material, short data) {
		final Vector3f transformed = transform(xx, yy, zz);
		position.getWorld().setBlockMaterial(transformed.getFloorX(), transformed.getFloorY(), transformed.getFloorZ(),
				material, data, null);
		if (material instanceof Directional) {
			final Directional directional = (Directional) material;
			final Block block = position.getWorld().getBlock(transformed);
			final BlockFace face = directional.getFacing(block);
			if (face != BlockFace.BOTTOM && face != BlockFace.TOP) {
				directional.setFacing(block, BlockFace.fromYaw(face.getDirection().getAxesAngleDeg().getY()
						+ rotation.getAxesAngleDeg().getY()));
			}
		} else if (material instanceof Attachable) {
			final Attachable attachable = (Attachable) material;
			final Block block = position.getWorld().getBlock(transformed);
			final BlockFace face = attachable.getAttachedFace(block);
			if (face != BlockFace.BOTTOM && face != BlockFace.TOP) {
				attachable.setAttachedFace(block, BlockFace.fromYaw(face.getDirection().getAxesAngleDeg().getY()
						+ rotation.getAxesAngleDeg().getY()), null);
			}
		}
	}

	public void setBlockMaterial(float odd, int xx, int yy, int zz, BlockMaterial material) {
		setBlockMaterial(odd, xx, yy, zz, material, material.getData());
	}

	public void setBlockMaterial(float odd, int xx, int yy, int zz, BlockMaterial material, short data) {
		if (getRandom().nextFloat() > odd) {
			setBlockMaterial(xx, yy, zz, material, data);
		}
	}

	public void attachMaterial(float odd, int xx, int yy, int zz, Attachable attachable) {
		if (getRandom().nextFloat() > odd) {
			attachMaterial(xx, yy, zz, attachable);
		}
	}

	public void attachMaterial(int xx, int yy, int zz, Attachable attachable) {
		final Block block = getBlock(xx, yy, zz);
		for (BlockFace face : BlockFaces.BTNSWE) {
			final Block adjacent = block.translate(face);
			if (attachable.canAttachTo(adjacent, face.getOpposite())) {
				block.setMaterial((BlockMaterial) attachable);
				attachable.setAttachedFace(block, face, null);
			}
		}
	}

	public void fillDownwards(int xx, int yy, int zz, int limit, BlockMaterial material) {
		fillDownwards(xx, yy, zz, limit, material, material.getData());
	}

	public void fillDownwards(int xx, int yy, int zz, int limit, BlockMaterial material, short data) {
		short counter = 0;
		Block block;
		while (((block = getBlock(xx, yy, zz)).getMaterial().isMaterial(VanillaMaterials.AIR)
				|| block.getMaterial() instanceof Liquid) && counter++ < limit) {
			block.setMaterial(material, data);
			yy--;
		}
	}

	public void placeObject(int xx, int yy, int zz, WorldGeneratorObject object) {
		final Vector3f transformed = transform(xx, yy, zz);
		if (object.canPlaceObject(position.getWorld(), transformed.getFloorX(), transformed.getFloorY(), transformed.getFloorZ())) {
			object.placeObject(position.getWorld(), transformed.getFloorX(), transformed.getFloorY(), transformed.getFloorZ());
		}
	}

	public void placeDoor(int xx, int yy, int zz, DoorBlock door, BlockFace facing) {
		final Block bottom = getBlock(xx, yy, zz);
		door.create(getBlock(xx, yy, zz), bottom.translate(BlockFace.TOP),
				BlockFace.fromYaw(facing.getDirection().getAxesAngleDeg().getY() + rotation.getAxesAngleDeg().getY()), false, false);
	}

	protected Vector3f transform(int x, int y, int z) {
		return rotate(x, y, z).add(position).round();
	}

	protected Vector3f rotate(int x, int y, int z) {
		return Matrix3f.createRotation(rotation).transform(new Vector3f(x, y, z).sub(rotationPoint)).add(rotationPoint);
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void offsetPosition(int x, int y, int z) {
		position = position.add(x, y, z);
	}

	public void offsetPosition(Vector3f offset) {
		offsetPosition(offset.getFloorX(), offset.getFloorY(), offset.getFloorZ());
	}

	public Quaternionf getRotation() {
		return rotation;
	}

	public void setRotation(Quaternionf rotation) {
		this.rotation = rotation;
	}

	public Vector3f getRotationPoint() {
		return rotationPoint;
	}

	public void setRotationPoint(Vector3f rotationPoint) {
		this.rotationPoint = rotationPoint;
	}

	public abstract boolean canPlace();

	public abstract void place();

	public abstract void randomize();

	public abstract List<StructurePiece> getNextPieces();

	public abstract BoundingBox getBoundingBox();

	public static class BoundingBox {
		private final Vector3f min;
		private final Vector3f max;

		public BoundingBox(float xA, float yA, float zA,
						   float xB, float yB, float zB) {
			this(new Vector3f(xA, yA, zA), new Vector3f(xB, yB, zB));
		}

		public BoundingBox(Vector3f cornerA, Vector3f cornerB) {
			this.min = cornerA.min(cornerB);
			this.max = cornerA.max(cornerB);
		}

		public Vector3f getMax() {
			return max;
		}

		public Vector3f getMin() {
			return min;
		}

		public Vector3f getSize() {
			return max.sub(min);
		}

		public float getXSize() {
			return max.getX() - min.getX();
		}

		public float getYSize() {
			return max.getY() - min.getY();
		}

		public float getZSize() {
			return max.getZ() - min.getZ();
		}

		public boolean intersects(BoundingBox box) {
			final Vector3f rMax = box.getMax();
			if (rMax.getX() < min.getX()
					|| rMax.getY() < min.getY()
					|| rMax.getZ() < min.getZ()) {
				return false;
			}
			final Vector3f rMin = box.getMin();
			return !(rMin.getX() > max.getX()
					|| rMin.getY() > max.getY()
					|| rMin.getZ() > max.getZ());
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof BoundingBox)) {
				return false;
			}
			final BoundingBox other = (BoundingBox) o;
			return other.max.equals(max) && other.min.equals(min);
		}

		@Override
		public int hashCode() {
			int hash = 5;
			hash = 89 * hash + (min != null ? min.hashCode() : 0);
			hash = 89 * hash + (max != null ? max.hashCode() : 0);
			return hash;
		}
	}
}
