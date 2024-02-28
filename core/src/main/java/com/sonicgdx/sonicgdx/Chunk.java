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

import com.badlogic.gdx.graphics.Texture;

import static com.sonicgdx.sonicgdx.TileMap.TILES_PER_CHUNK;

public class Chunk {
    private Texture texture;
    private final Tile[][] tileArray;
    private final boolean empty;

    public Chunk(Texture texture, Tile[][] tileArray) {
        if (tileArray.length == TILES_PER_CHUNK) this.tileArray = tileArray;
        else throw new IllegalArgumentException("heightArray Length is " +  tileArray.length + " instead of " + TILES_PER_CHUNK);
        this.empty = false;
        this.texture = texture;
    }

    public Chunk(Tile[][] tileArray) {
        if (tileArray.length == TILES_PER_CHUNK) this.tileArray = tileArray;
        else throw new IllegalArgumentException("heightArray Length is " +  tileArray.length + " instead of " + TILES_PER_CHUNK);
        this.empty = true;
    }

    public Texture getTexture() {
        return texture;
    }

    /**
     * This returns the tileArray, but doesn't perform any validations on it. Discouraged.
     * @see TileMap#getTile(int chunkX, int chunkY, int tileX, int tileY) instead.
     * @return the tileArray
     */
    public Tile[][] getTileArray() {
        return tileArray;
    }
    public boolean isEmpty() {
        return empty;
    }
}
