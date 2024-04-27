/*
 * Copyright 2023 SonicGDX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sonicgdx;

import static com.sonicgdx.TileMap.TILE_LENGTH;

/**
 * Taking how classes are not value-types into account, tiles are implemented as effectively immutable.
 * While fields are not explicitly final, any methods that mutate them should be considered unsafe.
 */
public class Tile {

    boolean flippedHorizontally, flippedVertically, empty;
    int[] heightArray, widthArray;
    int solidity;
    // 0 = solid from top, 1 = solid from bottom, 2 = solid from left, 3 = solid from right, 4 = solid from all sides
    float angle;
    Tile(int[] heightArray, int[] widthArray, float angle, int solidity, boolean flippedHorizontally, boolean flippedVertically) {
        this.empty = false;

        if (heightArray.length == TILE_LENGTH) this.heightArray = heightArray;
        else throw new IllegalArgumentException("heightArray Length is " +  heightArray.length + " instead of " + TILE_LENGTH);
        if (widthArray.length == TILE_LENGTH) this.widthArray = widthArray;
        else throw new IllegalArgumentException("heightArray Length is " +  widthArray.length + " instead of " + TILE_LENGTH);

        this.angle = angle;
        this.flippedHorizontally = flippedHorizontally;
        this.flippedVertically = flippedVertically;
        this.solidity = solidity;
    }
    Tile() {
        this.empty = true;
        this.heightArray = null;
        this.widthArray = null;
        this.angle = 0;
    }

    public int getHeight(int block) {
        if (empty || block < 0 || TILE_LENGTH - 1 < block) return 0;
        else {
            return heightArray[block];
        }

    }
    public int getWidth(int block) {
        if (empty || block < 0 || TILE_LENGTH - 1 < block ) return 0;
        else {
            return widthArray[block];
        }
    }

    public boolean isEmpty() {
        return empty;
    }

}
