import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Explosion graphic - An Actor that consists of a circle that expands
 * to a set size, then removes itself from the world, create the 
 * appearance of an explosion
 * 
 * Note - to use this, your WORLD CONSTRUCTOR MUST CALL: 
 * 
 * Explosion.init(); 
 * 
 * @author Jordan Cohen
 * @version November 2015
 */
public class PoliceFlashing extends Effect
{

    // This is static, so a single Array is created and shared by all Explosion objects.
    // This is safe because Greenfoot runs these commands consecutively, not simultaneously,
    // so there is no threat of a conflict. 
    private static GreenfootSound[] explosionSounds;
    private static int explosionSoundsIndex = 0; 
    private GreenfootImage fireImage;

    private Color currentColor;

    private int radius;
    private int speed;
    private int steps;
    private int red, green, blue;
    private int transparency;
    private int maxSize;
    private int additionalExplosions;

    public PoliceFlashing (int maxSize, int additionalExplosions){
        this(maxSize);
        this.additionalExplosions = additionalExplosions;
    }

    /**
     * Constructor to create an Explosion
     * 
     * @param   maxSize     The size at which the explosion will stop growing and remove itself
     */
    public PoliceFlashing (int maxSize)
    {
        // Create image to manage this graphic
        fireImage = new GreenfootImage (maxSize, maxSize);
        // Variables that control Colors for fire effect:
        //red = 255;
        //green = 40;
        //blue = 1;
        red = 255;
        green = 0;
        blue = 50;
        // Start as fully opaque
        transparency = 255;
        additionalExplosions = 0;

        // Dynamic way to set speed so small explosions don't
        // disappear to fast and large explosions don't linger
        speed = Math.max((int)Math.sqrt(maxSize / 2), 1);
        // Set starting Color
        currentColor = new Color (red, green, blue);
        // Set starting Radius
        radius = 2;
        // Figure out how many times this will run, so opacity can decrease at
        // an appropriate rate
        steps = (maxSize) / speed;
        // Store maxSize (from parameter) in instance variable for
        // future user
        this.maxSize = maxSize;

        // Method to actually draw the circle
        redraw();
        // Set this Actor's graphic as the image I just created
        this.setImage(fireImage);
    }





    /**
     * Act method gets called by Greenfoot every frame. In this class, this method
     * will serve to increase the size each act until maxSize is reached, at which
     * point the object will remove itself from existence.
     */
    public void act() 
    {
        redraw();   // redraw the circle at its new size

        if (additionalExplosions > 1){
            if (Greenfoot.getRandomNumber(5) == 0){
                // Random x and y offsets between -50 and 50
                int xOffset = Greenfoot.getRandomNumber (maxSize) - (maxSize / 2);
                int yOffset = Greenfoot.getRandomNumber (maxSize) - (maxSize / 2);
                int newSize = (int)(maxSize * 0.40);
                getWorld().addObject (new PoliceFlashing (newSize), getX() + xOffset, getY() + yOffset);
                additionalExplosions--;
            }

        }

        else if (radius + speed <= maxSize)  // If the explosion hasn't yet hit its max
            radius += speed;            // size, keep growing

        else{ // explosion has finished growing
            // what to do when I'm finished "Exploding"

            // get list of ALL Vehicles that I'm touching
            ArrayList<Vehicle> vehicles = (ArrayList<Vehicle>)getIntersectingObjects(Vehicle.class);
            for (Vehicle v : vehicles){
                if (v instanceof CatAzrael){
                    getWorld().removeObject(v);
                } else {
                    v.speedUp (0.5);
                }
            }

            ArrayList<Pedestrian> peds = (ArrayList<Pedestrian>)getObjectsInRange (maxSize, Pedestrian.class);
            for (Pedestrian p : peds){
                p.knockDown();
            }

            getWorld().removeObject(this);
        }
        // remove it from the World
    }    

    /**
     * redraw() method is a private method called by this object each act
     * in order to redraw the graphic
     */
    private void redraw()
    {
        // adjust colors
        green = Math.min(255, Math.max(0, green + (150 / steps)));
        blue = Math.min(255, Math.max(0, blue + (10 / steps)));
        // reduce transparency, but ensure it doesn't fall below zero - that would cause
        // a crash
        if (transparency - (255 / steps) > 0)
            transparency -= (255 / steps);
        else
            transparency = 0;

        // update Color
        currentColor = new Color (red, green, blue);

        // update transparency
        fireImage.setTransparency(transparency);
        fireImage.setColor (currentColor);
        // redraw image
        fireImage.fillOval ((maxSize - radius)/2, (maxSize - radius)/2, radius, radius);
        fireImage.setTransparency(transparency);
    }

}
