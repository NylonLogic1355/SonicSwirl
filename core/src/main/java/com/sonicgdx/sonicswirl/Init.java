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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Init extends Game {
    public SpriteBatch batch;
	public BitmapFont font;
	public GameScreen gameScreen;
	private MenuScreen menuScreen;

	public void create() {
        //Log level of 3 logs everything
        Gdx.app.setLogLevel(3); //TODO reduce logging level for release builds

        batch = new SpriteBatch(); // sprite batch provides multiple sprites to draw to the GPU to improve openGl performance https://gamedev.stackexchange.com/questions/32910/what-is-the-technical-defInition-of-sprite-batching
        // Can be easily shared between screens
		menuScreen = new MenuScreen(this);
		font = new BitmapFont(); // Default font = Liberation Sans
		this.setScreen(menuScreen);
	}

	public void render() {
        super.render(); // This is required when extending the game class if you want to implement another screen e.g. menu
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
        gameScreen.dispose();
		menuScreen.dispose();
	}

}
