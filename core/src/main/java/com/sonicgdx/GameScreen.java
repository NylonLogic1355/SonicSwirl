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

package com.sonicgdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.Optional;

public class GameScreen implements Screen {

    private final Game game;
    //private final FPSLogger frameLog;
    private final OrthographicCamera camera; private final Vector2 cameraOffset = Vector2.Zero; private final ExtendViewport gameViewport;
    private final Player player;

    public static TextureAtlas spriteAtlas;
    private final Texture whiteSquare, blackSquare;
    private final Texture backgroundTexture;
    private final Color backgroundTint;

    private ChunkRenderMode chunkRenderMode = ChunkRenderMode.CHUNK_TEXTURE;

    private final Music backgroundMusic;

    /**
     * The available selections for drawing chunks and their tiles to the screen at their co-ordinates.
     * <br>
     * CHUNK_TEXTURE - default mode which just draws each chunk's given texture if it has one.
     * <br>
     * TILE_WIDTH_ARRAY - draws a representation of the width arrays of tiles in each chunk.
     * <br>
     * TILE_HEIGHT_ARRAY - draws a representation of the height arrays of tiles in each chunk.
     */
    private enum ChunkRenderMode {
        CHUNK_TEXTURE,
        TILE_WIDTH_ARRAY,
        TILE_HEIGHT_ARRAY
    }

    public GameScreen(final Game game) {

        this.game = game;

        //TODO implement class with reference to https://gamedev.stackexchange.com/a/133593
        //FIXME possibly reduce viewport resolution to reduce pixels being missing at lower resolutions or change viewport type

        camera = new OrthographicCamera(); // 3D camera which projects into 2D so that the view is flat
        gameViewport = new ExtendViewport(1280,720,camera);
        camera.setToOrtho(false,1280,720); // Even if the device has a scaled resolution, the in game view will still be 1280x720

        spriteAtlas = new TextureAtlas(Gdx.files.internal("sprites/SonicGDX.atlas"));

        //TODO AssetManager
        whiteSquare = new Texture(Gdx.files.internal("sprites/1x1-ffffffff.png")); blackSquare = new Texture(Gdx.files.internal("sprites/1x1-000000ff.png"));
        player = new Player(9,19);

        //TODO adjust view when looking up or down (pressing up or down arrows)
        cameraOffset.set(0, camera.position.y - player.getYPosition());

        //frameLog = new FPSLogger();
        backgroundTexture = new Texture(Gdx.files.internal("sprites/aiz_background.jpg"));

        //randomly picks a color to tint the background with (to make it less bland over multiple playthroughs)
        //for development only
        //selected colors are closer to white (no tint) than black
        backgroundTint = new Color (
            MathUtils.randomTriangular(0, 1, 1),
            MathUtils.randomTriangular(0, 1, 1),
            MathUtils.randomTriangular(0, 1, 1),
            1);

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/aiz_loop.wav"));
        backgroundMusic.setVolume(0.3f);
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

    }

    @Override
    public void render(final float delta) {
        //UNCOMMENT for debugging purposes
        //frameLog.log();
        //delta = 0.016666668f;

        ScreenUtils.clear(Color.DARK_GRAY);

        //Toggle between one of three draw modes: texture drawing, height array drawing and width array drawing
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            chunkRenderMode = ChunkRenderMode.CHUNK_TEXTURE;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            chunkRenderMode = ChunkRenderMode.TILE_WIDTH_ARRAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            chunkRenderMode = ChunkRenderMode.TILE_HEIGHT_ARRAY;
        }

        player.update(delta);

        //Updates the camera position to where the player is but keeps the offset
        camera.position.set(player.getXPosition() + cameraOffset.x,player.getYPosition() + cameraOffset.y,camera.position.z); camera.update();

        //viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        /*Disabling blending gives a performance boost
        and transparency is not needed for the background image */
        game.batch.disableBlending();
        //tints only the backgroundTexture with a random color
        game.batch.setColor(backgroundTint);
        game.batch.draw(backgroundTexture,camera.position.x - (camera.viewportWidth / 2),camera.position.y - (camera.viewportHeight / 2));
        game.batch.setColor(Color.WHITE);

        game.batch.enableBlending();
        //TODO render gradually as player progresses

        //Iterates through every chunk on the x-axis
        for (int chunkX = 0; chunkX < TileMap.map.length; chunkX++)
        {
            //Iterates through every chunk on the y-axis
            for (int chunkY = 0; chunkY < TileMap.map[chunkX].length; chunkY++)
            {
                //Draws using the respective mode's method
                switch(chunkRenderMode) {
                    case CHUNK_TEXTURE:
                        drawChunkTexture(chunkX, chunkY);
                        break;
                    case TILE_HEIGHT_ARRAY:
                        drawChunkHeightArray(chunkX, chunkY);
                        break;
                    case TILE_WIDTH_ARRAY:
                        drawChunkWidthArray(chunkX, chunkY);
                        break;
                    default:
                        throw new AssertionError("No case for chunk rendering mode " + chunkRenderMode.name());
                }
            }
        }
        player.sprite.draw(game.batch);
        // DEBUG - draw 1x1 white squares at the player's sensor locations
        game.batch.draw(whiteSquare,player.leftEdgeX,player.bottomEdgeY);
        game.batch.draw(whiteSquare,player.rightEdgeX,player.bottomEdgeY);

        game.batch.draw(whiteSquare,player.leftEdgeX,player.getYPosition());
        game.batch.draw(whiteSquare,player.rightEdgeX,player.getYPosition());

        game.batch.draw(whiteSquare,player.leftEdgeX,player.topEdgeY);
        game.batch.draw(whiteSquare,player.rightEdgeX,player.topEdgeY);

        game.batch.draw(whiteSquare,player.getXPosition(),player.getYPosition());
        game.batch.end();
    }

    /**
     * Draws each Chunk's assigned texture at its corresponding location
     * @param chunkX the chunk number on the x-axis - not the same as its co-ordinate
     * @param chunkY the chunk number on the y-axis - not the same as its co-ordinate
     */
    public void drawChunkTexture(final int chunkX, final int chunkY) {
        final Chunk chunk = TileMap.getChunk(chunkX, chunkY);

        //If there is a chunk at this location and it has a texture, draw it.
        if (!chunk.isEmpty()) {
            final Optional<Texture> texture = chunk.getTexture();

            game.batch.draw(
                texture.orElseThrow(),
                (chunkX * TileMap.CHUNK_LENGTH),
                (chunkY * TileMap.CHUNK_LENGTH),
                TileMap.CHUNK_LENGTH,
                TileMap.CHUNK_LENGTH);
        }
    }

    /**
     * Draws each Tile in a Chunk's height arrays  using a gradient - for debugging purposes only
     * Further iteration is done outside the procedure for every chunk in the TileMap. This is so that this method
     * can potentially be reused in other circumstances (such as for rendering only one chunk in the creator UI)
     * @param chunkX the chunk number on the x-axis - not the same as its co-ordinate
     * @param chunkY the chunk number on the y-axis - not the same as its co-ordinate
     */
    public void drawChunkHeightArray(final int chunkX, final int chunkY) {
        //Iterates through every tile in the chunk
        for (int tileX = 0; tileX < TileMap.TILES_PER_CHUNK; tileX++)
        {
            for (int tileY = 0; tileY < TileMap.TILES_PER_CHUNK; tileY++)
            {
                //Skips the loop for empty tiles (every value in its array would be zero anyway)
                if (TileMap.getTile(chunkX,chunkY,tileX,tileY).isEmpty()) continue;

                //Goes through every element in the height array
                for (int block = 0; block < TileMap.TILE_LENGTH; block++)
                {
                    //At the starting x of each tile, sets the colour to black
                    //to make it easy to see where each tile starts from
                    if (block == 0) game.batch.setColor(Color.BLACK);
                    //Otherwise, format the tile's colours with a red gradient for each tile's y position
                    //and a blue gradient for each element in the array
                    else game.batch.setColor((1F/ TileMap.TILES_PER_CHUNK) * tileY,0, block,1);

                    // Draws a block at the located at the co-ordinates of the tile (+ the array position for the x-axis only)
                    // with width 1 and the height obtained from the array
                    game.batch.draw(
                        whiteSquare,
                        block + (tileX* TileMap.TILE_LENGTH)+(chunkX* TileMap.CHUNK_LENGTH),
                        (tileY* TileMap.TILE_LENGTH)+(chunkY* TileMap.CHUNK_LENGTH),
                        1,
                        TileMap.getTile(chunkX,chunkY,tileX,tileY).getHeight(block));

                    //TODO reversed search order for flipped tiles. e.g. Collections.reverse() or ArrayUtils.reverse(int[] array)

                }
            }
        }
        game.batch.setColor(Color.WHITE); //Resets batch colour

    }

    /**
     * Draws each Tile in a Chunk's height array using a gradient - for debugging purposes only
     * Further iteration is done outside the procedure for every chunk in the TileMap. This is so that this method
     * can potentially be reused in other circumstances (such as for rendering only one chunk in the creator UI)
     * @param chunkX the chunk number on the x-axis - not the same as its co-ordinate
     * @param chunkY the chunk number on the y-axis - not the same as its co-ordinate
     */
    public void drawChunkWidthArray(final int chunkX, final int chunkY) {

        //Iterates through every tile in the chunk
        for (int tileX = 0; tileX < TileMap.TILES_PER_CHUNK; tileX++)
        {
            for (int tileY = 0; tileY < TileMap.TILES_PER_CHUNK; tileY++)
            {
                //Skips the loop for empty tiles (every value in its array would be zero anyway)
                if (TileMap.getTile(chunkX,chunkY,tileX,tileY).isEmpty()) continue;
                for (int block = 0; block < TileMap.TILE_LENGTH; block++)
                {
                    if (block==0) game.batch.setColor(Color.BLACK);
                    else game.batch.setColor(0,(1F/ TileMap.TILES_PER_CHUNK) * tileY, block,1);

                    final int yPosition = (tileY * TileMap.TILE_LENGTH) + (chunkY * TileMap.CHUNK_LENGTH) + block;
                    final int blockWidth = TileMap.getTile(chunkX,chunkY,tileX,tileY).getWidth(TileMap.TILE_LENGTH - block - 1);

                    if (!TileMap.getTile(chunkX,chunkY,tileX,tileY).isFlippedHorizontally()) {
                        game.batch.draw(
                            whiteSquare,
                            (tileX * TileMap.TILE_LENGTH) + (chunkX * TileMap.CHUNK_LENGTH) + (TileMap.TILE_LENGTH - blockWidth),
                            yPosition,
                            blockWidth,
                            1);
                    } else {
                        game.batch.draw(
                            whiteSquare,
                            (tileX * TileMap.TILE_LENGTH) + (chunkX * TileMap.CHUNK_LENGTH),
                            yPosition,
                            blockWidth,
                            1);
                    }

                    //TODO reversed search order for flipped tiles. e.g. Collections.reverse() or ArrayUtils.reverse(int[] array)

                }
            }
        }
        game.batch.setColor(Color.WHITE); //Resets batch colour

    }


    /**
     * Draws each Tile using a gradient - for debugging purposes only
     * @param chunkX the chunk number on the x-axis - not the same as its co-ordinate
     * @param chunkY the chunk number on the y-axis - not the same as its co-ordinate
     * @deprecated Superseded by drawChunkHeightArray as ShapeRenderer uses its own mesh compared to the SpriteBatch and therefore conflicts in the rendering method making it cumbersome to use.
     */
    @Deprecated
    public void drawChunkHeightShapeRenderer(final int chunkX, final int chunkY) {

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

                    //TODO reversed search order for flipped tiles. e.g. Collections.reverse() or ArrayUtils.reverse(int[] array)

                }
            }
        }*/
    }


    public static TextureRegion getTextureRegion(final String regionName) {
        return spriteAtlas.findRegion(regionName);
    }

    public static TextureRegion getTextureRegion(final String regionName, final int animationIndex) {
        return spriteAtlas.findRegion(regionName,animationIndex);
    }

    @Override
    public void dispose () {
        //TODO game objects should have their own dispose method which would be called from here

        backgroundTexture.dispose();
        spriteAtlas.dispose();

        player.dispose();

        whiteSquare.dispose();
        blackSquare.dispose();


    }
    @Override
    public void resize(final int width, final int height) {
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
