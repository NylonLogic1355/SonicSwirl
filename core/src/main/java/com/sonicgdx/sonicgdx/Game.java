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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Game extends com.badlogic.gdx.Game {
    public SpriteBatch batch;
	public Screen gameScreen;
	//private Screen menuScreen;

    /**
     * Implements method from the ApplicationListener interface.
     */
	@Override
    public void create() {
        Gdx.app.setLogLevel(3); //TODO reduce logging level for release builds

        batch = new SpriteBatch(); // sprite batch provides multiple sprites to draw to the GPU to improve OpenGl performance https://gamedev.stackexchange.com/questions/32910/what-is-the-technical-defInition-of-sprite-batching
        //This is shared between screens to reduce memory usage and OpenGL calls

        gameScreen = new GameScreen(this);
        this.setScreen(gameScreen);


        /*FIXME uncomment when implementing the menu screen
		menuScreen = new MenuScreen(this);
		this.setScreen(menuScreen);*/
	}

	@Override
    public void render() {
        super.render(); // Calls the render method in the game class
	}

	@Override
    public void dispose() {
		batch.dispose();
        gameScreen.dispose();
        //menuScreen.dispose();

        super.dispose();
	}

}
