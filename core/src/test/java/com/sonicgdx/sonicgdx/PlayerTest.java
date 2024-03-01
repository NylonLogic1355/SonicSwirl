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

import com.sonicgdx.Player;
import org.junit.jupiter.api.Test;

class PlayerTest {

    private final Player player = new Player(10,10);

    @Test
    void slopeTest() {
        final float delta60 = 0.01666667F;
        player.sprite.setX(0);
    }
}
