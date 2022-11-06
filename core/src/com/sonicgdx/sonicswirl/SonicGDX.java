package com.sonicgdx.sonicswirl;

import com.badlogic.gdx.Game;
// input handling:
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

//import java.awt.*;

//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.Game; -- replaces ApplicationAdapter
public class SonicGDX extends Game {
	SpriteBatch batch; Sprite sprite1; Texture img, img2; ShapeRenderer dr;
	OrthographicCamera camera;
	// capital F can be used to cast from double to float (e.g. 50.55F)
	float speedX = 0, speedY = 0, groundSpeed = 0;
	float x = 600, y = 200; //https://colourtann.github.io/HelloLibgdx/

	Vector2 cameraOffset = Vector2.Zero;

	//https://info.sonicretro.org/SPG:Solid_Tiles#Sensors
	float leftFootSensor, rightFootSensor;

	static final float accel = 0.046875F, decel = 0.5F;

	//collision
	boolean isGrounded = false;

	int[] heightArray = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};

	int[][] chunk = {{16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16},{16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16},{16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16},{16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16},{16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16},{16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16}};

	// https://libgdx.com/wiki/start/a-simple-game

	@Override
	public void create () { // equivalent to start in unity

		// implement class with reference to https://gamedev.stackexchange.com/a/133593

		camera = new OrthographicCamera(); // 3D camera which projects into 2D.
   		camera.setToOrtho(false, 1280, 720); // Even if the device has a scaled resolution, the in game view will still be 1280x720
		// So for example, one screen won't be in the bottom left corner in 1080p
		// but would take up the entire view

		// https://web.archive.org/web/20200427232345/https://www.badlogicgames.com/wordpress/?p=1550


		dr = new ShapeRenderer();
		batch = new SpriteBatch(); //sprite batch provides multiple sprites to draw to the GPU to improve openGl performance https://gamedev.stackexchange.com/questions/32910/what-is-the-technical-definition-of-sprite-batching
		img = new Texture("1x1-ffffffff.png"); img2 = new Texture("1x1-000000ff.png");

		sprite1 = new Sprite(img2,25,50);
		sprite1.setPosition(x,y);

		cameraOffset.x = camera.position.x - sprite1.getX();
		cameraOffset.y = camera.position.y - sprite1.getY();

	}

	@Override
	public void render () { // equivalent to update in unity

		ScreenUtils.clear(Color.GRAY); // clears the screen and sets the background to a certain colour

		camera.update(); // recompute matrix for orthographical projection - this is necessary if it needs to move.

		dr.setProjectionMatrix(camera.combined);
		dr.begin(ShapeRenderer.ShapeType.Filled);
/*		for (int i=0;i<8;i++)
		{
			System.out.println(i);
			for (int j=0;j<8;j++)
			{
				Color colour = new Color(0.1F*i,0.1F*j,0.1F*i*j,1);
				dr.rect(200+16*i,200+16*j,16F,16F,colour,colour,colour,colour);
			}
		}*/

		dr.end();


		/*if (!isGrounded)
		{
			y -= 9.8;
		}*/

		//if (250-y >= 150) y += 10;
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			//ternary operator
			groundSpeed = (groundSpeed + accel <= 6) ? (groundSpeed + accel) : 6;
			//Takes 128 frames to accelerate from 0 to 6 - exactly 2 seconds

			if (x <= 1280) {
				x += groundSpeed;
				if (x > 1280) x = 20;
			}

		} else {
			groundSpeed = (groundSpeed - decel >= 0) ? (groundSpeed - decel) : 0;
			x += groundSpeed;
		}
		sprite1.setPosition(x, y);

		leftFootSensor = sprite1.getX();
		rightFootSensor = sprite1.getX() + (sprite1.getWidth() - 1); // x pos + (srcWidth - 1) - using srcWidth places it one pixel right of the square

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera. - https://libgdx.com/wiki/start/a-simple-game
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for (int i=0;i<5;i++)
		{
			for (int j =0; j<5;j++)
			{
				drawChunk(i,j);
			}
		}

		sprite1.draw(batch);
		batch.draw(img,leftFootSensor,y); // draw left foot sensor - DEBUG
		batch.draw(img,rightFootSensor,y); // draw right foot sensor - DEBUG

		camera.position.set(sprite1.getX() + cameraOffset.x,sprite1.getY() + cameraOffset.y,camera.position.z);

		//System.out.println(sprite1.getBoundingRectangle()); // use for collision detection - to set, it is sprite.SetBounds()
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		dr.dispose();
	}

	public void drawChunk(int chunkX, int chunkY)
	{
		for (int i = 0; i < 8;i++)
			for (int j = 0; j < 8; j++) {
				// Individual tile
				for (int block = 0; block < heightArray.length; block++) {
					// Individual block
					for (int height = 0; height < heightArray[block]; height++) batch.draw(img, block+(i)*16, height+(j)*16);
				}
			}
	}

}