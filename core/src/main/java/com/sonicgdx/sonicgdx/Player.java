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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * This is the class that handles player movement, player collision with the ground as well as player collision
 * with other objects.
 * It is final since it is not necessary to extend this class.
 */
public final class Player extends Entity {
    private boolean flipX = false, flipY = false;
    private boolean debugMode = false, isGrounded, isJumping;
    private final float ACCELERATION = 168.75F, AIR_ACCELERATION = 337.5F, SLOPE_FACTOR = 7.5F, GRAVITY_FORCE = -787.5F;
    private final int DECELERATION = 1800, MAX_SPEED = 360, JUMP_FORCE = 390;
    // An FPS of 60 was used to obtain the adjusted values
    // Original: ACCELERATION = 0.046875F, DECELERATION = 0.5F, DEBUG_SPEED = 1.5F, MAX_SPEED = 6, SLOPE_FACTOR = 0.125, AIR_ACCELERATION = 0.09375F, GRAVITY_FORCE = 0.21875F;
    // Original values were designed to occur 60 times every second so by multiplying it by 60 you get the amount of pixels moved per second.
    private float groundVelocity = 0, groundAngle = 0;
    private final Sensor sensorA, sensorB, sensorE,sensorF;
    private TextureRegion spriteRegion;
    private final Vector2 velocity;
    Player(float widthRadius, float heightRadius) {
        super(widthRadius, heightRadius);
        spriteRegion = GameScreen.getTextureRegion("sonic-idle",0);
        position = new Vector2(50,200); // Sets the player's starting position at (50,200). (The variable was initialised in super constructor)
        //The vector has two components for the x position and y position respectively
        velocity = new Vector2(); //Initialises to zero starting speed
        sensorA = new Sensor(); //Copies the player's position to the left floor sensor's.
        sensorB = new Sensor(); //Copies the player's position but placed at the sprite's right instead of left.
        sensorE = new Sensor(); //Copies the player's position but placed at the middle y position instead of the bottom
        sensorF = new Sensor(); //Copies the player's position but placed at the middle y position instead of the bottom and at the sprite's right instead of left.
        calculateSensorPositions();
    }

    //TODO Tommy Ettinger's digital extension could be used for faster operations on GWT


    /**
     * @param delta time since last frame. Used to make physics similar to how they would be at 60FPS
     * even with higher, lower or varying frame rates.
     * @see GameScreen#render(float)
     */
    public void update(float delta)
    {
        //TODO Would be better to implement an InputProcessor. This makes more sense as an interrupt rather than constant polling.
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q))
        {
            //Toggle debug mode
            debugMode = !debugMode;

            //Reset movement variables when entering or exiting debug mode to prevent oddities in physics.
            groundVelocity = 0;
            velocity.setZero();
            groundAngle = 0;

            //TODO acceleration in debug mode
        }
        if (debugMode) {
            debugMove(delta);
        }
        else {

            if (!isGrounded) {
                //air state
                airMove(delta);
                setAirSensors();

                //if (sensorE.getActive()) sensorE.wallProcess();
                if (sensorF.getActive()) sensorF.wallProcess();

                //since positive distances would mean the player is outside the detected tile, they are not accepted
                if (sensorF.getDistance() < 0) {
                    //testing only one sensor at this point to see if the basic collision works
                    wallCollision(sensorF);
                }

            }

            else {
                groundMove(delta);

                sensorA.setActive(true);
                sensorB.setActive(true);

                //Updates player position
                position.x += velocity.x * delta;
                position.y += velocity.y * delta;

            }

            if (sensorA.getActive() && sensorB.getActive()) {

                Sensor winningSensor = floorSensors();

                if (isGrounded) {
                    //checks that the sensor distance is in a valid range before correcting the player's position
                    if (winningSensor != null && Math.max(-Math.abs(velocity.x) - 4, -14) < winningSensor.getDistance() && winningSensor.getDistance() < 14) groundCollision(winningSensor); //TODO comment out this line first if there are physics bugs.
                    else isGrounded = false;
                }
                else{
                    if (Math.abs(velocity.x) >= Math.abs(velocity.y)) {
                        if (velocity.x > 0) { //going mostly right
                            if (winningSensor != null && winningSensor.getDistance() >= 0 && velocity.y <= 0) groundCollision(winningSensor);
                        }
                        else { //going mostly left
                            if (winningSensor != null && winningSensor.getDistance() >= 0 && velocity.y <= 0) groundCollision(winningSensor);
                        }
                    }
                    else {
                        if (velocity.y > 0) { //going mostly up

                        }
                        else { //going mostly down
                            if (winningSensor != null && winningSensor.getDistance() >= 0 && (sensorA.getDistance() <= -(velocity.y + 8) || sensorB.getDistance() >= -(velocity.y + 8))) groundCollision(winningSensor);
                        }
                    }
                }
            }
            //TODO perhaps add a check if the player is stationary before calculating collision to increase FPS

        }

        enforceBoundaries();

        calculateSensorPositions();

        //if (speedX == 0 && speedY == 0 && isGrounded) spriteRegion = GameScreen.getTextureRegion("sonic-idle",0);

        sprite.setRegion(spriteRegion);

        //FIXME rotation

        //Rotates the sprite first, and THEN changes its co-ordinates - translating it
        sprite.setRotation(groundAngle);

        //TODO calculate y Position from ground up
        sprite.setBounds(position.x - ((spriteRegion.getRegionWidth() + 1) / 2F),bottomEdgeY, spriteRegion.getRegionWidth(), spriteRegion.getRegionHeight());
        //Since the xPos is the centre, you can just subtract the difference between the first pixel and the middle pixel to get the sprite co-ordinates.
        //yPos is also the centre, but bottomEdgeY is used instead since sprites don't have constant height and positioning above the ground can be inconsistent.
        sprite.setOriginCenter(); //TODO only set origin when region, also perhaps look into setOriginBasedPosition

        sprite.flip(flipX,flipY);
        //FIXME possible approach https://www.reddit.com/r/libgdx/comments/i0plt4/comment/fzrlqqt

    }

    private void groundMove(float delta) {

        //These booleans are true if any of the inputs which cause their respective action are pressed
        //or held down in the current frame

        //"Move Right" action
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.D) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        //"Move Left" action
        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.A) || (Gdx.input.isKeyPressed(Input.Keys.LEFT));
        //"Jump" action
        boolean jumpJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

        if (groundVelocity != 0) groundVelocity -= delta * SLOPE_FACTOR * MathUtils.sinDeg(groundAngle); //TODO this only happens when the player is not in ceiling mode.

        //Moving right and moving left are mutually exclusive - if both are true, the outcome
        //is the same as if both are false

        if (rightPressed && !leftPressed) // if moving right
        {
            flipX = false;
            if (groundVelocity < 0) groundVelocity += (DECELERATION * delta); // Deceleration acts in the opposite direction to the one in which the player is currently moving.
            else if (groundVelocity < MAX_SPEED) groundVelocity += (ACCELERATION * delta); //Takes 128 frames to accelerate from 0 to 6 - exactly 2 seconds
        }
        else if (leftPressed && !rightPressed) // if moving left
        {
            flipX = true;
            if (groundVelocity > 0) groundVelocity -= (DECELERATION * delta);
            else if (groundVelocity > -MAX_SPEED) groundVelocity -= ACCELERATION * delta;
        }
        else groundVelocity -= Math.min(Math.abs(groundVelocity), ACCELERATION * delta) * Math.signum(groundVelocity); // friction if not pressing any directions
        // Decelerates until the ground speed is lower than the acceleration value (which doubles as the friction value) and then stops

        velocity.set(groundVelocity * MathUtils.cosDeg(groundAngle), groundVelocity * MathUtils.sinDeg(groundAngle));

        if (jumpJustPressed) jump(delta); //TODO placement different from original, may cause bugs.


        //FIXME player momentum functions oddly when landing after jumping downwards from a steep slope
    }

    /**
     * Collides with the nearest floor within a certain limit by adjusting the player's y position appropriately.
     * The positive limit is always 14, but the negative limit only becomes more lenient as the player's speed increases.
     * Limits of -16<=x<=16 are not used as those distances are likely too far away from the player to matter.
     * Uses angle for rotation and speed of the player and for player slope physics. TODO
     * Applies unique calculation to find minimum value, from Sonic 2 depending on the player's speed.
     * @return NULLABLE "Winning Distance" sensor.
     * Returns null in the condition that the sensor distances are equal but their respective returnTiles are different -
     * this prevents the groundAngle being changed and the player rotating haphazardly.
     */
    public Sensor floorSensors()
    {
        //Checks below and potentially above the positions of both sensors to find the nearest tile if one is present.
        sensorA.floorProcess();
        sensorB.floorProcess();

        /*
        Returns the sensor that had found the greater distance.
        Note that even if it returns a sensor it may not have a valid distance.
        The validation happens outside this method.
        */
        if(sensorA.getDistance() > sensorB.getDistance()) return sensorA;
        else if (sensorA.getDistance() < sensorB.getDistance()) return sensorB;
        //If sensorB could be returned in this case it would not make a difference - the sensors are essentially the same.
        else if (sensorA.getTile() == sensorB.getTile()) return sensorA; //FIXME comment out this line first if there are physics bugs.
        else return null;

    }

    public void groundCollision(Sensor sensor)
    {
        /*
        Corrects the player's Y position according to the distance found by the sensor.
        This should place them at the same height as the found tile after the frame is drawn, responding to the collision.
        Note that the player might not be on top of a surface even after this - this line may run multiple times if they
        are inside the floor to push them upwards and out of it.
        */
        position.y += sensor.getDistance();

        /*
        In the special case that the Tile has an angle of 360 (the 'flagged' angle)
        the player's current angle is rounded to the nearest 90 degrees.
        An example of a use case is the peak of a ramp which sends the player directly upwards when they run off it.
        */
        if (groundAngle == 360) {
            groundAngle = snapToNearest(groundAngle,90);
        }

        //Otherwise, sets the player's ground angle to that of the tile found by the sensor.
        else groundAngle = sensor.getTile().angle; //TODO possibly apply this to enemies?

        /*
        This block is run when the player lands onto the ground from the air (e.g. after jumping).
        Its purpose is to apply the same momentum that the player had whilst in the air, but adjusted
        depending on the angle of the ground.
        */
        if (!isGrounded) {
            if (0 <= groundAngle && groundAngle <= 23) groundVelocity = velocity.x;
                //TODO when mirrored... https://info.sonicretro.org/SPG:Slope_Physics#When_Falling_Downward
            else if (24 <= groundAngle && groundAngle <= 45) {
                if (Math.abs(velocity.x) >= Math.abs(velocity.y)) {
                    groundVelocity = velocity.x;
                } else {
                    groundVelocity = velocity.y * 0.5F * -MathUtils.sinDeg(groundAngle);
                }
            }
            else if (46 <= groundAngle && groundAngle <= 90) {
                if (Math.abs(velocity.x) >= Math.abs(velocity.y)) {
                    groundVelocity = velocity.x;
                } else {
                    groundVelocity = velocity.y * -MathUtils.sinDeg(groundAngle);
                }
            }
            isGrounded = true;
            if (isJumping) isJumping = false;
        }
    }

    /**
     * @param sensor the sensor that has collided with a wall.
     * (Distances have been generated beforehand)
     */
    public void wallCollision(Sensor sensor) {
        //the distance is the difference between the tile's x position and the sensor's
        //since only a negative distance is accepted, the player will be pushed backwards by this amount
        position.x += sensor.getDistance();
    }

    /**
     * @param delta time since last frame. Used to make physics similar to how they would be at 60FPS
     * even with higher, lower or varying frame rates.
     */
    public void jump(float delta) {
        //FIXME bug when jumping while moving downhill on a slope
        velocity.x -= JUMP_FORCE * MathUtils.sinDeg(groundAngle);
        velocity.y += JUMP_FORCE * MathUtils.cosDeg(groundAngle);
        isGrounded = false; isJumping = true;
        //TODO if time is available, jump buffering and coyote time
    }

    public void airMove(float delta) {
        //These booleans are true if any of the inputs which cause their respective action are pressed
        //or held down in the current frame

        //"Move Right" action
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.D) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        //"Move Left" action
        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.A) || (Gdx.input.isKeyPressed(Input.Keys.LEFT));
        //"Jump" action
        boolean jumpPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        //Reduce the height jumped by capping the Y Speed if player releases the jump button (Space) early.
        if (!jumpPressed && velocity.y > 4 && isJumping) velocity.y = 4;

        //Air acceleration
        if (rightPressed && !leftPressed) // if moving right
        {
            flipX = false;
            if (velocity.x < MAX_SPEED) velocity.x += AIR_ACCELERATION * delta; // accelerates right at twice the speed compared to on ground (no friction)
        }
        else if (leftPressed && !rightPressed) // if moving left
        {
            flipX = true;
            if (velocity.x > -MAX_SPEED) velocity.x -= AIR_ACCELERATION * delta; // accelerates left at twice the speed compared to on ground (no friction)
        }
        //Air drag
        if (0 < velocity.y && velocity.y < 4)
        {
            velocity.x -= (MathUtils.floor(velocity.x / 0.125F) / 256F * 60F * delta); //TODO Maybe use 60 * delta in all calculations instead of applying it to variable. For readability
        }

        //Updates player position
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        //Gravity - a force pushing the player down when they are in the air
        velocity.y += GRAVITY_FORCE * delta;
    }

    /**
     * Note: The && operator uses short-circuit evaluation (as opposed to &) so will only evaluate the left hand side of the boolean expression is true. This means that
     * if the returned value is null it won't check its distance so won't throw a NullPointerException.
     */
    public void setAirSensors(){
        //TODO insert sensorC and sensorD
        if (Math.abs(velocity.x) >= Math.abs(velocity.y)) {
            //In both cases the ground sensors will be checked
            sensorA.setActive(true);
            sensorB.setActive(true);
            if (velocity.x > 0) { //going mostly right

                sensorE.setActive(false);
                sensorF.setActive(true);
            }
            else { //going mostly left

                sensorE.setActive(true);
                sensorF.setActive(false);
            }
        }
        else {
            //In both cases the wall sensors will be checked
            sensorE.setActive(true);
            sensorF.setActive(true);
            if (velocity.y > 0) { //going mostly up
                sensorA.setActive(false);
                sensorB.setActive(false);
            }
            else { //going mostly down
                sensorA.setActive(true);
                sensorB.setActive(true);
            }
        }
    }

    /**
     * Sets the sensor positions relative to the player's position.
     * sensorA is positioned in the bottom left corner and sensorB in the bottom right corner.
     * sensorE is positioned at the centre left and sensorF is positioned at the centre right.
     */
    public void calculateSensorPositions() {
        super.calculateCornerPositions();
        sensorA.setPositionValues(leftEdgeX,bottomEdgeY);
        sensorB.setPositionValues(rightEdgeX,bottomEdgeY);
        sensorE.setPositionValues(leftEdgeX,position.y);
        sensorF.setPositionValues(rightEdgeX,position.y);
    }

    private void debugMove(float delta) {
        final int DEBUG_SPEED = 90;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) position.x += (DEBUG_SPEED * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) position.x -= (DEBUG_SPEED * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) position.y += (DEBUG_SPEED * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) position.y -= (DEBUG_SPEED * delta);
        //DEBUG key for testing sprite rotation
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) groundAngle += 45;
        //Gdx.app.debug("deltaTime",String.valueOf(delta));
    }

    public float getXPosition() {
        return position.x;
    }
    public float getYPosition() {
        return position.y;
    }
}
