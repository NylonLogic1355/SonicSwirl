package com.sonicgdx.sonicswirl;

import com.badlogic.gdx.math.MathUtils;

public class WallSensor extends Sensor{
    public WallSensor(float xPos, float yPos) {
        super(xPos,yPos);
    }

    @Override
    void process() {
        if (xPosition < 0 || yPosition < 0) {
            tile = TileMap.getEmpty(); distance = -50;
            return;
        }
        //TODO prevent catch block in getTile() from being used.

        int tileX = Math.floorMod(MathUtils.round(xPosition), 128) / 16;
        int chunkX = MathUtils.round(xPosition) / 128;

        int tileY = Math.floorMod(MathUtils.round(yPosition), 128) / 16;
        int chunkY = MathUtils.round(yPosition) / 128;

        int block = Math.floorMod(MathUtils.round(yPosition),16); //Different behaviour for negative numbers compared to using %. For
        // example, -129 % 16 would return -1 which would cause an ArrayIndexOutOfBoundsException. Math.floorMod() would return a positive index in these cases.

        byte width = TileMap.getTile(chunkX,chunkY,tileX,tileY).getWidth(block);

        float checkDistance = ((chunkX * 128) + (tileX * 16) + width) - yPosition;

        tile = TileMap.getTile(chunkX,chunkY,tileX,tileY); distance = checkDistance;
    }


}
