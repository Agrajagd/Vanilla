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
package org.spout.vanilla.material.block.attachable;

import org.spout.api.event.Cause;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.block.BlockFace;

import org.spout.physics.collision.shape.CollisionShape;

import org.spout.vanilla.material.VanillaBlockMaterial;

public class GroundAttachable extends AbstractAttachable {
	public GroundAttachable(short dataMask, String name, int id, String model, CollisionShape shape) {
		super(dataMask, name, id, model, shape);
		this.setAttachable(BlockFace.BOTTOM);
	}

	public GroundAttachable(String name, int id, String model, CollisionShape shape) {
		super(name, id, model, shape);
		this.setAttachable(BlockFace.BOTTOM);
	}

	public GroundAttachable(String name, int id, int data, VanillaBlockMaterial parent, String model, CollisionShape shape) {
		super(name, id, data, parent, model, shape);
		this.setAttachable(BlockFace.BOTTOM);
	}

	@Override
	public boolean canSeekAttachedAlternative() {
		return true;
	}

	@Override
	public void setAttachedFace(Block block, BlockFace attachedFace, Cause<?> cause) {
	}

	@Override
	public BlockFace getAttachedFace(short data) {
		return BlockFace.BOTTOM;
	}
}
