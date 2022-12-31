package com.sonicgdx.sonicswirl;

public class Tile {

    boolean isFlipped, empty;
    byte[] heightArray, widthArray;
    byte solidity;
    // 0 = solid from top, 1 = solid from bottom, 2 = solid from left, 3 = solid from right, 4 = solid from all sides
    float angle;
    Tile(byte[] heightArray, byte[] widthArray, float angle, byte solid, boolean flipped) {
        this.empty = false;

        if (heightArray.length == 16) this.heightArray = heightArray;
        else throw new RuntimeException("heightArray Length = " + String.valueOf(heightArray.length));
        if (widthArray.length == 16) this.widthArray = widthArray;
        else throw new RuntimeException("widthArray Length = " + String.valueOf(widthArray.length));

        this.angle = angle;
        this.isFlipped = flipped;
        this.solidity = solid;

    }
    Tile()
    {
        this.empty = true;
        this.heightArray = null;
        this.widthArray = null;
        this.angle = 0;
    }

    public byte getHeight(int block)
    {
        if (empty || block < 0 || block > 15) return 0;
        else {
            return heightArray[block];
        }

    }
    public byte getWidth(int block)
    {
        if (empty || block < 0 || block > 15) return 0;
        else {
            return widthArray[block];
        }
    }


}
