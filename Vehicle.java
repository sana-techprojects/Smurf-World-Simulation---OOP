import greenfoot.*;  // Import necessary Greenfoot classes.
import java.util.ArrayList;

/**
 * This is the superclass for Vehicles.
 */
public abstract class Vehicle extends SuperSmoothMover
{
    protected double maxSpeed; // Maximum speed of the vehicle.
    protected double speed; // Current speed of the vehicle.
    protected int direction; // 1 = right, -1 = left, indicating the vehicle's direction of movement.
    protected boolean moving; // Indicates if the vehicle is currently moving.
    protected boolean isNew; // Indicates if the vehicle is newly spawned.
    protected int yOffset; // Vertical offset for positioning the vehicle.
    protected VehicleSpawner origin; // Reference to the VehicleSpawner that spawned the vehicle.
    protected int followingDistance; // Distance to maintain from the vehicle ahead.
    protected int myLaneNumber; // The lane number in which the vehicle is placed.

    // Just added for snowstorm
    private boolean isSnowstormActive = false;

    // Just added
    protected int possibleLane1; // The possible lane to change to (left).
    protected int possibleLane2; // The possible lane to change to (right).
    private boolean canChangeLane; // Indicates if the vehicle can change lanes.
    private int laneChangeCooldown; // Cooldown for lane changes.
    private GreenfootSound laneChangeSound; // Sound for lane changes.

    // Abstract method to check if the vehicle hit a pedestrian. To be implemented in subclasses.
    protected abstract boolean checkHitPedestrian();

    // Constructor for the Vehicle class.
    public Vehicle(VehicleSpawner origin) {
        this.origin = origin;
        laneChangeSound = new GreenfootSound("moveSound.mp3");
        moving = true;
        myLaneNumber = origin.getLaneNumber();
        
        if (origin.facesRightward()) { // Right-facing vehicles
            direction = 1;
        } else { // Left-facing vehicles
            direction = -1;
            getImage().mirrorHorizontally(); // Reverse the image for left-facing vehicles.
        }
        
        maxSpeed *= origin.getSpeedModifier();
        speed = maxSpeed;
        isNew = true;

        // Just added for lane changing
        canChangeLane = true;
        laneChangeCooldown = 0;
        possibleLane1 = -1;
        possibleLane2 = -1;
    }

    // Method to perform a lane change based on certain conditions.
    protected void performLaneChange() {
        if (laneChangeCooldown > 0) {
            laneChangeCooldown--;
            return;
        }

        if (checkLaneChangeCondition()) {
            if (myLaneNumber == 0) {
                possibleLane1 = 1; // Top lane can only change to the right
            } else if (myLaneNumber == 3) {
                possibleLane1 = 2; // Bottom lane can only change to the left
            } else {
                possibleLane1 = myLaneNumber - 1; // Middle lanes can check left
                possibleLane2 = myLaneNumber + 1; // and right lanes
            }

            if (checkLaneClear(possibleLane1)) {
                changeToLane(possibleLane1);
            } else if (possibleLane2 != -1 && checkLaneClear(possibleLane2)) {
                changeToLane(possibleLane2);
            }

            canChangeLane = false;
            laneChangeCooldown = 100; // Adjust the cooldown duration as needed
        }
    }

    // Method to check if the lane change condition is met.
    private boolean checkLaneChangeCondition() {
        Vehicle ahead = (Vehicle) getOneObjectAtOffset(direction * (int)(getImage().getWidth() / 2 + 6), 0, Vehicle.class);
        if (ahead != null) {
            return speed < ahead.getSpeed(); // Adjust this condition as needed for lane change.
        }
        return false;
    }

    // Method to check if a lane is clear for lane change.
    private boolean checkLaneClear(int laneNumber) {
        int offset = 0; // Adjust this value to set how far ahead to check for lane clearance.
        Vehicle vehicleInLane = (Vehicle) getOneObjectAtOffset(0, laneNumber * yOffset + offset, Vehicle.class);
        return vehicleInLane == null; // The lane is clear if there is no vehicle in it.
    }

    // Method to move to the specified lane.
    private void changeToLane(int newLane) {
        int newY = origin.getY() - (newLane * yOffset);
        laneChangeSound.setVolume(100);
        laneChangeSound.play();
        setLocation(getX(), newY);
        myLaneNumber = newLane; // Update the lane number.
    }

    /**
     * Method called automatically when the Vehicle is added to the World, placing it off-screen for a smooth entrance.
     */
    public void addedToWorld(World w) {
        if (isNew) {
            setLocation(origin.getX() - (direction * 100), origin.getY() - yOffset);
            isNew = false;
        }
    }

    // Method to set the snowstorm status.
    public void setSnowstormActive(boolean isActive) {
        isSnowstormActive = isActive;
    }

    // Method to get the snowstorm status.
    public boolean getSnowstormStatus() {
        return isSnowstormActive;
    }

    // Method to speed up the vehicle.
    public void speedUp(double speed) {
        maxSpeed += speed;
    }

    // The superclass Vehicle's act() method.
    public void act() {
        drive();
        if (!checkHitPedestrian()) {
            repelPedestrians();
        }
        if (checkEdge()) {
            getWorld().removeObject(this);
            return;
        }
    }

    // Method to check if the vehicle is at the edge of the world.
    protected boolean checkEdge() {
        if (direction == 1) {
            if (getX() > getWorld().getWidth() + 200) { // Check if the vehicle is moving right and beyond the max X.
                return true;
            }
        } else {
            if (getX() < -200) { // Check if the vehicle is moving left and beyond negative values.
                return true;
            }
        }
        return false;
    }

    // Method to repel pedestrians from the vehicle (work in progress).
    public void repelPedestrians() {
        ArrayList<Pedestrian> pedsTouching = (ArrayList<Pedestrian>)getIntersectingObjects(Pedestrian.class);
        pushAwayFromObjects(pedsTouching, this.getImage().getHeight() / 2);
    }

    // Method for repelling pedestrians (primary method, work in progress).
    public void pushAwayFromObjects(ArrayList<Pedestrian> nearbyObjects, double minDistance) {
        int currentX = getX();
        int currentY = getY();

        for (Pedestrian object : nearbyObjects) {
            if (!object.isAwake()) {
                continue;
            }

            int objectX = object.getX();
            int objectY = object.getY();
            double distance = Math.sqrt(Math.pow(currentX - objectX, 2) + Math.pow(currentY - objectY, 2));

            if (distance < minDistance) {
                int deltaX = objectX - currentX;
                int deltaY = objectY - currentY;
                double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                double unitX = deltaX / length;
                double unitY = deltaY / length;
                double pushAmount = minDistance - distance;

                object.setLocation(objectX, objectY + (int)(pushAmount * unitY));
            }
        }
    }

    // Method to handle vehicle movement and adjustments based on different conditions.
    public void drive() {
        if (isSnowstormActive) {
            // Adjust speed when snowstorm is active
            speed = 0.5; // Change the factor as needed
        } else {
            Vehicle ahead = (Vehicle) getOneObjectAtOffset(direction * (int)(speed + getImage().getWidth()/2 + 6), 0, Vehicle.class);
            double otherVehicleSpeed = -1;
            if (ahead != null) {
                performLaneChange();
                otherVehicleSpeed = ahead.getSpeed();
            }
    
            if (otherVehicleSpeed >= 0 && otherVehicleSpeed < maxSpeed) {
                speed = otherVehicleSpeed;
            } else {
                speed = maxSpeed;
            }
        }
    
        move(speed * direction);
    }

    // Accessor method to get the vehicle's speed.
    public double getSpeed() {
        if (moving) {
            return speed;
        }
        return 0;
    }
}
