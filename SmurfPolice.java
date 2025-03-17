import greenfoot.*;  // Import the Greenfoot library for game development.

/**
 * The SmurfPolice class, a subclass of Vehicle.
 */
public class SmurfPolice extends Vehicle
{
    GreenfootImage smurfPolice = new GreenfootImage("smurfPolice.png");  // Create a GreenfootImage object for SmurfPolice's image.
    
    /**
     * The act method is called on each game update frame.
     */
    public void act()
    {
        super.act();  // Call the act method of the superclass (Vehicle).
        smurfPolice.scale(170, 140);  // Scale SmurfPolice's image to a fixed size.
        setImage(smurfPolice);  // Set SmurfPolice's image.
        
        if (getWorld() == null){
            return;  // If SmurfPolice is not in a world, exit the act method.
        }
    
        Pedestrian pedestrian = (Pedestrian) getOneIntersectingObject(Pedestrian.class);  // Check if SmurfPolice intersects with a Pedestrian.
        if (pedestrian != null) {
            healPedestrian(pedestrian);  // Call the healPedestrian method to heal the pedestrian.
        }
    }
    
    /**
     * Constructor for the SmurfPolice class.
     * @param origin The VehicleSpawner that created this SmurfPolice instance.
     */
    public SmurfPolice(VehicleSpawner origin) {
        super(origin); // Call the constructor of the superclass (Vehicle).
        yOffset = 35;  // Set the vertical offset from the ground.
        
        // Check if SmurfPolice is touching a Snowstorm. If so, set the speed to 0.
        if (this.getWorld() != null && isTouching(Snowstorm.class)) {
            speed = 0;
        } else {
            maxSpeed = 3;  // Set the maximum speed of SmurfPolice.
            speed = maxSpeed;  // Set the initial speed to the maximum speed.
        }
    }
    
    /**
     * Check if SmurfPolice has hit Gargamel and rotate Gargamel.
     * @return true if a hit occurred, false otherwise.
     */
    public boolean checkHitPedestrian () {
        Gargamel gargamel = (Gargamel) getOneObjectAtOffset((int)speed + getImage().getWidth()/2, 0, Gargamel.class);
        if (gargamel != null)
        {
            gargamel.setRotation(0);  // Rotate Gargamel.
            return true;  // Indicate a successful hit.
        }
        return false;  // No hit occurred.
    }
    
    /**
     * Heal a pedestrian if they are not awake.
     * @param pedestrian The pedestrian to heal.
     */
    public void healPedestrian(Pedestrian pedestrian) {
        if (pedestrian != null && !pedestrian.isAwake()) {
            pedestrian.healMe();  // Call the healMe method of the pedestrian.
        }
    }
}
