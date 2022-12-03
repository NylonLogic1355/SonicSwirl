package com.sonicgdx.sonicswirl;

import com.badlogic.gdx.Game; // Replaces ApplicationAdapter
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
//import com.badlogic.gdx.maps.tiled.TiledMap;
//import java.util.Arrays;
//import java.awt.*;
//import com.badlogic.gdx.math.Rectangle;

public class SonicGDX extends Game {
	SpriteBatch batch; ShapeRenderer dr; Sprite player; Texture img, img2;
	static final float accel = 0.046875F, decel = 0.5F; float speedX = 0, speedY = 0, groundSpeed = 0, x = 600, y = 200; // Player starts at (600,200);
	OrthographicCamera camera; Vector2 cameraOffset = Vector2.Zero;
	boolean debugMode = false; float leftFootSensor, rightFootSensor;

	TileMap tm;

	FPSLogger frameLog;

	@Override
	public void create () {

		// implement class with reference to https://gamedev.stackexchange.com/a/133593

		//System.out.println(tile[1][3][15]);

		camera = new OrthographicCamera(); // 3D camera which projects into 2D.
   		camera.setToOrtho(false, 1280, 720); // Even if the device has a scaled resolution, the in game view will still be 1280x720
		// So for example, one screen won't be in the bottom left corner in 1080p
		// but would take up the entire view

		tm = new TileMap();

		dr = new ShapeRenderer();
		batch = new SpriteBatch(); //sprite batch provides multiple sprites to draw to the GPU to improve openGl performance https://gamedev.stackexchange.com/questions/32910/what-is-the-technical-definition-of-sprite-batching
		img = new Texture("1x1-ffffffff.png"); img2 = new Texture("1x1-000000ff.png");

		player = new Sprite(img2,20,40);
		player.setPosition(x,y);

		cameraOffset.x = camera.position.x - player.getX();
		cameraOffset.y = camera.position.y - player.getY();

		frameLog = new FPSLogger();

	}

	@Override
	public void render () { // equivalent to update in unity

		//frameLog.log();

		ScreenUtils.clear(Color.GRAY); // clears the screen and sets the background to a certain colour

		dr.setProjectionMatrix(camera.combined);
		dr.begin(ShapeRenderer.ShapeType.Filled);
		dr.end();

		// Would be better to implement an InputProcessor. This makes more sense as an interrupt rather
		// than constant polling.
		if (Gdx.input.isKeyJustPressed(Input.Keys.Q))
		{
			debugMode = !debugMode;
			System.out.println(debugMode);
		}
		if (!debugMode) {
			//if (250-y >= 150) y += 10;
			if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				groundSpeed = (groundSpeed + accel <= 6) ? (groundSpeed + accel) : 6;
				//Takes 128 frames to accelerate from 0 to 6 - exactly 2 seconds

				x = (x <= 1280) ? (x + groundSpeed) : 20;

			} else {
				groundSpeed = (groundSpeed - decel >= 0) ? (groundSpeed - decel) : 0;
				x += groundSpeed;
			}
		}
		else {
			speedX = 0; speedY = 0; groundSpeed = 0;

			if (Gdx.input.isKeyPressed(Input.Keys.D)) speedX += 7;
			if (Gdx.input.isKeyPressed(Input.Keys.A)) speedX -= 7;
			if (Gdx.input.isKeyPressed(Input.Keys.W)) speedY += 7;
			if (Gdx.input.isKeyPressed(Input.Keys.S)) speedY -= 7;

			x += speedX;
			y += speedY;
		}

		player.setPosition(x, y); camera.position.set(x + cameraOffset.x,y + cameraOffset.y,camera.position.z); camera.update(); // recompute matrix for orthographical projection - this is necessary if it needs to move.

		leftFootSensor = player.getX();
		rightFootSensor = player.getX() + (player.getWidth() - 1); // x pos + (srcWidth - 1) - using srcWidth places it one pixel right of the square

		// tell the SpriteBatch to render in the coordinate system specified by the camera
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for (int i=0;i<10;i++)
		{
			for (int j =0; j<1;j++)
			{
				drawChunk(i,j);
			}
		}

		player.draw(batch);

		// DEBUG
		batch.draw(img,leftFootSensor,y); batch.draw(img,rightFootSensor,y);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		dr.dispose();
	}

	public void drawChunk(int chunkX, int chunkY) {

		for (int blockX = 0; blockX < 8; blockX++)
		{
			for (int blockY = 0; blockY < 8; blockY++)
			{
				for (int grid = 0; grid < 16; grid++)
				{
					if (tm.rvMap[chunkX][chunkY][blockX][blockY].empty){
						break;
					}
					batch.draw(img, blockX*16+grid+(128*chunkX),blockY*16+(128*chunkY),1, tm.rvMap[chunkX][chunkY][blockX][blockY].height[grid]);
				}
			}
		}
	}



}