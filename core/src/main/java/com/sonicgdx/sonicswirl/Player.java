package com.sonicgdx.sonicswirl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

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
    private final Sensor sensorA, sensorB, sensorE,sensorF;
    Player(Texture image, int width, int height) {
        super(image, width, height);
        xPos = 200; yPos = 200; // Player starts at (600,200);
        sensorA = new Sensor(xPos,yPos);
        sensorB = new Sensor(xPos + (sprite.getWidth() - 1),yPos);
        sensorE = new Sensor(xPos,yPos + (sprite.getHeight() - 1) / 2);
        sensorF = new Sensor(xPos + (sprite.getWidth() - 1),yPos + (sprite.getHeight() - 1) / 2);
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
            //Toggle debug mode
            debugMode = !debugMode;

            //Reset movement variables when entering or exiting debug mode to prevent oddities in physics.
            groundSpeed = 0;
            speedX = 0;
            speedY = 0;
            groundAngle = 0;

            //TODO acceleration in debug mode
        }
        if (debugMode) {
            debugMove(delta);
        }
        else {
            //FIXME Right now, right movement is prioritised if both directions are pressed at the same time. Consider cancelling them out.

            if (!isGrounded) {
                airMove(delta);
                setAirSensors();
            }

            else {
                groundMove(delta);

                sensorA.setActive(true);
                sensorB.setActive(true);

                //Updates player position
                xPos += speedX * delta;
                yPos += speedY * delta;

            }

            if (sensorA.getActive() && sensorB.getActive()) {

                Sensor winningSensor = floorSensors();

                if (isGrounded) {
                    if (winningSensor != null && Math.max(-Math.abs(speedX) - 4, -14) < winningSensor.getDistance() && winningSensor.getDistance() < 14) groundCollision(winningSensor); //TODO comment out this line first if there are physics bugs.
                    else isGrounded = false;
                }
                else{
                    if (Math.abs(speedX) >= Math.abs(speedY)) {
                        if (speedX > 0) { //going mostly right
                            if (winningSensor != null && winningSensor.getDistance() >= 0 && speedY <= 0) groundCollision(winningSensor);
                        }
                        else { //going mostly left
                            if (winningSensor != null && winningSensor.getDistance() >= 0 && speedY <= 0) groundCollision(winningSensor);
                        }
                    }
                    else {
                        if (speedY > 0) { //going mostly up

                        }
                        else { //going mostly down
                            if (winningSensor != null && winningSensor.getDistance() >= 0 && (sensorA.getDistance() <= -(speedY + 8) || sensorB.getDistance() >= -(speedY + 8))) groundCollision(winningSensor);
                        }
                    }
                }

            }

            sensorE.wallProcess();
            sensorF.wallProcess();



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


        //FIXME player momentum functions oddly when landing after jumping downwards from a steep slope
    }

    /**
     * Collides with the nearest floor within a certain limit by adjusting the player's yPos appropriately.
     * The positive limit is always 14, but the negative limit only becomes more lenient as the player's speed increases.
     * Limits of -16<=x<=16 are not used as those distances are likely too far away from the player to matter.
     * Uses angle for rotation and speed of the player and for player slope physics. TODO
     * Applies unique calculation to find minimum value, from Sonic 2 depending on the player's speed.
     * @return "Winning Distance" sensor which could be null in the condition that the sensor distances are equal but their respective returnTiles are different.
     */
    public Sensor floorSensors()
    {
        sensorA.floorProcess();
        sensorB.floorProcess();

        if(sensorA.getDistance() > sensorB.getDistance()) return sensorA;
        else if (sensorA.getDistance() < sensorB.getDistance()) return sensorB;
        else if (sensorA.getTile() == sensorB.getTile()) return sensorA; //FIXME comment out this line first if there are physics bugs.
        else return null;

    }

    public void groundCollision(Sensor sensor)
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

    /**
     * @param delta time since last frame. Used to make physics similar to how they would be at 60FPS
     * at different frame rates.
     */
    public void jump(float delta) {
        //FIXME bug when jumping while moving downhill on a slope
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
            if (speedX < MAX_SPEED) speedX += AIR_ACCELERATION * delta; // accelerates right at twice the speed compared to on ground
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A) || (Gdx.input.isKeyPressed(Input.Keys.LEFT))) // if moving left
        {
            if (speedX > -MAX_SPEED) speedX -= AIR_ACCELERATION * delta; // accelerates left at twice the speed compared to on ground
        }
        //Air drag
        if (0 < speedY && speedY < 4)
        {
            speedX -= (MathUtils.floor(speedX / 0.125F) / 256F * 60F * delta); //TODO Maybe use 60 * delta in all calculations instead of applying it to variable. For readability
        }

        //Updates player position
        xPos += speedX * delta;
        yPos += speedY * delta;

        //Gravity - a force pushing the player down when they are in the air
        speedY += GRAVITY_FORCE * delta;
    }

    /**
     * && operator uses short-circuit evaluation (as opposed to &) so will only evaluate the left hand side of the boolean expression is true. This means that
     * if the returned value is null it won't check its distance so won't throw a NullPointerException.
     */
    public void setAirSensors(){
        //TODO insert sensorC and sensorD
        if (Math.abs(speedX) >= Math.abs(speedY)) {
            //In both cases the ground sensors will be checked
            sensorA.setActive(true);
            sensorB.setActive(true);
            if (speedX > 0) { //going mostly right

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
            if (speedY > 0) { //going mostly up
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
    @Override
    public void calculateSensorPositions() {
        super.calculateSensorPositions();
        sensorA.setPosition(lSensorX,yPos); //TODO possibly remove these variables
        sensorB.setPosition(rSensorX,yPos);
        sensorE.setPosition(lSensorX,centreY);
        sensorF.setPosition(rSensorX,centreY);
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
