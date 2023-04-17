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

import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.graphics.Texture;

public class Tile {

    boolean isFlipped, empty;
    byte[] heightArray, widthArray;
    byte solidity;
    // 0 = solid from top, 1 = solid from bottom, 2 = solid from left, 3 = solid from right, 4 = solid from all sides
    float angle;
    Texture texture;

    Tile(byte[] heightArray, byte[] widthArray, float angle, byte solidity, boolean flipped, Texture texture) {
        this.empty = false;

        if (heightArray.length == 16) this.heightArray = heightArray;
        else throw new RuntimeException("heightArray Length = " + heightArray.length);
        if (widthArray.length == 16) this.widthArray = widthArray;
        else throw new RuntimeException("widthArray Length = " + widthArray.length);

        this.angle = angle;
        this.isFlipped = flipped;
        this.solidity = solidity;
        this.texture = texture;

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

    public Texture getTexture() {
        return texture;
    }

    public boolean isEmpty() {
        return empty;
    }

}
