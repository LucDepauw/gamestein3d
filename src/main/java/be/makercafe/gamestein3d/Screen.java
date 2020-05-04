/*
 	Gamestein3D is a java based 3D raycast game engine
    Copyright (C) 2020  Luc De pauw

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package be.makercafe.gamestein3d;

import java.util.ArrayList;
import java.util.Arrays;

public class Screen {
	public int[][] map;
	public int mapWidth, mapHeight, width, height;
	public ArrayList<Texture> textures;
	private double cameraX;
	private double rayDirX;
	private double rayDirY;
	private int mapX;
	private int mapY;
	private double sideDistX;
	private double sideDistY;
	private double deltaDistX;
	private double deltaDistY;
	private double perpWallDist;
	private int stepX;
	private int stepY;
	private boolean hit;
	private int side;
	private int lineHeight;
	private int drawStart;
	private int drawEnd;
	private int texNum;
	private double wallX;
	private int texX;
	private int texY;
	private int color;

	private double rayDirX0;
	private double rayDirY0;
	private double rayDirX1;
	private double rayDirY1;
	private int position;
	private double posZ;
	private double rowDistance;
	private double floorStepX;
	private double floorStepY;
	private double floorX;
	private double floorY;
	private int cellX;
	private int cellY;
	private int texWidth;
	private int texHeight;
	private int floorTexture;
	private int ceilingTexture;

	// 1D Zbuffer
	private double ZBuffer[];

	// arrays used to sort the sprites
	private Sprite sprite[] = null;
	private double spriteX;
	private double spriteY;
	private double invDet;
	private double transformX;
	private double transformY;
	private int spriteScreenX;
	private int spriteHeight;
	private int drawStartY;
	private int drawEndY;
	private int spriteWidth;
	private int drawStartX;
	private int drawEndX;
	private int texX2;
	private int d;
	private int texY2;

	public Screen(int[][] m, int mapW, int mapH, ArrayList<Texture> tex, int w, int h, Sprite[] sprite) {
		map = m;
		mapWidth = mapW;
		mapHeight = mapH;
		textures = tex;
		width = w;
		height = h;
		texWidth = textures.get(0).SIZE;
		texHeight = texWidth;
		ZBuffer = new double[width];
		this.sprite = sprite;
	}

	public int[] update(Camera camera, int[] pixels) {
//		final int halfPixelLength = pixels.length/2;
//		for(int n=0; n<halfPixelLength; n++) {
//			if(pixels[n] != Color.DARK_GRAY.getRGB()) pixels[n] = Color.DARK_GRAY.getRGB();
//		}
//		for(int i=halfPixelLength; i<pixels.length; i++){
//			if(pixels[i] != Color.gray.getRGB()) pixels[i] = Color.gray.getRGB();
//		}

		// FLOOR CASTING
		for (int y = 0; y < height; y++) {
			rayDirX0 = camera.xDir - camera.xPlane;
			rayDirY0 = camera.yDir - camera.yPlane;
			rayDirX1 = camera.xDir + camera.xPlane;
			rayDirY1 = camera.yDir + camera.yPlane;

			position = y - height / 2;

			posZ = 0.5 * height;

			rowDistance = posZ / position;

			floorStepX = rowDistance * (rayDirX1 - rayDirX0) / width;
			floorStepY = rowDistance * (rayDirY1 - rayDirY0) / width;

			floorX = camera.xPos + rowDistance * rayDirX0;
			floorY = camera.yPos + rowDistance * rayDirY0;

			for (int x = 0; x < width; ++x) {
				cellX = (int) (floorX);
				cellY = (int) (floorY);

				// get the texture coordinate from the fractional part
				int tx = (int) (texWidth * (floorX - cellX)) & (texWidth - 1);
				int ty = (int) (texHeight * (floorY - cellY)) & (texHeight - 1);

				floorX += floorStepX;
				floorY += floorStepY;

				floorTexture = 3;
				ceilingTexture = 6;

				// floor
				color = (textures.get(floorTexture).pixels[texWidth * ty + tx] >> 1) & 8355711;// make a bit darker
				pixels[x + y * (width)] = color;

				// ceiling (symmetrical, at screenHeight - y - 1 instead of y)
				color = (textures.get(ceilingTexture).pixels[texWidth * ty + tx] >> 1) & 8355711;// make a bit darker
				pixels[x + ((height - y - 1) * (width))] = color;
			}
		}

		// WALL casting
		for (int x = 0; x < width; x = x + 1) {
			cameraX = 2 * x / (double) (width) - 1;
			rayDirX = camera.xDir + camera.xPlane * cameraX;
			rayDirY = camera.yDir + camera.yPlane * cameraX;
			mapX = (int) camera.xPos;
			mapY = (int) camera.yPos;
			deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
			deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
			hit = false;
			side = 0;
			// Figure out the step direction and initial distance to a side
			if (rayDirX < 0) {
				stepX = -1;
				sideDistX = (camera.xPos - mapX) * deltaDistX;
			} else {
				stepX = 1;
				sideDistX = (mapX + 1.0 - camera.xPos) * deltaDistX;
			}
			if (rayDirY < 0) {
				stepY = -1;
				sideDistY = (camera.yPos - mapY) * deltaDistY;
			} else {
				stepY = 1;
				sideDistY = (mapY + 1.0 - camera.yPos) * deltaDistY;
			}
			// Loop to find where the ray hits a wall
			while (!hit) {
				// Jump to next square
				if (sideDistX < sideDistY) {
					sideDistX += deltaDistX;
					mapX += stepX;
					side = 0;
				} else {
					sideDistY += deltaDistY;
					mapY += stepY;
					side = 1;
				}
				// Check if ray has hit a wall
				// System.out.println(mapX + ", " + mapY + ", " + map[mapX][mapY]);
				if (map[mapX][mapY] > 0)
					hit = true;
			}
			// Calculate distance to the point of impact
			if (side == 0)
				perpWallDist = Math.abs((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX);
			else
				perpWallDist = Math.abs((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY);
			if (perpWallDist > 0)
				lineHeight = Math.abs((int) (height / perpWallDist));
			else
				lineHeight = height;
			drawStart = -lineHeight / 2 + height / 2;
			if (drawStart < 0)
				drawStart = 0;
			drawEnd = lineHeight / 2 + height / 2;
			if (drawEnd >= height)
				drawEnd = height - 1;
			texNum = map[mapX][mapY] - 1;
			if (side == 1) {// If its a y-axis wall
				wallX = (camera.xPos + ((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY) * rayDirX);
			} else {// X-axis wall
				wallX = (camera.yPos + ((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX) * rayDirY);
			}
			wallX -= Math.floor(wallX);
			texX = (int) (wallX * (textures.get(texNum).SIZE));
			if (side == 0 && rayDirX > 0)
				texX = textures.get(texNum).SIZE - texX - 1;
			if (side == 1 && rayDirY < 0)
				texX = textures.get(texNum).SIZE - texX - 1;
			// calculate y coordinate on texture
			for (int y = drawStart; y < drawEnd; y++) {
				texY = (((y * 2 - height + lineHeight) << 6) / lineHeight) / 2;
				if (texX >= 0 && texY >= 0) {
					if (side == 0)
						color = textures.get(texNum).pixels[texX + (texY * textures.get(texNum).SIZE)];
					else
						color = (textures.get(texNum).pixels[texX + (texY * textures.get(texNum).SIZE)] >> 1) & 8355711;// Make
																														// y
																														// sides
																														// darker
					pixels[x + y * (width)] = color;
				}
			}
			ZBuffer[x] = perpWallDist;
		}

		// SPRITE CASTING
		// calculate distance for each sprite
		for (int i = 0; i < sprite.length; i++) {
			sprite[i].distance = ((camera.xPos - sprite[i].x) * (camera.xPos - sprite[i].x)
					+ (camera.yPos - sprite[i].y) * (camera.yPos - sprite[i].y)); // sqrt not taken, unneeded
		}
		// sort sprites from far to close
		sortSpritesOnDistance();

		// after sorting the sprites, do the projection and draw them
		for (int i = 0; i < sprite.length; i++) {
			spriteX = sprite[i].x - camera.xPos;
			spriteY = sprite[i].y - camera.yPos;

			// transform sprite with the inverse camera matrix
			// [ planeX dirX ] -1 [ dirY -dirX ]
			// [ ] = 1/(planeX*dirY-dirX*planeY) * [ ]
			// [ planeY dirY ] [ -planeY planeX ]

			invDet = 1.0 / (camera.xPlane * camera.yDir - camera.xDir * camera.yPlane);

			transformX = invDet * (camera.yDir * spriteX - camera.xDir * spriteY);
			transformY = invDet * (-camera.yPlane * spriteX + camera.xPlane * spriteY);

			spriteScreenX = (int) Math.floor((width / 2) * (1 + transformX / transformY));

			spriteHeight = (int) Math.abs((height / (transformY)));
			drawStartY = -spriteHeight / 2 + height / 2;
			if (drawStartY < 0)
				drawStartY = 0;
			drawEndY = spriteHeight / 2 + height / 2;
			if (drawEndY >= height)
				drawEndY = height - 1;

			spriteWidth = (int) Math.abs((height / (transformY)));
			drawStartX = -spriteWidth / 2 + spriteScreenX;
			if (drawStartX < 0)
				drawStartX = 0;
			drawEndX = spriteWidth / 2 + spriteScreenX;
			if (drawEndX >= width)
				drawEndX = width - 1;

			// loop through every vertical stripe of the sprite on screen
			for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
				texX2 = (int) Math
						.floor((256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * texWidth / spriteWidth) / 256);
				// the conditions in the if are:
				// 1) it's in front of camera plane so you don't see things behind you
				// 2) it's on the screen (left)
				// 3) it's on the screen (right)
				// 4) ZBuffer, with perpendicular distance
				if (transformY > 0 && stripe > 0 && stripe < width && transformY < ZBuffer[stripe] && texX2 >= 0) {
					for (int y = drawStartY; y < drawEndY; y++) // for every pixel of the current stripe
					{
						d = (y) * 256 - height * 128 + spriteHeight * 128;
						texY2 = ((d * texHeight) / spriteHeight) / 256;
						if (texY2 >= 0) {
							// Uint32 color = texture[sprite[spriteOrder[i]].texture][texWidth * texY +
							// texX]; //get current color from the texture
							try {
								color = textures.get(sprite[i].texture).pixels[texX2 + (texY2 * texWidth)];
							} catch (Exception ex) {
								ex.printStackTrace();
								color = 0;
							}
							if ((color & 0x00FFFFFF) != 0) {
								// buffer[y][stripe] = color; //paint pixel if it isn't black, black is the
								// invisible color
								pixels[stripe + y * (width)] = color;
							}
						}
					}
				}
			}
		}

		return pixels;
	}

	void sortSpritesOnDistance() {
		Arrays.sort(sprite, new SortByDescDistance());
	}
}
