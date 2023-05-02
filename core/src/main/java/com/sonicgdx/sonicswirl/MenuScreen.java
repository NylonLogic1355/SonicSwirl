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

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {

    //Stage is a class that automatically processes user input and directs
    //click / touch positions to the appropriate UI element action
    private final Stage stage;

    //The table class positions widgets automatically depending on the screen size
    //which is better than absolute positioning
    private final Table table;

    private final BitmapFont uiFont;

    //The initial class that sets the screen. Used for sharing a SpriteBatch
    final Init Init;
    Skin buttonSkin; TextButton createButton;

    public MenuScreen(final Init Init){

        this.Init = Init;
        Init.gameScreen = new GameScreen(Init);

        //Reuses the SpriteBatch from the Init class to improve performance
        stage = new Stage(new ScreenViewport(),Init.batch);
        Gdx.input.setInputProcessor(stage);

        table = new Table();

        /*This is the root table, so FillParent is enabled
        because it is used for positioning "children" widgets*/
        table.setFillParent(true);

        //Stage models real life theatre where the stage comprises actors that "act" to update their positions
        stage.addActor(table);

        //DEBUG - draws debug lines on tables to visualize what is happening in the layout
        table.setDebug(false);

        //the skin defines the appearance of UI elements in different states
        buttonSkin = new Skin(Gdx.files.internal("ui/uiskin.json")); //Constructor automatically finds and disposes atlas file as required.

        uiFont = new BitmapFont();

        //Get the font stored with the name 'default' in the atlas and set its scale to 5x.
        //Queried fonts in the atlas - System.out.println(buttonSkin.getAll(BitmapFont.class));
        buttonSkin.getFont("default").getData().setScale(1f);


        createButton = new TextButton("Begin", buttonSkin);
        //adds the button as a child of the table, so it is automatically positioned
        //then sets its width and height to 100
        table.add(createButton).height(100).width(100);

    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(Color.WHITE);

        stage.act(delta);
        stage.draw();

    }
    @Override
    public void dispose() {
        buttonSkin.dispose();
        stage.dispose();
    }
    @Override
    //The third argument in the update method enables the camera to be centred
    //which is used in the Main Menu because the position doesn't change
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,true);
    }

    @Override
    public void pause()
    {
        // TODO Auto-generated method stub
    }
    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }
    @Override
    public void show() {
        // TODO Auto-generated method stub

    }
    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }
}
