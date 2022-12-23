package com.sonicgdx.sonicswirl;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

//import com.badlogic.gdx.maps.tiled.TiledMap;
//import java.util.Arrays;
//import java.awt.*;
//import com.badlogic.gdx.math.Rectangle;

public class SonicGDX implements Screen {

    final Init Init; TileMap tm;
    ShapeRenderer dr; Texture img; Texture playerImg; FPSLogger frameLog;
    float speedX = 0, speedY = 0, groundSpeed = 0, x = 600, y = 200; // Player starts at (600,200);
    final int PLAYER_WIDTH = 20, PLAYER_HEIGHT = 40;
    final float ACCELERATION = 168.75F; final int DEBUG_SPEED = 90, DECELERATION = 1800, MAX_SPEED = 360;
    // An FPS of 60 was used to obtain the adjusted values
    // Original: ACCELERATION = 0.046875F, DECELERATION = 0.5F, DEBUG_SPEED = 1.5F, MAX_SPEED = 6;
    // Original values were designed to occur 60 times every second so by multiplying it by 60 you get the amount of pixels moved per second.

    //TODO change usage of local variables x and y
    OrthographicCamera camera; Viewport viewport; Vector2 cameraOffset = Vector2.Zero;
    final int TILE_SIZE = 16, CHUNK_SIZE = 128, TILES_PER_CHUNK = CHUNK_SIZE / TILE_SIZE;
    boolean debugMode = false;
    boolean fSensors,cSensors,wSensors; //when grounded, fsensors are active. TODO
    int vpHeight, vpWidth;

    Player player;

    public SonicGDX(final Init Init) {

        //Have to declare it outside, so it is a global variable?

        this.Init = Init;

        //TODO implement class with reference to https://gamedev.stackexchange.com/a/133593

        //Gdx.app.debug("debugMode",String.valueOf(tile[1][3][15]));

        vpWidth = Gdx.app.getGraphics().getWidth(); vpHeight = Gdx.app.getGraphics().getHeight();
        //TODO possibly reduce viewport resolution to reduce pixels being missing at lower resolutions or change viewport type

        camera = new OrthographicCamera(); // 3D camera which projects into 2D.
        viewport = new FitViewport(vpWidth,vpHeight,camera);

        // stretch viewport //TODO Update comments
        camera.setToOrtho(false); // Even if the device has a scaled resolution, the in game view will still be 1280x720
        // So for example, one screen won't be in the bottom left corner in 1080p
        // but would take up the entire view

        tm = new TileMap();
        dr = new ShapeRenderer();
        img = new Texture(Gdx.files.internal("1x1-ffffffff.png")); playerImg = new Texture(Gdx.files.internal("1x1-000000ff.png"));
        player = new Player(playerImg,PLAYER_WIDTH,PLAYER_HEIGHT);

        player.sprite.setPosition(x,y);

        cameraOffset.x = camera.position.x - player.sprite.getX();
        cameraOffset.y = camera.position.y - player.sprite.getY();

        frameLog = new FPSLogger();

    }

    @Override
    public void render(float delta) {

        frameLog.log();

        ScreenUtils.clear(Color.DARK_GRAY); // clears the screen and sets the background to a certain colour

        //TODO Would be better to implement an InputProcessor. This makes more sense as an interrupt rather than constant polling.
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q))
        {
            debugMode = !debugMode;
            groundSpeed = 0;
            //Gdx.app.log("debugMode",String.valueOf(debugMode));
            //TODO ACCELERATION in debug mode
        }
        //TODO move movement into player class
        if (debugMode) {
            if (Gdx.input.isKeyPressed(Input.Keys.D)) x += (DEBUG_SPEED * delta);
            if (Gdx.input.isKeyPressed(Input.Keys.A)) x -= (DEBUG_SPEED * delta);
            if (Gdx.input.isKeyPressed(Input.Keys.W)) y += (DEBUG_SPEED * delta);
            if (Gdx.input.isKeyPressed(Input.Keys.S)) y -= (DEBUG_SPEED * delta);
            //Gdx.app.debug("deltaTime",String.valueOf(delta));
        }
        else {
            if (Gdx.input.isKeyPressed(Input.Keys.D) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT))) // if moving right
            {
                if (groundSpeed < 0) groundSpeed += (DECELERATION * delta); // Deceleration acts in the opposite direction to the one in which the player is currently moving.
                else if (groundSpeed < MAX_SPEED) groundSpeed += (ACCELERATION * delta); //Takes 128 frames to accelerate from 0 to 6 - exactly 2 seconds
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.A) || (Gdx.input.isKeyPressed(Input.Keys.LEFT))) // if moving left
            {
                if (groundSpeed > 0) groundSpeed -= (DECELERATION * delta);
                else if (groundSpeed > -MAX_SPEED) groundSpeed -= ACCELERATION * delta;
            }
            else groundSpeed -= Math.min(Math.abs(groundSpeed), ACCELERATION * delta) * Math.signum(groundSpeed); // friction if not pressing any directions
            // Decelerates until the absolute value of groundSpeed is lower than the ACCELERATION value (which doubles as the friction value) and then stops

            x += groundSpeed * delta;

            //TODO ground angle and sin/cos with Gdx MathUtils

        }

        //TODO check for jumps here

        // "Invisible walls" - prevent players from going beyond borders to simplify calculations. TODO stop collision errors when going outside index bounds
        x = Math.min(x,1280);
        x = Math.max(x,0);
        y = Math.max(y,0);

        player.sprite.setPosition(x, y); camera.position.set(x + cameraOffset.x,y + cameraOffset.y,camera.position.z); camera.update(); // recompute matrix for orthographical projection so that the change is responded to in the view

        boolean nothing = player.checkTile(x,y,tm);

        player.lSensorX = x;
        player.rSensorX = x + (player.sprite.getWidth() - 1); // x pos + (srcWidth - 1) - using srcWidth places it one pixel right of the square
        player.middleY = y + (player.sprite.getHeight() / 2);


        //TODO Add collision logic

        dr.setProjectionMatrix(camera.combined);
        dr.begin(ShapeRenderer.ShapeType.Filled);
        //TODO render gradually as player progresses
        for (int chunkX = 0; chunkX<tm.map.length; chunkX++)
        {
            for (int chunkY = 0; chunkY<tm.map[chunkX].length; chunkY++)
            {
                drawChunkDR(chunkX,chunkY);
            }
        }
        dr.end();

        // tells the SpriteBatch to render in the coordinate system specified by the camera
        Init.batch.setProjectionMatrix(camera.combined);
        Init.batch.begin();
        player.sprite.draw(Init.batch);
        // DEBUG
        Init.batch.draw(img,player.lSensorX,y); Init.batch.draw(img,player.rSensorX,y); Init.batch.draw(img,player.lSensorX,player.middleY); Init.batch.draw(img,player.rSensorX,player.middleY);
        Init.batch.end();
    }

    //TODO multithreading except for GWT?
    public void drawChunkDR(int chunkX, int chunkY) {

        for (int tileX = 0; tileX < TILES_PER_CHUNK; tileX++)
        {
            for (int tileY = 0; tileY < TILES_PER_CHUNK; tileY++)
            {
                if (tm.map[chunkX][chunkY][tileX][tileY].empty){
                    continue;
                }
                for (int block = 0; block < TILE_SIZE; block++)
                {

                    if (block==0) dr.setColor(new Color(0));
                    else dr.setColor(new Color(0.125F * tileY,0,block,0));
                    dr.rect( block + (tileX*TILE_SIZE)+(chunkX*CHUNK_SIZE),(tileY*TILE_SIZE)+(chunkY*CHUNK_SIZE),1,tm.map[chunkX][chunkY][tileX][tileY].height[block]);

					/*if ((int) x == (chunkX*128 + tileX*16+block))
					{
						if (tm.map[chunkX][chunkY][tileX][tileY].solidity == 0);
					}*/

                    //TODO reversed search order for flipped tiles. e.g. Collections.reverse() or ArrayUtils.reverse(byte[] array)

                }
            }
        }
    }
    @Deprecated
    public void drawChunkBatch(int chunkX, int chunkY) {

        for (int tileX = 0; tileX < TILES_PER_CHUNK; tileX++)
        {
            for (int tileY = 0; tileY < TILES_PER_CHUNK; tileY++)
            {
                for (int block = 0; block < TILE_SIZE; block++)
                {
                    if (tm.map[chunkX][chunkY][tileX][tileY].empty){
                        break;
                    }
                    Init.batch.draw(img, block + (tileX*TILE_SIZE)+(chunkX*CHUNK_SIZE),(tileY*TILE_SIZE)+(chunkY*CHUNK_SIZE),1, tm.map[chunkX][chunkY][tileX][tileY].height[block]);

					/*if ((int) x == (chunkX*128 + tileX*16+block))
					{
						if (tm.map[chunkX][chunkY][tileX][tileY].solidity == 0);
					}*/

                    //TODO reversed search order for flipped tiles. e.g. Collections.reverse() or ArrayUtils.reverse(byte[] array)

                }
            }
        }
    }

    @Override
    public void dispose () {
        img.dispose();
        playerImg.dispose();
        dr.dispose();
    }
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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