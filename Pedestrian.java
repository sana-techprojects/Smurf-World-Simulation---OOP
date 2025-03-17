import greenfoot.*;  // Import necessary Greenfoot classes.

/**
 * A Pedestrian that tries to walk across the street
 */
public abstract class Pedestrian extends SuperSmoothMover
{
    private double speed;  // Current speed of the pedestrian.
    private double maxSpeed;  // Maximum speed of the pedestrian.
    
    private int direction; // direction is always -1 or 1, for moving down or up, respectively
    protected boolean awake, entering;
    
    // Constructor for the Pedestrian class
    public Pedestrian(int direction) {
        // Choose a random speed for the pedestrian.
        maxSpeed = Math.random() * 2 + 1;
        speed = maxSpeed;
        // Start as awake.
        awake = true;
        entering = true;
        this.direction = direction;
    }

    /**
     * Act - do whatever the Pedestrian wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        checkHitGargamel();  // Check for collisions with a character named Gargamel.
        checkHitAzrael();    // Check for collisions with a character named Azrael.
        checkHitHackus();    // Check for collisions with a character named Hackus.
        
        // The Pedestrian is awake unless "knocked down."
        if (awake){
            // Check in the direction I'm moving vertically for a Vehicle,
            // and only move if there is no Vehicle in front of me.
            if (getOneObjectAtOffset(0, (int)(direction * getImage().getHeight()/2 + (int)(direction * speed)), Vehicle.class) == null){
                setLocation (getX(), getY() + (int)(speed * direction));
            }
            if (direction == -1 && getY() < 100){
                getWorld().removeObject(this);
            } else if (direction == 1 && getY() > getWorld().getHeight() - 30){
                getWorld().removeObject(this);
            }
        }
    }

    // Getter for the pedestrian's direction.
    public int getDirection() {
        return direction;
    }

    /**
     * Method to cause this Pedestrian to become knocked down - stop moving, turn onto side.
     */
    public void knockDown () {
        speed = 0;
        setRotation (direction * 90);
        awake = false;
    }

    /**
     * Method to allow a downed Pedestrian to be healed.
     */
    public void healMe () {
        speed = maxSpeed;
        setRotation (0);
        awake = true;
    }

    // Check if the pedestrian is awake or knocked down.
    public boolean isAwake () {
        return awake;
    }
    
    // Apply a wind push effect to the pedestrian.
    public void windPush(double speed)
    {
        if(!awake) return;
        double factor = 0.5 * ((double)Greenfoot.getRandomNumber(100) / 100);
        double movement = speed * factor;
        setLocation(getX() + movement, getY());
    }
    
    // Check for collisions with a character named Gargamel at specific points.
    public boolean checkHitGargamel() {
        Gargamel p1 = (Gargamel)getOneObjectAtOffset(98, 46, Gargamel.class);
        Gargamel p2 = (Gargamel)getOneObjectAtOffset(75, 26, Gargamel.class);
        
        if (p1 != null && p2 != null) {
            if (this.intersects(p1) || this.intersects(p2)) {
                this.knockDown();
                return true;
            }
        }
        return false;
    }
    
    // Check for collisions with a character named Azrael at specific points.
    public boolean checkHitAzrael() {
        CatAzrael p1 = (CatAzrael)getOneObjectAtOffset(309, 467, CatAzrael.class);
        CatAzrael p2 = (CatAzrael)getOneObjectAtOffset(523, 303, CatAzrael.class);
        CatAzrael p3 = (CatAzrael)getOneObjectAtOffset(99, 422, CatAzrael.class);
        CatAzrael p4 = (CatAzrael)getOneObjectAtOffset(19, 43, CatAzrael.class);
        
        if (p1 != null && p2 != null && p3 != null && p4 != null) {
            if (this.intersects(p1) || this.intersects(p2) || this.intersects(p3) || this.intersects(p4)) {
                this.knockDown();
                return true;
            }
        }
        return false;
    }
    
    // Check for collisions with a character named Hackus at specific points.
    public boolean checkHitHackus() {
        Hackus p1 = (Hackus)getOneObjectAtOffset(131, 2, Hackus.class);
        Hackus p2 = (Hackus)getOneObjectAtOffset(176, 32, Hackus.class);
        Hackus p3 = (Hackus)getOneObjectAtOffset(192, 168, Hackus.class);
        Hackus p4 = (Hackus)getOneObjectAtOffset(3, 150, Hackus.class);
        
        if (p1 != null && p2 != null && p3 != null && p4 != null) {
            if (this.intersects(p1) || this.intersects(p2) || this.intersects(p3) || this.intersects(p4)) {
                this.knockDown();
                return true;
            }
        }
        return false;
    }
}
