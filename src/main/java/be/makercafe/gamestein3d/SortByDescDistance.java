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

import java.util.Comparator;

public class SortByDescDistance implements Comparator<Sprite> 
{

	//Sort on descending distance
	public int compare(Sprite spriteA, Sprite spriteB) {
		return (int) Math.round((spriteB.distance - spriteA.distance) * 100);
	}  
	
}
