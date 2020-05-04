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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import kuusisto.tinysound.Sound;

public class Camera implements KeyListener {
	public double xPos, yPos, xDir, yDir, xPlane, yPlane;
	public boolean left, right, forward, back;
	public final double MOVE_SPEED = .08;
	public final double ROTATION_SPEED = .045;
	private ArrayList<Sound> sounds = null;

	public Camera(double x, double y, double xd, double yd, double xp, double yp, ArrayList<Sound> sounds) {
		xPos = x;
		yPos = y;
		xDir = xd;
		yDir = yd;
		xPlane = xp;
		yPlane = yp;
		this.sounds = sounds;
	}

	public void keyPressed(KeyEvent key) {
		if ((key.getKeyCode() == KeyEvent.VK_LEFT))
			left = true;
		if ((key.getKeyCode() == KeyEvent.VK_RIGHT))
			right = true;
		if ((key.getKeyCode() == KeyEvent.VK_UP))
			forward = true;
		if ((key.getKeyCode() == KeyEvent.VK_DOWN))
			back = true;
	}

	public void keyReleased(KeyEvent key) {
		if ((key.getKeyCode() == KeyEvent.VK_LEFT))
			left = false;
		if ((key.getKeyCode() == KeyEvent.VK_RIGHT))
			right = false;
		if ((key.getKeyCode() == KeyEvent.VK_UP))
			forward = false;
		if ((key.getKeyCode() == KeyEvent.VK_DOWN))
			back = false;
	}

	public void update(int[][] map, ControllerManager controllers) {

		//Read the first connected gamepad
		try {
			if(controllers != null) {
				ControllerState currState = controllers.getState(0);
				if(currState.isConnected) {
					left = false;
					right = false;
					forward = false;
					back = false;
					if(currState.dpadLeft) {
						left = true;
					}
					if(currState.dpadRight) {
						right = true;
					}
					if(currState.dpadUp) {
						forward = true;
					}
					if(currState.dpadDown) {
						back = true;
					}
					if(currState.a) {
						sounds.get(0).play();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (forward) {
			if (map[(int) (xPos + xDir * MOVE_SPEED)][(int) yPos] == 0) {
				xPos += xDir * MOVE_SPEED;
			}
			if (map[(int) xPos][(int) (yPos + yDir * MOVE_SPEED)] == 0)
				yPos += yDir * MOVE_SPEED;
		}
		if (back) {
			if (map[(int) (xPos - xDir * MOVE_SPEED)][(int) yPos] == 0)
				xPos -= xDir * MOVE_SPEED;
			if (map[(int) xPos][(int) (yPos - yDir * MOVE_SPEED)] == 0)
				yPos -= yDir * MOVE_SPEED;
		}
		if (right) {
			double oldxDir = xDir;
			xDir = xDir * Math.cos(-ROTATION_SPEED) - yDir * Math.sin(-ROTATION_SPEED);
			yDir = oldxDir * Math.sin(-ROTATION_SPEED) + yDir * Math.cos(-ROTATION_SPEED);
			double oldxPlane = xPlane;
			xPlane = xPlane * Math.cos(-ROTATION_SPEED) - yPlane * Math.sin(-ROTATION_SPEED);
			yPlane = oldxPlane * Math.sin(-ROTATION_SPEED) + yPlane * Math.cos(-ROTATION_SPEED);
		}
		if (left) {
			double oldxDir = xDir;
			xDir = xDir * Math.cos(ROTATION_SPEED) - yDir * Math.sin(ROTATION_SPEED);
			yDir = oldxDir * Math.sin(ROTATION_SPEED) + yDir * Math.cos(ROTATION_SPEED);
			double oldxPlane = xPlane;
			xPlane = xPlane * Math.cos(ROTATION_SPEED) - yPlane * Math.sin(ROTATION_SPEED);
			yPlane = oldxPlane * Math.sin(ROTATION_SPEED) + yPlane * Math.cos(ROTATION_SPEED);
		}
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}