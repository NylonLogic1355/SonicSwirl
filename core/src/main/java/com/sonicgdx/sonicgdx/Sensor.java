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

package com.sonicgdx.sonicgdx;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import static com.sonicgdx.sonicgdx.TileMap.CHUNK_LENGTH;
import static com.sonicgdx.sonicgdx.TileMap.TILE_LENGTH;
import static com.sonicgdx.sonicgdx.TileMap.TILES_PER_CHUNK;

public class Sensor {
    private boolean isActive;
    private Vector2 position;
    private Tile tile;
    private float distance;

    public Sensor(float xPos, float yPos) {
        position = new Vector2(xPos,yPos);
    }
    public Sensor(Vector2 positionVector) {
        this.position = positionVector;
    }

    /**
     * Creates an empty sensor (with the assumption that its position will be updated later)
     */
    public Sensor() {
        //Default position is (0,0)
        this.position = new Vector2();
    }

    /**Attempts to find the nearest top of the surface relative to the sensor's position.
     * If no surface is found, the method will check one tile downwards for a non-empty height (and therefore a non-empty Tile).
     * Conversely, if a tile that is full in that position (has a height of 16) is found, the method will check one tile upwards for a possible top of the surface.
     * The tile attribute is set to the tile type of the nearest floor that has been located
     * The distance attribute is set to the distance on the y-axis between the sensor and that Tile.
     */
    public void floorProcess() {
        //TODO take into account solidity value

        /*  Out of bounds values would cause an error if attempting to check their distance
            - the arrays do not have negative indices. Therefore, if the player's x position
            or y position is negative values are set so that collision will never happen.
        */
        //TODO possibly perform check inside player function instead
        if (position.x < 0 || position.y < 0) {
            tile = TileMap.getEmptyTile(); distance = -50;
            return;
        }
        //TODO prevent catch block in getTile() from being used.

        int tileX = Math.floorMod(MathUtils.round(position.x), CHUNK_LENGTH) / TILE_LENGTH;
        int chunkX = MathUtils.round(position.x) / CHUNK_LENGTH;

        int tileY = Math.floorMod(MathUtils.round(position.y), CHUNK_LENGTH) / TILE_LENGTH;
        int chunkY = MathUtils.round(position.y) / CHUNK_LENGTH;

        int block = Math.floorMod(MathUtils.round(position.x), TILE_LENGTH); //Different behaviour for negative numbers compared to using %. For
        // example, -129 % 16 would return -1 which would cause an ArrayIndexOutOfBoundsException. Math.floorMod() would return a positive index in these cases.

        // An alternate expression to calculate block: ((chunkX * CHUNK_LENGTH) + (tileX * TILE_LENGTH) - position.x));

        int height = TileMap.getTile(chunkX,chunkY,tileX,tileY).getHeight(block);

        float checkDistance = ((chunkY * CHUNK_LENGTH) + (tileY * TILE_LENGTH) + height) - position.y;

        if (height == TILE_LENGTH)
        {
            int tempTileY, tempChunkY;
            // sensor regression, checks one tile above with downwards facing sensors in an attempt to find surface if the height of the array is full
            if (tileY < TILES_PER_CHUNK - 1)
            {
                tempChunkY = chunkY;
                tempTileY = tileY + 1;
            }
            else
            {
                tempChunkY = chunkY + 1;
                tempTileY = 0;
            }

            height = TileMap.getTile(chunkX,tempChunkY,tileX,tempTileY).getHeight(block);
            if (height > 0) //TODO outline conditions in comment
            {
                chunkY = tempChunkY;
                tileY = tempTileY;

                checkDistance += height;
            }
        }

        else if (height == 0)
        {
            // sensor extension, checks one tile below with downwards facing sensors in an attempt to find surface
            if (tileY == 0)
            {
                chunkY--;
                tileY = TILES_PER_CHUNK - 1;
            }
            else tileY--;

            height = TileMap.getTile(chunkX,chunkY,tileX,tileY).getHeight(block);

            if (height == 0) checkDistance -= TILE_LENGTH;
            else checkDistance -= (TILE_LENGTH -height);
        }

        tile = TileMap.getTile(chunkX,chunkY,tileX,tileY); distance = checkDistance;
    }

    /**
     * Generates the distance value based on how far away the sensor is from the nearest wall on the x-axis
     */
    public void wallProcess() {
        //TODO take into account solidity value

        /*
            Out of bounds values would cause an error if attempting to check their distance
            - the arrays do not have negative indices. Therefore, if the player's x position
            or y position is negative values are set so that collision will never happen.
        */
        //TODO possibly perform check inside player function instead
        if (position.x < 0 || position.y < 0) {
            //positive distances aren't accepted and 50 is too far out of the range
            //so this will never trigger a collision if out of bounds
            tile = TileMap.getEmptyTile(); distance = 50;
            return;
        }
        //TODO prevent catch block in getTile() from being used.

        int tileX = Math.floorMod(MathUtils.round(position.x), CHUNK_LENGTH) / TILE_LENGTH;
        int chunkX = MathUtils.round(position.x) / CHUNK_LENGTH;

        int tileY = Math.floorMod(MathUtils.round(position.y), CHUNK_LENGTH) / TILE_LENGTH;
        int chunkY = MathUtils.round(position.y) / CHUNK_LENGTH;

        int block = Math.floorMod(MathUtils.round(position.y), TILE_LENGTH); //Different behaviour for negative numbers compared to using %. For
        // example, -129 % 16 would return -1 which would cause an ArrayIndexOutOfBoundsException. Math.floorMod() would return a positive index in these cases.

        int width = TileMap.getTile(chunkX,chunkY,tileX,tileY).getWidth(block);

        //TODO change process if tile is flipped horizontally
        float checkDistance = ((chunkX * CHUNK_LENGTH) + ((tileX + 1) * TILE_LENGTH) - width) - position.x;

        if (width == TILE_LENGTH) {
            int tempTileX, tempChunkX;
            // sensor regression, checks one tile above with downwards facing sensors in an attempt to find surface if the height of the array is full

            //for right facing tiles
            if (tileX > 0)
            {
                tempChunkX = chunkX;
                tempTileX = tileX - 1;
            }
            else
            {
                tempChunkX = chunkX - 1;
                tempTileX = TILES_PER_CHUNK - 1;
            }

            width = TileMap.getTile(tempChunkX,chunkY,tempTileX,tileY).getHeight(block);
            if (width > 0)
            {
                chunkX = tempChunkX;
                tileX = tempTileX;

                checkDistance -= width;
            }
        }

        else if (width == 0) {
            // sensor extension, checks one tile below with downwards facing sensors in an attempt to find surface

            //for right facing tiles
            if (tileX == TILES_PER_CHUNK - 1)
            {
                chunkX++;
                tileY = 0;
            }
            else tileX++;

            width = TileMap.getTile(chunkX,chunkY,tileX,tileY).getHeight(block);

            if (width == 0) checkDistance += TILE_LENGTH;
            else checkDistance += (TILE_LENGTH -width);
        }

        tile = TileMap.getTile(chunkX,chunkY,tileX,tileY); distance = checkDistance;
        //Gdx.app.debug("distance",String.valueOf(distance));
    }

    public void setPositionValues(float x, float y) {
        position.set(x,y);
    }
    public void setPositionVector(Vector2 positionVector) {
        this.position = positionVector;
    }
    public float getXPosition() {
        return position.x;
    }
    public float getYPosition() {
        return position.y;
    }
    public Vector2 getPositionVector() {
        return position;
    }
    public float getDistance() {
        return distance;
    }
    public Tile getTile() {
        return tile;
    }
    public boolean getActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

}
