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

import java.util.Collections;

public enum TileMap {

    TILE_MAP;

    // solid blocks
    //TODO tile ID
    //TODO reconsider usage of TileMap class
    //TODO possible GUI chunk editor

    public static final Tile[][][][] map = TILE_MAP.testMap;

    //TODO test class - check if all these are 16 in length

    // Classes are reference types so modifying a value would affect all the tiles that are the same.
    private final byte[] zero = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private final byte[] slope = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    private final byte[] full = {16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16};
    private final byte[] halfh = {8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8}; private final byte[] halfw = {0,0,0,0,0,0,0,0,16,16,16,16,16,16,16,16};
    private final byte[] rvSlope = {16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1};
    private final byte[] tall1 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
    private final byte[] testh = {0,0,1,2,2,3,4,5,5,6,6,7,8,9,9,9}, testw = {0,0,0,0,0,0,0,3,4,5,7,9,10,11,13,14};
    private final Tile EMPTY = new Tile();
    private final Tile ftile = new Tile(full,full,0,(byte) 4,false);
    private final Tile stile = new Tile(slope, slope,45,(byte) 1,false);
    private final Tile rvtile = new Tile(rvSlope, rvSlope,-45,(byte) 1,false);
    private final Tile htile = new Tile(halfh,halfw,0,(byte) 1,false);
    private final Tile testtile = new Tile(testh,testw,33.75F,(byte) 1,false);

    private final Tile[][] fChunk = Collections.nCopies(8,Collections.nCopies(8,ftile).toArray(new Tile[0])).toArray(new Tile[0][0]);

    private final Tile[][] hChunk = {
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,htile},
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,htile}};
    private final Tile[][] rvChunk = {
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,rvtile},
            {ftile,ftile,ftile,ftile,ftile,ftile,rvtile,EMPTY},
            {ftile,ftile,ftile,ftile,ftile,rvtile,EMPTY,EMPTY},
            {ftile,ftile,ftile,ftile,rvtile,EMPTY,EMPTY,EMPTY},
            {ftile,ftile,ftile,rvtile,EMPTY,EMPTY,EMPTY,EMPTY},
            {ftile,ftile,rvtile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
            {ftile,rvtile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
            {rvtile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY}};

    private final Tile[][] sChunk =
            {
                    {stile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
                    {ftile,stile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
                    {ftile,ftile,stile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
                    {ftile,ftile,ftile,stile,EMPTY,EMPTY,EMPTY,EMPTY},
                    {ftile,ftile,ftile,ftile,stile,EMPTY,EMPTY,EMPTY},
                    {ftile,ftile,ftile,ftile,ftile,stile,EMPTY,EMPTY},
                    {ftile,ftile,ftile,ftile,ftile,ftile,stile,EMPTY},
                    {ftile,ftile,ftile,ftile,ftile,ftile,ftile,stile},
            };


    private final Tile[][] eChunk = Collections.nCopies(8,Collections.nCopies(8,EMPTY).toArray(new Tile[0])).toArray(new Tile[0][0]);
    private final Tile[][] testtileChunk = Collections.nCopies(8,Collections.nCopies(8,testtile).toArray(new Tile[0])).toArray(new Tile[0][0]);

    private final Tile[][] borderedChunk = {
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,ftile},
            {ftile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,ftile},
            {ftile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,ftile},
            {ftile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,ftile},
            {ftile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,ftile},
            {ftile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,ftile},
            {ftile,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,ftile},
            {ftile,ftile,ftile,ftile,ftile,ftile,ftile,ftile}

    };

    private final Tile[][][][] fMap = Collections.nCopies(8,Collections.nCopies(8,fChunk).toArray(new Tile[0][0][0])).toArray(new Tile[0][0][0][0]);

    private final Tile[][][][] eMap = Collections.nCopies(8,Collections.nCopies(8,eChunk).toArray(new Tile[0][0][0])).toArray(new Tile[0][0][0][0]);
            /*{ OLD
                    Collections.nCopies(8,eChunk).toArray(new Tile[0][0][0]),
                    {eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk,eChunk}

            };*/

    private final Tile[][][][] sMap = Collections.nCopies(8,Collections.nCopies(8,sChunk).toArray(new Tile[0][0][0])).toArray(new Tile[0][0][0][0]);
    private final Tile[][][][] rvMap = Collections.nCopies(8,Collections.nCopies(8,rvChunk).toArray(new Tile[0][0][0])).toArray(new Tile[0][0][0][0]);
    private final Tile[][][][] testtileMap = Collections.nCopies(8,Collections.nCopies(8,testtileChunk).toArray(new Tile[0][0][0])).toArray(new Tile[0][0][0][0]);
    private final Tile[][][][] testMap =
            {
                    {sChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk},
                    {eChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {fChunk,eChunk,eChunk,eChunk},
                    {rvChunk,eChunk,eChunk,eChunk}

            };


    // 128x128 chunk - one dimension for x, one dimension for y and the data is a height array
    // one height array makes up a 16x16 block

    public static Tile getTile(int chunkX, int chunkY, int tileX, int tileY)
    {
        if(chunkX >= 0 && chunkY >= 0 && tileX >= 0 && tileY >= 0) {
            if (chunkX < map.length && chunkY < map[chunkX].length && tileX < map[chunkX][chunkY].length && tileY < map[chunkX][chunkY][tileX].length)
                return map[chunkX][chunkY][tileX][tileY];
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

    public static Tile getEmpty()
    {
        return TILE_MAP.EMPTY;
    }

    @Deprecated
    public byte getHeight(int chunkX, int chunkY, int tileX, int tileY, int block)
    {
        if (map[chunkX][chunkY][tileX][tileY].empty) return 0;
        else return map[chunkX][chunkY][tileX][tileY].heightArray[block];
    }

    @Deprecated
    public byte getWidth(int chunkX, int chunkY, int tileX, int tileY, int block)
    {
        if (map[chunkX][chunkY][tileX][tileY].empty) return 0;
        else return map[chunkX][chunkY][tileX][tileY].widthArray[block];
    }

    @Deprecated
    public Tile[][][][] getMap() {
        return map;
    }

    @Deprecated
    public byte getHeightAbove(int chunkX, int chunkY, int tileX, int tileY, int block)
    {
        if (tileY < 7) tileY = tileY + 1;
        else
        {
            chunkY +=1;
            tileY = 0;
        }

        if (map[chunkX][chunkY][tileX][tileY].empty) return 0;
        else return map[chunkX][chunkY][tileX][tileY].heightArray[block];
    }
    @Deprecated
    public byte getHeightBelow(int chunkX, int chunkY, int tileX, int tileY, int block)
    {
        if (tileY == 0)
        {
            chunkY--;
            tileY = 7;
        }
        else tileY--;

        if (map[chunkX][chunkY][tileX][tileY].empty) return 0;
        else return map[chunkX][chunkY][tileX][tileY].heightArray[block];
    }

    @Deprecated
    public byte[] getWidthArray(int chunkX, int chunkY, int tileX, int tileY)
    {
        if (map[chunkX][chunkY][tileX][tileY].empty) return new byte[16];
        else return map[chunkX][chunkY][tileX][tileY].widthArray;
    }

    @Deprecated
    public byte[] getHeightArray(int chunkX, int chunkY, int tileX, int tileY)
    {
        if (map[chunkX][chunkY][tileX][tileY].empty) return new byte[16];
        else return map[chunkX][chunkY][tileX][tileY].heightArray;
    }


}
