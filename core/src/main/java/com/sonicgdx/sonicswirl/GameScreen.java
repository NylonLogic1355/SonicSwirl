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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameScreen implements Screen {

    private final Init Init;
    private final Texture whiteSquare, blackSquare;
    //private final FPSLogger frameLog;
    private final OrthographicCamera camera; private final Vector2 cameraOffset = Vector2.Zero; private final ExtendViewport gameViewport;

    Player player;

    private final Texture backgroundTexture;

    private int drawMode;
    public static final int TILE_SIZE = 16;
    public static final int CHUNK_SIZE = 96;

    public GameScreen(final Init Init) {

        //Have to declare it outside, so it is a global variable?

        this.Init = Init;

        //TODO implement class with reference to https://gamedev.stackexchange.com/a/133593

        //Gdx.app.debug("debugMode",String.valueOf(tile[1][3][15]));
        //TODO possibly reduce viewport resolution to reduce pixels being missing at lower resolutions or change viewport type

        camera = new OrthographicCamera(); // 3D camera which projects into 2D.
        gameViewport = new ExtendViewport(1280,720,camera);
        //TODO Update comments
        camera.setToOrtho(false,1280,720); // Even if the device has a scaled resolution, the in game view will still be 1280x720
        // So for example, one screen won't be in the bottom left corner in 1080p
        // but would take up the entire view

        //TODO AssetManager
        whiteSquare = new Texture(Gdx.files.internal("1x1-ffffffff.png")); blackSquare = new Texture(Gdx.files.internal("1x1-000000ff.png"));
        final int PLAYER_WIDTH = 20, PLAYER_HEIGHT = 40; //FIXME standardise
        player = new Player(whiteSquare, PLAYER_WIDTH,PLAYER_HEIGHT);

        cameraOffset.x = 0; //TODO adjust view when looking up or down
        cameraOffset.y = camera.position.y - player.yPos;

        //frameLog = new FPSLogger();
        backgroundTexture = new Texture(Gdx.files.internal("sprites/aiz_background.jpg"));

    }

    @Override
    public void render(float delta) {
        //UNCOMMENT For debug
        //frameLog.log();
        //delta = 0.016666668f;

        ScreenUtils.clear(Color.DARK_GRAY); // clears the screen and sets the background to a certain colour

        //Toggle between one of two draw modes: texture drawing and height array drawing
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            drawMode += 1;
            if (drawMode == 2) drawMode = 0;
        }

        player.move(delta);

        //TODO check for jumps here

        //Updates the camera position to where the player is but keeps the offset
        camera.position.set(player.xPos + cameraOffset.x,player.yPos + cameraOffset.y,camera.position.z); camera.update(); // recompute matrix for orthographical projection so that the change is responded to in the view

        //TODO Add collision logic

        // tells the SpriteBatch to render in the coordinate system specified by the camera
        //viewport.apply();
        Init.batch.setProjectionMatrix(camera.combined);
        Init.batch.begin();
        //Disabling and enabling blending gives a performance boost
        //and transparency is not needed for the background image
        Init.batch.disableBlending();
        Init.batch.draw(backgroundTexture,camera.position.x - (camera.viewportWidth / 2),camera.position.y - (camera.viewportHeight / 2));
        Init.batch.enableBlending();
        //TODO render gradually as player progresses

        //Iterates through every chunk on the x-axis
        for (int chunkX = 0; chunkX<TileMap.map.length; chunkX++)
        {
            //Iterates through every chunk on the y-axis
            for (int chunkY = 0; chunkY<TileMap.map[chunkX].length; chunkY++)
            {
                //TODO draw tile width batch
                //Draw the chunk's texture unless the debug drawing mode has been toggled
                if (drawMode == 0) drawChunkTextureBatch(chunkX,chunkY);
                else drawChunkHeightBatch(chunkX,chunkY);
            }
        }
        player.sprite.draw(Init.batch);
        // DEBUG - draw white squares at sensor locations on the player
        Init.batch.draw(whiteSquare,player.leftEdgeX,player.yPos); Init.batch.draw(whiteSquare,player.rightEdgeX,player.yPos);
        Init.batch.draw(whiteSquare,player.leftEdgeX,player.bottomEdgeY); Init.batch.draw(whiteSquare,player.rightEdgeX,player.bottomEdgeY);
        Init.batch.draw(whiteSquare,player.leftEdgeX,player.topEdgeY); Init.batch.draw(whiteSquare,player.rightEdgeX,player.topEdgeY);
        Init.batch.draw(whiteSquare,player.xPos,player.yPos);
        Init.batch.end();
    }

    //TODO multithreading except for GWT?

    /**
     * Draws each Tile in a chunk using a gradient - for debugging purposes only
     * @param chunkX the chunk number on the x-axis - not the same as its co-ordinate
     * @param chunkY the chunk number on the y-axis - not the same as its co-ordinate
     */
    public void drawTileHeightBatch(int chunkX, int chunkY) {
        final int TILES_PER_CHUNK = CHUNK_SIZE / TILE_SIZE;
        //Iterates through every tile in the chunk
        for (int tileX = 0; tileX < TILES_PER_CHUNK; tileX++)
        {
            for (int tileY = 0; tileY < TILES_PER_CHUNK; tileY++)
            {
                //skip if empty
                if (TileMap.map[chunkX][chunkY].getTileArray()[tileX][tileY].empty) continue;
                //draw the tile's height array at its location
                for (int block = 0; block < TILE_SIZE; block++)
                {
                    if (block==0) Init.batch.setColor(new Color(0,0,0,1));
                    else Init.batch.setColor(new Color((1F/ TILES_PER_CHUNK) * tileY,0,block,1));
                    Init.batch.draw(whiteSquare, block + (tileX* TILE_SIZE)+(chunkX* CHUNK_SIZE),(tileY* TILE_SIZE)+(chunkY* CHUNK_SIZE),1, TileMap.map[chunkX][chunkY].getTileArray()[tileX][tileY].getHeight(block));

                    //TODO reversed search order for flipped tiles. e.g. Collections.reverse() or ArrayUtils.reverse(byte[] array)

                }
            }
        }
        Init.batch.setColor(Color.WHITE); //Resets batch colour

    }

    /**
     * Draws each Tile using a gradient - for debugging purposes only
     * @param chunkX the chunk number on the x-axis - not the same as its co-ordinate
     * @param chunkY the chunk number on the y-axis - not the same as its co-ordinate
     */
    public void drawTileWidthBatch(int chunkX, int chunkY) {

        for (int tileX = 0; tileX < TILES_PER_CHUNK; tileX++)
        {
            for (int tileY = 0; tileY < TILES_PER_CHUNK; tileY++)
            {
                if (TileMap.map[chunkX][chunkY][tileX][tileY].empty) continue;
                for (int block = 0; block < TILE_SIZE; block++)
                {
                    if (block==0) Init.batch.setColor(new Color(0,0,0,1));
                    else Init.batch.setColor(new Color((1F/TILES_PER_CHUNK) * tileY,0,block,1));

                    int yPosition = (tileY * TILE_SIZE) + (chunkY * CHUNK_SIZE) + block;
                    if (!TileMap.map[chunkX][chunkY][tileX][tileY].horizontalFlip) {
                        Init.batch.draw(img, (tileX * TILE_SIZE) + (chunkX * CHUNK_SIZE) + (TILE_SIZE - TileMap.map[chunkX][chunkY][tileX][tileY].getWidth(TILE_SIZE - block - 1)), yPosition, TileMap.map[chunkX][chunkY][tileX][tileY].getWidth(TILE_SIZE - block - 1), 1);
                    } else {
                        Init.batch.draw(img, (tileX * TILE_SIZE) + (chunkX * CHUNK_SIZE), yPosition, TileMap.map[chunkX][chunkY][tileX][tileY].getWidth(TILE_SIZE - block - 1), 1);
                    }

                    //TODO reversed search order for flipped tiles. e.g. Collections.reverse() or ArrayUtils.reverse(byte[] array)

                }
            }
        }
        Init.batch.setColor(Color.WHITE); //Resets batch colour


    }

    /**
     * Draws each Tile using a gradient - for debugging purposes only
     * @param chunkX the chunk number on the x-axis - not the same as its co-ordinate
     * @param chunkY the chunk number on the y-axis - not the same as its co-ordinate
     */
    public void drawTileWidthBatch(int chunkX, int chunkY) {

        for (int tileX = 0; tileX < TILES_PER_CHUNK; tileX++)
        {
            for (int tileY = 0; tileY < TILES_PER_CHUNK; tileY++)
            {
                if (TileMap.map[chunkX][chunkY][tileX][tileY].empty) continue;
                for (int block = TILE_SIZE - 1 ; block >= 0; block--)
                {
                    if (block==0) Init.batch.setColor(new Color(0,0,0,1));
                    else Init.batch.setColor(new Color((1F/TILES_PER_CHUNK) * tileY,0,block,1));
                    Init.batch.draw(img, block + (tileX*TILE_SIZE)+(chunkX*CHUNK_SIZE),(tileY*TILE_SIZE)+(chunkY*CHUNK_SIZE),1, TileMap.map[chunkX][chunkY][tileX][tileY].getWidth(block));

                    //TODO reversed search order for flipped tiles. e.g. Collections.reverse() or ArrayUtils.reverse(byte[] array)

                }
            }
        }
        Init.batch.setColor(Color.WHITE); //Resets batch colour

    }

    /**
     * Draws each Chunk's assigned texture at its corresponding location
     * @param chunkX the chunk number on the x-axis - not the same as its co-ordinate
     * @param chunkY the chunk number on the y-axis - not the same as its co-ordinate
     */
    public void drawChunkTextureBatch(int chunkX, int chunkY) {
        //If the chunk isn't empty, draw its texture at the chunk's location
        if (!TileMap.map[chunkX][chunkY].isEmpty()) Init.batch.draw(TileMap.map[chunkX][chunkY].getTexture(), (chunkX* CHUNK_SIZE),(chunkY* CHUNK_SIZE),CHUNK_SIZE, CHUNK_SIZE);

    }

    /**
     * Draws each Tile using a gradient - for debugging purposes only
     * @param chunkX the chunk number on the x-axis - not the same as its co-ordinate
     * @param chunkY the chunk number on the y-axis - not the same as its co-ordinate
     * @deprecated Superseded by drawChunkBatch as ShapeRenderer uses its own mesh compared to the SpriteBatch and therefore conflicts in the rendering method making it cumbersome to use.
     */
    @Deprecated
    public void drawTileHeightShapeRenderer(int chunkX, int chunkY) {

        /*//TODO Foreach loop?
        for (int tileX = 0; tileX < TILES_PER_CHUNK; tileX++)
        {
            for (int tileY = 0; tileY < TILES_PER_CHUNK; tileY++)
            {
                if (TileMap.map[chunkX][chunkY][tileX][tileY].empty){
                    continue;
                }
                for (int block = 0; block < TILE_SIZE; block++)
                {

                    if (block==0) shapeRenderer.setColor(new Color(0,0,0,1));
                    else shapeRenderer.setColor(new Color((1F/TILES_PER_CHUNK) * tileY,0,block,1));
                    shapeRenderer.rect( block + (tileX*TILE_SIZE)+(chunkX*CHUNK_SIZE),(tileY*TILE_SIZE)+(chunkY*CHUNK_SIZE),1,TileMap.map[chunkX][chunkY][tileX][tileY].getHeight(block));

                    //TODO reversed search order for flipped tiles. e.g. Collections.reverse() or ArrayUtils.reverse(byte[] array)

                }
            }
        }*/
    }

    @Override
    public void dispose () {
        whiteSquare.dispose();
        blackSquare.dispose();
    }
    @Override
    public void resize(int width, int height) {
        gameViewport.update(width,height);
    }

    @Override
    public void show() {
        //TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        //TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        //TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        //TODO Auto-generated method stub

    }

}
