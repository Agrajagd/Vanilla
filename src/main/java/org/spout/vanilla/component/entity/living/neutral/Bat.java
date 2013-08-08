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
package org.spout.vanilla.component.entity.living.neutral;

import org.spout.api.util.Parameter;

import org.spout.vanilla.component.entity.living.Living;
import org.spout.vanilla.component.entity.living.Neutral;
import org.spout.vanilla.component.entity.misc.Health;
import org.spout.vanilla.data.VanillaData;
import org.spout.vanilla.protocol.entity.creature.BatEntityProtocol;

/**
 * A component that identifies the entity as a Bat.
 */
public class Bat extends Living implements Neutral {
	@Override
	public void onAttached() {
		super.onAttached();
		setEntityProtocol(new BatEntityProtocol());
		if (getAttachedCount() == 1) {
			getOwner().add(Health.class).setSpawnHealth(6);
		}
	}

	public boolean isHanging() {
		return getData().get(VanillaData.HANGING);
	}

	public void setHanging(boolean hanging) {
		getData().put(VanillaData.HANGING, hanging);
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte) (hanging ? 1 : 0)));
	}
}
