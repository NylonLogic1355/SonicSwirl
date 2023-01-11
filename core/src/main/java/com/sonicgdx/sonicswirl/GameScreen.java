package com.sonicgdx.sonicswirl;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameScreen implements Screen {

    private final Init Init;
    private final Texture img, playerImg;
    //private final FPSLogger frameLog;
    private final OrthographicCamera camera; private final Vector2 cameraOffset = Vector2.Zero; private final ExtendViewport gameViewport;
    private final int TILE_SIZE = 16, CHUNK_SIZE = 128, TILES_PER_CHUNK = CHUNK_SIZE / TILE_SIZE;

    Player player;

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
        img = new Texture(Gdx.files.internal("1x1-ffffffff.png")); playerImg = new Texture(Gdx.files.internal("1x1-000000ff.png"));
        final int PLAYER_WIDTH = 20, PLAYER_HEIGHT = 40;
        player = new Player(playerImg, PLAYER_WIDTH,PLAYER_HEIGHT);

        cameraOffset.x = camera.position.x - player.xPos;
        cameraOffset.y = camera.position.y - player.yPos;

        //frameLog = new FPSLogger();

    }

    @Override
    public void render(float delta) {
        //UNCOMMENT For debug
        //frameLog.log();
        //delta = 0.016666668f;

        ScreenUtils.clear(Color.DARK_GRAY); // clears the screen and sets the background to a certain colour

        player.move(delta);

        //TODO check for jumps here

        camera.position.set(player.xPos + cameraOffset.x,player.yPos + cameraOffset.y,camera.position.z); camera.update(); // recompute matrix for orthographical projection so that the change is responded to in the view

        //TODO Add collision logic

        // tells the SpriteBatch to render in the coordinate system specified by the camera
        //viewport.apply();
        Init.batch.setProjectionMatrix(camera.combined);
        Init.batch.begin();
        //Blending has been disabled in MenuScreen
        //TODO render gradually as player progresses
        for (int chunkX = 0; chunkX<TileMap.map.length; chunkX++)
        {
            for (int chunkY = 0; chunkY<TileMap.map[chunkX].length; chunkY++)
            {
                drawTileWidthBatch(chunkX,chunkY);
            }
        }
        player.sprite.draw(Init.batch);
        // DEBUG
        Init.batch.draw(img,player.lSensorX,player.sprite.getY()); Init.batch.draw(img,player.rSensorX,player.sprite.getY());
        Init.batch.draw(img,player.lSensorX,player.centreY); Init.batch.draw(img,player.rSensorX,player.centreY);
        Init.batch.draw(img,player.lSensorX,player.topY); Init.batch.draw(img,player.rSensorX,player.topY);
        Init.batch.end();
    }

    //TODO multithreading except for GWT?

    /**
     * Draws each Tile using a gradient - for debugging purposes only
     * @param chunkX the chunk number on the x-axis - not the same as its co-ordinate
     * @param chunkY the chunk number on the y-axis - not the same as its co-ordinate
     */
    public void drawTileHeightBatch(int chunkX, int chunkY) {

        for (int tileX = 0; tileX < TILES_PER_CHUNK; tileX++)
        {
            for (int tileY = 0; tileY < TILES_PER_CHUNK; tileY++)
            {
                if (TileMap.map[chunkX][chunkY][tileX][tileY].empty) continue;
                for (int block = 0; block < TILE_SIZE; block++)
                {
                    if (block==0) Init.batch.setColor(new Color(0,0,0,1));
                    else Init.batch.setColor(new Color((1F/TILES_PER_CHUNK) * tileY,0,block,1));
                    Init.batch.draw(img, block + (tileX*TILE_SIZE)+(chunkX*CHUNK_SIZE),(tileY*TILE_SIZE)+(chunkY*CHUNK_SIZE),1, TileMap.map[chunkX][chunkY][tileX][tileY].getHeight(block));

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
                    Init.batch.draw(img, (tileX*TILE_SIZE)+(chunkX*CHUNK_SIZE) + (TILE_SIZE - TileMap.map[chunkX][chunkY][tileX][tileY].getWidth(block)),(tileY*TILE_SIZE)+(chunkY*CHUNK_SIZE) - block,TileMap.map[chunkX][chunkY][tileX][tileY].getWidth(block),1);

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
        img.dispose();
        playerImg.dispose();
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
