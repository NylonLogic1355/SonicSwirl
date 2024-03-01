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

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * The base class all objects extend from, including the Player.
 */
public abstract class Entity {
    protected Vector2 position;
    protected float leftEdgeX, rightEdgeX, bottomEdgeY, topEdgeY;

    protected final float WIDTH_RADIUS, HEIGHT_RADIUS;

    //TODO reconsider usage of local variables as well as sprite.getx/y
    Sprite sprite;
    Entity(float widthRadius, float heightRadius) {
        sprite = new Sprite();
        position = new Vector2(); //Initialise Vector with zero co-ordinates to prevent NullPointerExceptions
        this.WIDTH_RADIUS = widthRadius; this.HEIGHT_RADIUS = heightRadius;
    }

    /**
     * @param delta time since last frame. Used to make physics similar to how they would be at 60FPS
     * even with higher, lower or varying frame rates.
     * @see GameScreen#render(float)
     */
    public abstract void update(float delta);

    /**
     * Ensures the player doesn't go into negative co-ordinates as calculations may not
     * take that into account
     */
    public void enforceBoundaries()
    {
        // "Invisible walls" - prevent objects from going beyond borders to simplify calculations. TODO stop collision errors when going outside index bounds
        position.x = Math.max(position.x,0);

        //Respawns the player at the starting position if they fall off the terrain
        //TODO replace with death animation and retry screen before respawning
        if (position.y <= -100) position.set(50,200);
    }

    public void calculateCornerPositions()
    {
        leftEdgeX = position.x - WIDTH_RADIUS;
        bottomEdgeY = position.y - HEIGHT_RADIUS;
        rightEdgeX = position.x + WIDTH_RADIUS; // xPos + (srcWidth - 1) - using srcWidth places it one pixel right of the square
        topEdgeY = position.y + HEIGHT_RADIUS;
    }

    /**
     * Utility method to round angle to nearest x degrees, where x is the value of snapTo (usually 90).
     * @param angle angle which is going to be rounded
     * @param snapTo angle is rounded to a multiple of this value
     * @return The multiple of snapTo that is closest to angle.
     */
    public static float snapToNearest (float angle, float snapTo) {
        return MathUtils.round(angle/snapTo) * snapTo;
    }

    /**
     * @return the X component of the position vecotr
     */
    public float getXPos() {
        return position.x;
    }

    /**
     * @return the Y component of the position vector
     */
    public float getYPos() {
        return position.y;
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

    public void dispose() {

    }


}
