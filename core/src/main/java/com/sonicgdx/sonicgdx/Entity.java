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

    /**
     * With the assumption that position refers to the centre of the object's hitbox, the corner locations of the hitbox are calculated.
     */
    public void calculateCornerPositions()
    {
        leftEdgeX = position.x - WIDTH_RADIUS;
        bottomEdgeY = position.y - HEIGHT_RADIUS;
        rightEdgeX = position.x + WIDTH_RADIUS;
        topEdgeY = position.y + HEIGHT_RADIUS;
    }
    public float snapToNearestX(float angle, float x) {
        return MathUtils.round(angle/x) * x;
    }
}
