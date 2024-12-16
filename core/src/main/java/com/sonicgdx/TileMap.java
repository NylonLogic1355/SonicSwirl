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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.Collections;

public enum TileMap {

    INSTANCE;

    // solid blocks
    //TODO reconsider usage of TileMap class

    public static final Chunk[][] map = INSTANCE.testLevel;
    public static final int TILE_LENGTH = 16;
    public static final int CHUNK_LENGTH = 96;
    public static final int TILES_PER_CHUNK = CHUNK_LENGTH / TILE_LENGTH;

    // Classes are reference types so modifying a value would affect all the tiles that are the same.
    private final int[] zero = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private final int[] slope = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    private final int[] full = {16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16};
    private final int[] halfh = {8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8}; private final int[] halfw = {0,0,0,0,0,0,0,0,16,16,16,16,16,16,16,16};
    private final int[] rvSlope = {16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1};
    private final int[] tall1 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
    private final int[] testh = {0,0,1,2,2,3,4,5,5,6,6,7,8,9,9,9}, testw = {0,0,0,0,0,0,0,3,4,5,7,9,10,11,13,14};
    private final Tile EMPTY = new Tile();
    private final Tile ftile = new Tile(full,full,0,4,false,false);
    private final Tile stile = new Tile(slope, slope,45,1,false,false);
    private final Tile rvtile = new Tile(rvSlope, slope,-45,1,true,false);
    private final Tile htile = new Tile(halfh,halfw,0,1,false,false);
    private final Tile testtile = new Tile(testh,testw,33.75F,1,false,false);

    private final Chunk fChunk = new Chunk(new Texture(Gdx.files.internal("sprites/AIZ2/95.png")),Collections.nCopies(TILES_PER_CHUNK,Collections.nCopies(TILES_PER_CHUNK,ftile).toArray(new Tile[0])).toArray(new Tile[0][0]));

    public final Tile[][] emptyTileArray = Collections.nCopies(TILES_PER_CHUNK,Collections.nCopies(TILES_PER_CHUNK,EMPTY).toArray(new Tile[0])).toArray(new Tile[0][0]);

    private final Chunk hChunk = new Chunk(new Texture(Gdx.files.internal("sprites/AIZ2/176.png")),new Tile[][]{
            {ftile,ftile,htile},
            {ftile,ftile,htile},
            {ftile,ftile,htile},
            {ftile,ftile,htile},
            {ftile,ftile,htile},
            {ftile,ftile,htile}});
    private final Chunk rvChunk = new Chunk(new Texture(Gdx.files.internal("sprites/AIZ2/130.png")),new Tile[][]{
            {ftile,ftile,ftile,ftile,ftile,rvtile,},
            {ftile,ftile,ftile,ftile,rvtile,EMPTY},
            {ftile,ftile,ftile,rvtile,EMPTY,EMPTY},
            {ftile,ftile,rvtile,EMPTY,EMPTY,EMPTY},
            {ftile,rvtile,EMPTY,EMPTY,EMPTY,EMPTY},
            {rvtile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY}});

    private final Chunk sChunk =
        new Chunk(new Texture(Gdx.files.internal("sprites/AIZ2/65.png")),new Tile[][]{
                    {stile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
                    {ftile,stile,EMPTY,EMPTY,EMPTY,EMPTY},
                    {ftile,ftile,stile,EMPTY,EMPTY,EMPTY},
                    {ftile,ftile,ftile,stile,EMPTY,EMPTY},
                    {ftile,ftile,ftile,ftile,stile,EMPTY},
                    {ftile,ftile,ftile,ftile,ftile,stile}});


    private final Chunk emptyChunk = new Chunk(emptyTileArray);
    private final Chunk[][] testMap =
            {
                    {sChunk, emptyChunk, emptyChunk, emptyChunk},
                    {fChunk, emptyChunk, emptyChunk, emptyChunk},
                    {fChunk, emptyChunk, emptyChunk, emptyChunk},
                    {fChunk, emptyChunk, emptyChunk, emptyChunk},
                    {emptyChunk, emptyChunk, emptyChunk, emptyChunk},
                    {emptyChunk,fChunk, emptyChunk, emptyChunk},
                    {emptyChunk, emptyChunk, emptyChunk, emptyChunk},
                    {fChunk, emptyChunk, emptyChunk, emptyChunk},
                    {fChunk, emptyChunk, emptyChunk, emptyChunk},
                    {fChunk, emptyChunk, emptyChunk, emptyChunk},
                    {fChunk, emptyChunk, emptyChunk, emptyChunk},
                    {fChunk,fChunk, emptyChunk, emptyChunk},
                    {fChunk, emptyChunk, emptyChunk, emptyChunk},
                    {rvChunk, emptyChunk, emptyChunk, emptyChunk}

            };

    private final Chunk[][] testLevel =
        {
            {sChunk},
            {fChunk},
            {fChunk},
            {fChunk},
            {emptyChunk},
            {emptyChunk,fChunk},
            {emptyChunk},
            {fChunk},
            {fChunk},
            {fChunk},
            {fChunk},
            {fChunk,fChunk},
            {fChunk,fChunk},
            {emptyChunk,rvChunk, emptyChunk, emptyChunk, emptyChunk, fChunk},
            {fChunk, emptyChunk, emptyChunk, emptyChunk, emptyChunk, fChunk},
            {emptyChunk,sChunk, emptyChunk,hChunk,fChunk},
            {emptyChunk, emptyChunk, emptyChunk,hChunk},
            {emptyChunk, emptyChunk,rvChunk},
            {emptyChunk,rvChunk},
            {emptyChunk,sChunk},
            {emptyChunk, emptyChunk,sChunk},
            {emptyChunk},
            {emptyChunk},
            {emptyChunk},
            {emptyChunk},
            {hChunk}



        };

    public static Tile getTile(final int chunkX, final int chunkY, final int tileX, final int tileY) {
        if(0 <= tileX && 0 <= tileY && !isChunkEmpty(chunkX,chunkY)) {
            if (tileX < map[chunkX][chunkY].getTileArray().length && tileY < map[chunkX][chunkY].getTileArray()[tileX].length)
                return map[chunkX][chunkY].getTileArray()[tileX][tileY];
            else return INSTANCE.EMPTY;
        }
        else return INSTANCE.EMPTY;
        //OLD try catch version
        /*try {
            return map[chunkX][chunkY][tileX][tileY];
        }
        catch (ArrayIndexOutOfBoundsException e){
            //Gdx.app.error("getTile() Error",String.valueOf(e));
            //e.printStackTrace();
            return INSTANCE.EMPTY;
        }*/


    }

    /**
     * @param chunkX the index used to get a Chunk[] from the TileMap
     * @param chunkY the index used to get a Chunk from the array given by map[tileX]
     * @return the respective Chunk in the Chunk array
     * <p>
     * An empty chunk if the co-ordinates are out of range for the array.
     */
    public static Chunk getChunk(final int chunkX, final int chunkY) {
        if (chunkX < 0 || chunkX >= map.length || chunkY < 0 || chunkY >= map[chunkX].length) {
            return INSTANCE.emptyChunk;
        }

        return map[chunkX][chunkY];
    }

    public static boolean isChunkEmpty(final int chunkX, final int chunkY) {
        return getChunk(chunkX,chunkY).isEmpty();
    }

    public static Tile getEmptyTile()
    {
        return INSTANCE.EMPTY;
    }

    /*@Deprecated
    public int getHeight(int chunkX, int chunkY, int tileX, int tileY, int block)
    {
        if (map[chunkX][chunkY].getTileArray()[tileX][tileY].empty) return 0;
        else return map[chunkX][chunkY].getTileArray()[tileX][tileY].heightArray[block];
    }

    @Deprecated
    public int getWidth(int chunkX, int chunkY, int tileX, int tileY, int block)
    {
        if (map[chunkX][chunkY].getTileArray()[tileX][tileY].empty) return 0;
        else return map[chunkX][chunkY].getTileArray()[tileX][tileY].widthArray[block];
    }

    @Deprecated
    public Chunk[][] getMap() {
        return map;
    }

    @Deprecated
    public int getHeightAbove(int chunkX, int chunkY, int tileX, int tileY, int block)
    {
        if (tileY < 7) tileY = tileY + 1;
        else
        {
            chunkY +=1;
            tileY = 0;
        }

        if (map[chunkX][chunkY].getTileArray()[tileX][tileY].empty) return 0;
        else return map[chunkX][chunkY].getTileArray()[tileX][tileY].heightArray[block];
    }
    @Deprecated
    public int getHeightBelow(int chunkX, int chunkY, int tileX, int tileY, int block)
    {
        if (tileY == 0)
        {
            chunkY--;
            tileY = 7;
        }
        else tileY--;

        if (map[chunkX][chunkY].getTileArray()[tileX][tileY].empty) return 0;
        else return map[chunkX][chunkY].getTileArray()[tileX][tileY].heightArray[block];
    }

    @Deprecated
    public int[] getWidthArray(int chunkX, int chunkY, int tileX, int tileY)
    {
        if (map[chunkX][chunkY].getTileArray()[tileX][tileY].empty) return new int[16];
        else return map[chunkX][chunkY].getTileArray()[tileX][tileY].widthArray;
    }

    @Deprecated
    public int[] getHeightArray(int chunkX, int chunkY, int tileX, int tileY)
    {
        if (map[chunkX][chunkY].getTileArray()[tileX][tileY].empty) return new int[16];
        else return map[chunkX][chunkY].getTileArray()[tileX][tileY].heightArray;
    }
*/

}
