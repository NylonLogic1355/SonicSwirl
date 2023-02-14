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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

/**
 * The base class all objects extend from, including the Player.
 */
public abstract class Entity {
    protected float xPos, yPos;
    protected float lSensorX, rSensorX, centreY, topY;

    //TODO reconsider usage of local variables as well as sprite.getx/y
    Sprite sprite;
    Entity(Texture image, int width, int height) {
        sprite = new Sprite(image,width,height);
    }

    public void enforceBoundaries()
    {
        // "Invisible walls" - prevent objects from going beyond borders to simplify calculations. TODO stop collision errors when going outside index bounds
        //xPos = Math.min(xPos,1280);
        //yPos = Math.min(yPos,720);
        xPos = Math.max(xPos,0);
        //yPos = Math.max(yPos,0);
    }

    public void calculateSensorPositions()
    {
        lSensorX = xPos;
        rSensorX = xPos + (sprite.getWidth() - 1); // xPos + (srcWidth - 1) - using srcWidth places it one pixel right of the square
        centreY = yPos + ((sprite.getHeight() - 1) / 2);
        topY = yPos + (sprite.getHeight() - 1);
    }
    public float snapToNearest (float angle, float snapTo) {
        return MathUtils.round(angle/snapTo) * snapTo;
    }


    /*
    @Deprecated
    public int regression(int chunkX, int chunkY, int tileX, int tileY, int grid)
    {
        //TODO possibly use for more accuracy?

        byte height;

        //INCREMENT Tile
        if (tileY < 7)
        {
            tileY = tileY + 1;
        }
        else
        {
            chunkY +=1;
            tileY = 0;
        }


        height = TileMap.map.getHeight(chunkX,chunkY,tileX,tileY,grid);

        //CHECK height depending on conditions
        if (height == 0) {
            return 0;
        }
        else if (height < 16) {
            return 1;
        }
        else {
            return MathUtils.clamp(1 + regression(chunkX, chunkY, tileX, tileY, grid),0,2);
        }


    }
    */


}
