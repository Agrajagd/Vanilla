/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, VanillaDev <http://www.spout.org/>
 * Vanilla is licensed under the SpoutDev License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.spout.api.math.MathHelper;
import org.spout.api.resource.ResourceNotFoundException;

public class MapColorPalette {
	private static final byte[] colorPalette = new byte[128 * 256 * 256];
	private static final String PALETTERESOURCE = "mapColorPalette.dat"; //TODO: Get correct path
	private static boolean initialized = false;

	public static void initialize() {
		if (initialized) {
			return;
		}
		boolean canLoad = false; // Resources are failing in Spout...
		if (!canLoad) {
			return;
		}
		try {
			InputStream stream = MapColorPalette.class.getResourceAsStream(PALETTERESOURCE);
			if (stream == null) {
				throw new ResourceNotFoundException("Failed to find Map Color Palette resource '" + PALETTERESOURCE + "'!");
			} else {
				stream = new GZIPInputStream(MapColorPalette.class.getResourceAsStream(PALETTERESOURCE));
				try {
					if (stream.read(colorPalette) != colorPalette.length) {
						throw new ResourceNotFoundException("Failed to read Map Color Palette resource '" + PALETTERESOURCE + "': invalid size!");
					}
				} finally {
					stream.close();
				}
			}
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Failed to read Map Color Palette resource '" + PALETTERESOURCE + "': " + ex.getMessage());
		} finally {
			initialized = true;
		}
	}

	/**
	 * Generates a new Palette file (only used to generate the included file)
	 * @param outputFile to write the palette to
	 */
	public static void generatePaletteFile(File outputFile) {
		// Generate new Color Palette
		byte[] colorPalette = new byte[128 * 256 * 256];
		int r, g, b;
		float rf, gf, bf;
		double diff, nearestDiff;
		MapColor nearest = null;
		for (r = 0; r < 256; r++) {
			rf = (float) r / 255f;
			for (g = 0; g < 256; g++) {
				gf = (float) g / 255f;
				for (b = 0; b < 256; b++) {
					bf = (float) b / 255f;

					// Obtain nearest map color
					nearestDiff = Double.MAX_VALUE;
					for (MapColor color : MapColor.values()) {
						if (color.getBase() == MapMaterialColor.TRANSPARENT) {
							continue;
						}
						diff = MathHelper.lengthSquared(color.getRed() - rf, color.getGreen() - gf, color.getBlue() - bf);
						if (diff < nearestDiff) {
							nearestDiff = diff;
							nearest = color;
							if (diff <= 0.02f) {
								break;
							}
						}
					}
					// Set in palette
					if ((b & 0x80) == 0x80) {
						colorPalette[getPaletteKey(r, g, b)] |= nearest.getId();
					} else {
						colorPalette[getPaletteKey(r, g, b)] |= nearest.getId() << 4;
					}
				}
			}
		}

		// Write to file
		try {
			GZIPOutputStream stream = new GZIPOutputStream(new FileOutputStream(outputFile));
			try {
				stream.write(colorPalette);
			} finally {
				stream.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static int getPaletteKey(int r, int g, int b) {
		return ((r & 0xFF) << 15) | ((g & 0xFF) << 7) | (b & 0x7F);
	}

	/**
	 * Gets the map color nearest to the RGB color specified
	 * @param r component of the color (0 - 255)
	 * @param g component of the color (0 - 255)
	 * @param b component of the color (0 - 255)
	 * @return the MapColor nearest to the color
	 */
	public static MapColor getColor(int r, int g, int b) {
		byte color = colorPalette[getPaletteKey(r, g, b)];
		if ((b & 0x80) == 0x80) {
			return MapColor.getById(color & 0x3F);
		} else {
			return MapColor.getById(color >> 4);
		}
	}

	/**
	 * Gets the map color nearest to the RGB color specified<br>
	 * Note: This is a slow function
	 * @param r component of the color (0F - 1F)
	 * @param g component of the color (0F - 1F)
	 * @param b component of the color (0F - 1F)
	 * @return the MapColor nearest to the color
	 */
	public static MapColor getColor(float r, float g, float b) {
		return getColor((int) (r * 255), (int) (g * 255), (int) (b * 255));
	}
}