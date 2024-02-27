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

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;

public class MenuScreen implements Screen {
    private final Game Game;

    public MenuScreen(final Game Game){
        Gdx.app.setLogLevel(3); //TODO reduce logging level for release builds
        this.Game = Game;
    }

    @Override
    public void render(float delta)
    {
        //TODO implement menu screen and only change screens when the create button is pressed
        //ScreenUtils.clear(Color.BLACK);
        Game.setScreen(Game.gameScreen);
        dispose();
    }

    @Override
    public void pause()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }


}
