package com.sonicgdx.sonicswirl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.util.Optional;

/**
 * This is the class that handles player movement, player collision with the ground as well as player collision
 * with other objects.
 * It is final since it is not necessary to extend this class.
 */
public final class Player extends Entity {
    private boolean debugMode = false, isGrounded, isJumping;
    private final float ACCELERATION = 168.75F, AIR_ACCELERATION = 337.5F, SLOPE_FACTOR = 7.5F, GRAVITY_FORCE = -787.5F;
    private final int DECELERATION = 1800, MAX_SPEED = 360, JUMP_FORCE = 390;
    // An FPS of 60 was used to obtain the adjusted values
    // Original: ACCELERATION = 0.046875F, DECELERATION = 0.5F, DEBUG_SPEED = 1.5F, MAX_SPEED = 6, SLOPE_FACTOR = 0.125, AIR_ACCELERATION = 0.09375F, GRAVITY_FORCE = 0.21875F;
    // Original values were designed to occur 60 times every second so by multiplying it by 60 you get the amount of pixels moved per second.
    private float speedX = 0, speedY = 0, groundSpeed = 0, groundAngle = 0;
    private final FloorSensor sensorA, sensorB;
    private final WallSensor sensorE,sensorF;
    Player(Texture image, int width, int height) {
        super(image, width, height);
        xPos = 200; yPos = 200; // Player starts at (600,200);
        sensorA = new FloorSensor(xPos,yPos);
        sensorB = new FloorSensor(xPos + (sprite.getWidth() - 1),yPos);
        sensorE = new WallSensor(xPos,yPos + (sprite.getHeight() - 1));
        sensorF = new WallSensor(xPos + (sprite.getWidth() - 1),yPos + (sprite.getHeight() - 1));
    }

    //TODO Tommy Ettinger's digital extension could be used for faster operations on GWT


    /**
     * @param delta time since last frame. Used to make physics similar to how they would be at 60FPS
     * at different frame rates.
     * @see GameScreen#render(float)
     */
    public void move(float delta)
    {
        //TODO Would be better to implement an InputProcessor. This makes more sense as an interrupt rather than constant polling.
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q))
        {
            toggleDebugMode();
        }
        if (debugMode) {
            debugMove(delta);
        }
        else {
            //TODO Right now, right movement is prioritised if both directions are pressed at the same time. Consider cancelling them out.

            sensorE.process();
            sensorF.process();
            if (!isGrounded) {
                airMove(delta);
                airSensors();
            }

            else {
                groundMove(delta);
                if (isGrounded){
                    sensorA.process();
                    sensorB.process();

                    sensorA.setActive(Math.max(-Math.abs(speedX) - 4, -14) < sensorA.getDistance() && sensorA.getDistance() < 14);
                    sensorB.setActive(Math.max(-Math.abs(speedX) - 4, -14) < sensorB.getDistance() && sensorB.getDistance() < 14);

                    if(sensorA.getDistance() > sensorB.getDistance() && sensorA.getActive()) groundCollision(sensorA);
                    else if(sensorB.getActive() && sensorA.getDistance() < sensorB.getDistance()) groundCollision(sensorB);
                    else if(sensorA.getActive() && sensorB.getActive() && sensorA.getDistance() == sensorB.getDistance() && sensorA.getTile() == sensorB.getTile()) groundCollision(sensorA); //TODO comment out this line first if there are physics bugs.
                    else isGrounded = false;


                }

            }

            xPos += speedX * delta;
            yPos += speedY * delta;

            //TODO perhaps add a check if the player is stationary before calculating collision



        }

        enforceBoundaries();

        calculateSensorPositions();

        sprite.setPosition(xPos, yPos);
        sprite.setRotation(groundAngle);

    }

    private void groundMove(float delta) {
        if (groundSpeed != 0) groundSpeed -= delta * SLOPE_FACTOR * MathUtils.sinDeg(groundAngle); //TODO this only happens when the player is not in ceiling mode.

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

        speedX = groundSpeed * MathUtils.cosDeg(groundAngle);
        speedY = groundSpeed * MathUtils.sinDeg(groundAngle);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) jump(delta); //TODO placement different from original, may cause bugs.


    }

    /**
     * @param delta time since last frame. Used to make physics similar to how they would be at 60FPS
     * at different frame rates.
     */
    public void jump(float delta) {
        //TODO bug when jumping while moving downhill on a slope
        speedX -= JUMP_FORCE * MathUtils.sinDeg(groundAngle);
        speedY += JUMP_FORCE * MathUtils.cosDeg(groundAngle);
        isGrounded = false; isJumping = true;
        //TODO if time is available, jump buffering and coyote time
    }

    public void airMove(float delta) {
        //Reduce the height jumped by capping the Y Speed if player releases the jump button (Space) early.
        if (!Gdx.input.isKeyPressed(Input.Keys.SPACE) && speedY > 4 && isJumping) speedY = 4;

        //Air acceleration
        if (Gdx.input.isKeyPressed(Input.Keys.D) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT))) // if moving right
        {
            if (speedX < MAX_SPEED) speedX += AIR_ACCELERATION * delta;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A) || (Gdx.input.isKeyPressed(Input.Keys.LEFT))) // if moving left
        {
            if (speedX > -MAX_SPEED) speedX -= AIR_ACCELERATION * delta;
        }
        //Air drag
        if (0 < speedY && speedY < 4)
        {
            speedX -= (MathUtils.floor(speedX / 0.125F) / 256F * 60F * delta); //TODO Maybe use 60 * delta in all calculations instead of applying it to variable. For readability
        }
        //Gravity
        speedY += GRAVITY_FORCE * delta;
    }

    /**
     * && operator uses short-circuit evaluation (as opposed to &) so will only evaluate the left hand side of the boolean expression is true. This means that
     * if the returned value is null it won't check its distance so won't throw a NullPointerException.
     */
    public void airSensors(){
        if (Math.abs(speedX) >= Math.abs(speedY)) {
            if (speedX > 0) { //going mostly right
                FloorSensor winningSensor = floorSensors();
                if (winningSensor != null && winningSensor.getDistance() >= 0 && speedY <= 0) groundCollision(winningSensor);
            }
            else { //going mostly left
                FloorSensor winningSensor = floorSensors();
                if (winningSensor != null && winningSensor.getDistance() >= 0 && speedY <= 0) groundCollision(winningSensor);
            }
        }
        else {
            if (speedY > 0) { //going mostly up

            }
            else { //going mostly down
                FloorSensor winningSensor = floorSensors();
                if (winningSensor != null && winningSensor.getDistance() >= 0 && (sensorA.getDistance() <= -(speedY + 8) || sensorB.getDistance() >= -(speedY + 8))) groundCollision(winningSensor);
            }
        }
    }


    /**
     * Collides with the nearest floor within a certain limit by adjusting the player's yPos appropriately.
     * The positive limit is always 14, but the negative limit only becomes more lenient as the player's speed increases.
     * Limits of -16<=x<=16 are not used as those distances are likely too far away from the player to matter.
     * Uses angle for rotation and speed of the player and for player slope physics. TODO
     * Applies unique calculation to find minimum value, from Sonic 2 depending on the player's speed.
     * @return "Winning Distance" sensor which could be null in the condition that the sensor distances are equal but their respective returnTiles are different.
     */
    public FloorSensor floorSensors()
    {
        calculateSensorPositions();

        sensorA.process();
        sensorB.process();

        if(sensorA.getDistance() > sensorB.getDistance()) return sensorA;
        else if (sensorB.getDistance() > sensorA.getDistance()) return sensorB;
        else if (sensorA.getTile() == sensorB.getTile()) return sensorA;
        else return null;

    }

    public void groundCollision(FloorSensor sensor)
    {
        yPos += sensor.getDistance();

        if (groundAngle == 360) {
            groundAngle = snapToNearest(groundAngle,90);
        }
        else groundAngle = sensor.getTile().angle; //TODO possibly apply this to enemies?
        if (!isGrounded) {
            if (0 <= groundAngle && groundAngle <= 23) groundSpeed = speedX;
            //TODO when mirrored... https://info.sonicretro.org/SPG:Slope_Physics#When_Falling_Downward
            else if (24 <= groundAngle && groundAngle <= 45) {
                if (Math.abs(speedX) >= Math.abs(speedY)) {
                    groundSpeed = speedX;
                } else {
                    groundSpeed = speedY * 0.5F * -MathUtils.sinDeg(groundAngle);
                }
            }
            else if (46 <= groundAngle && groundAngle <= 90) {
                if (Math.abs(speedX) >= Math.abs(speedY)) {
                    groundSpeed = speedX;
                } else {
                    groundSpeed = speedY * -MathUtils.sinDeg(groundAngle);
                }
            }
            isGrounded = true;
            if (isJumping) isJumping = false;
        }
    }

    @Override
    public void calculateSensorPositions() {
        super.calculateSensorPositions();
        sensorA.setPosition(lSensorX,yPos); //TODO possibly remove these variables
        sensorB.setPosition(rSensorX,yPos);
    }

    private void toggleDebugMode() {
        debugMode = !debugMode;
        groundSpeed = 0;
        speedX = 0;
        speedY = 0;
        groundAngle = 0;
        //Gdx.app.debug("debugMode",String.valueOf(debugMode));
        //TODO acceleration in debug mode
    }

    private void debugMove(float delta) {
        final int DEBUG_SPEED = 90;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) xPos += (DEBUG_SPEED * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) xPos -= (DEBUG_SPEED * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) yPos += (DEBUG_SPEED * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) yPos -= (DEBUG_SPEED * delta);
        //Gdx.app.debug("deltaTime",String.valueOf(delta));
    }
}
