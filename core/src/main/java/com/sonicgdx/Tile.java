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

import java.util.Arrays;
import java.util.Objects;

import static com.sonicgdx.TileMap.TILE_LENGTH;

/**
 * An almost-immutable data class which contains the collision data of a tile.
 * The arrays are not, but they are not exposed (only values of elements of it are)
 */
public class Tile {

    private final int[] heightArray, widthArray;
    private final float angle;
    private final boolean flippedHorizontally, flippedVertically;
    /**
     * 0 = solid from top, 1 = solid from bottom, 2 = solid from left, 3 = solid from right, 4 = solid from all sides
     */
    private final int solidity;

    private final boolean empty;

    Tile(final int[] heightArray,
         final int[] widthArray,
         final float angle,
         final int solidity,
         final boolean flippedHorizontally,
         final boolean flippedVertically) {

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
        //initialize arrays to simulate "empty" tile with no collision
        this.heightArray = new int[TILE_LENGTH];
        Arrays.fill(heightArray, 0);
        this.widthArray = new int[TILE_LENGTH];
        Arrays.fill(widthArray, 0);
        this.flippedHorizontally = false;
        this.flippedVertically = false;
        this.angle = 0;
        this.solidity = 0;
    }

    public int getHeight(final int block) {
        if (empty || block < 0 || TILE_LENGTH - 1 < block) return 0;
        else {
            return heightArray[block];
        }

    }
    public int getWidth(final int block) {
        if (empty || block < 0 || TILE_LENGTH - 1 < block ) return 0;
        else {
            return widthArray[block];
        }
    }

    public int getSolidity() {
        return solidity;
    }

    public float getAngle() {
        return angle;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isFlippedHorizontally() {
        return flippedHorizontally;
    }

    public boolean isFlippedVertically() {
        return flippedVertically;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        //note that an object of this class can NOT be equal to an object of a subclass
        if (o == null || getClass() != o.getClass()) return false;
        final Tile t = (Tile) o;
        return Float.compare(angle, t.angle) == 0
            && flippedHorizontally == t.flippedHorizontally
            && flippedVertically == t.flippedVertically
            && solidity == t.solidity
            && empty == t.empty
            && Objects.deepEquals(heightArray, t.heightArray)
            && Objects.deepEquals(widthArray, t.widthArray);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            Arrays.hashCode(heightArray),
            Arrays.hashCode(widthArray),
            angle,
            flippedHorizontally,
            flippedVertically,
            solidity,
            empty
        );
    }
}
