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

package com.sonicgdx.sonicswirl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.Collections;

public enum TileMap {

    TILE_MAP;

    // solid blocks
    //TODO tile ID
    //TODO add tile textureregion
    //TODO reconsider usage of TileMap class
    //TODO possible GUI chunk editor

    public static final Chunk[][] map = TILE_MAP.testMap;

    private final int TILES_PER_CHUNK = GameScreen.CHUNK_LENGTH / GameScreen.TILE_LENGTH;

    //TODO test class - check if all these are 16 in length

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

    private final Chunk hChunk = new Chunk(new Texture(Gdx.files.internal("sprites/AIZ2/95.png")),new Tile[][]{
            {ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,htile}});
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


    private final Chunk eChunk = new Chunk(emptyTileArray);
    private final Chunk[][] testMap =
            {
                    {sChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk},
                    {eChunk,hChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,fChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {rvChunk,eChunk,eChunk,eChunk}

            };

    public static Tile getTile(int chunkX, int chunkY, int tileX, int tileY)
    {
        if(tileX >= 0 && tileY >= 0 && !checkEmpty(chunkX,chunkY)) {
            if (tileX < map[chunkX][chunkY].getTileArray().length && tileY < map[chunkX][chunkY].getTileArray()[tileX].length)
                return map[chunkX][chunkY].getTileArray()[tileX][tileY];
            else return TILE_MAP.EMPTY;
        }
        else return TILE_MAP.EMPTY;
        //OLD try catch version
        /*try {
            return map[chunkX][chunkY][tileX][tileY];
        }
        catch (ArrayIndexOutOfBoundsException e){
            //Gdx.app.error("getTile() Error",String.valueOf(e));
            //e.printStackTrace();
            return TILE_MAP.EMPTY;
        }*/


    }

    public static Chunk getChunk(int chunkX, int chunkY) {
        if (chunkX < map.length && chunkX >= 0) if (chunkY < map[chunkX].length && chunkY >= 0) return map[chunkX][chunkY];
        return null;
    }

    public static boolean checkEmpty(int chunkX, int chunkY) {
        if (getChunk(chunkX,chunkY) == null) return true;
        else return getChunk(chunkX,chunkY).isEmpty();
    }

    public static Tile getEmptyTile()
    {
        return TILE_MAP.EMPTY;
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
