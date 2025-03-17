import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Collections;
import java.util.ArrayList;
import java.util.Random;
/**
 * <h1>The new and vastly improved 2022 Vehicle Simulation Assignment.</h1>
 * <p> This is the first redo of the 8 year old project. Lanes are now drawn dynamically, allowing for
 *     much greater customization. Pedestrians can now move in two directions. The graphics are better
 *     and the interactions smoother.</p>
 * <p> The Pedestrians are not as dumb as before (they don't want straight into Vehicles) and the Vehicles
 *     do a somewhat better job detecting Pedestrians.</p>
 * 
 * Version Notes - Feb 2023
 * --> Includes grid <--> lane conversion method
 * --> Now starts with 1-way, 5 lane setup (easier)
 * 
 * V2023_021
 * --> Improved Vehicle Repel (still work in progress)
 * --> Implemented Z-sort, disabled paint order between Pedestrians and Vehicles (looks much better now)
 * --> Implemented lane-based speed modifiers for max speed
 * 
 * 
 * VEHICLE STIMULATION PROJECT: Written Task
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Image Credits:
 * Smurfette and PapaSmurf = https://www.smurf.com/ 
 * Smurf Village Background = https://www.sonypicturesanimation.com/projects/films/smurfs-lost-village 
 * Hackus = Hackus Image 
 * Azrael/ Cat = https://smurfs.fandom.com/wiki/Azrael/Gallery 
 * Gargamel = https://bluedwarfs.com/en/schleich-smurfs/4069985-20818-20826-8-schleich-smurfs-2020-smurfmix-4055744012235.html 
 * Smurf Police = https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.pinterest.com%2Fschlumpf1165795143%2F&psig=AOvVaw1_lOCq5f97nrUXvy4kz-un&ust=1698344648421000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCNCRiPzokYIDFQAAAAAdAAAAABAE 
 * Bomb GIF = https://www.google.com/url?sa=i&url=https%3A%2F%2Fgifer.com%2Fen%2F3iCN&psig=AOvVaw13WWu58ROSaPJzdw0FgDpq&ust=1698456937876000&source=images&cd=vfe&opi=89978449&ved=0CA8QjRxqFwoTCPiLtamLlYIDFQAAAAAdAAAAABAY 
 * 
 * Sound Credits:(7 Sounds)
 * Police siren sound = Free Police-Siren Sound Effects Download - Pixabay
 * Snowstorm blowing sound = Blizzard Sound Effect | Best Online SFX Library for Your Projects
 * Gargamel “MOVE!” sound = https://elements.envato.com/man-yelling-move-YF67CY6 
 * Smurfette “Oh no” sound = https://getyarn.io/yarn-clip/59c2123d-51a3-4e90-89a9-0972b2babd4a 
 * Azrael Cat Meow sound = Sound Effect from <a href="https://pixabay.com/?utm_source=link-attribution&utm_medium=referral&utm_campaign=music&utm_content=82091">Pixabay</a>
 * Background sound = Youtube → Mixed clips edited and modified by Sana through the Flixier Browser based video editor, extracted sound from YouTube video
 * One Additional Sound(BOMB explosion on Gargamel) = Sound Effect from <a href="https://pixabay.com/sound-effects/?utm_source=link-attribution&utm_medium=referral&utm_campaign=music&utm_content=6055">Pixabay</a>
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * FEATURES:
 * 1. I added a GIF Explosion --> when the Police and Gargamel are in the VehicleWorld at the same time the Police bombs any Gargamels nearby(my local detection effect)
 * 2. Added changing Speech Text for Smurfette when she's in the world (In the movie, Smurfette is vocal about her feelings so I added that into my stimulation)
 * 3. Added Death Counter for Gargamel
 * 4. Flashing Police Light **occurs sometimes 
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 *  
 * 
 * 
 * 
 */
public class VehicleWorld extends World
{
    private GreenfootImage background;
    // Color Constants
    public static Color BLACK_BORDER = new Color (0, 0, 0);
    //light red street
    public static Color VERMILLION_STREET = new Color (236, 100, 75);
    //light pink  street
    ///public static Color VERMILLION_STREET = new Color (255, 182, 193);
    //light green  street
    //public static Color VERMILLION_STREET = new Color (144, 238, 144);
    public static Color YELLOW_LINE = new Color (255, 216, 0);
    public static boolean SHOW_SPAWNERS = false;
    
    // Set Y Positions for Pedestrians to spawn
    public static final int TOP_SPAWN = 350; // Pedestrians who spawn on top
    public static final int BOTTOM_SPAWN = 705; // Pedestrians who spawn on the bottom

    // Instance variables / Objects
    private boolean twoWayTraffic, splitAtCenter;
    private int laneHeight, laneCount, spaceBetweenLanes;
    private int[] lanePositionsY;
    private VehicleSpawner[] laneSpawners;
    private SimpleTimer elapsedTime;
    private int actCount;
    private int nextSnowstormAct; // when to spawn the next Sandstorm
    private Snowstorm snowstorm;
    public static int gargamelCount;
    //sounds/ music
    private GreenfootSound bgm;
    private GreenfootSound snowstormSound;
    private GreenfootSound policeSiren;
    private GreenfootSound explosionSound;
    private GreenfootSound evilMeow;
    
    /**
     * Constructor for objects of class MyWorld.
     * 
     * Note that the Constrcutor for the default world is always called
     * when you click the reset button in the Greenfoot scenario screen -
     * this is is basically the code that runs when the program start.
     * Anything that should be done FIRST should go here.
     * 
     */
    public VehicleWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        //super(800, 400, 1, false);
        //super(1024, 800, 1, false); 
        super(1109, 600, 1, false);
        snowstorm = new Snowstorm();
        actCount = 0;
        nextSnowstormAct = 300;
        gargamelCount = 0;
        
        bgm = new GreenfootSound("New Project (1).mp3");
        bgm.setVolume(80);
        snowstormSound = new GreenfootSound("snowstormSound.mp3");
        policeSiren = new GreenfootSound("distant-ambulance-siren-6108.mp3");
        explosionSound = new GreenfootSound("explosion-6055.mp3");
        evilMeow = new GreenfootSound("angry-cat-meow-82091.mp3");
        
        //SmurfSmoke.init();
        // This command (from Greenfoot World API) sets the order in which 
        // objects will be displayed. In this example, Pedestrians will
        // always be on top of everything else, then Vehicles (of all
        // sub class types) and after that, all other classes not listed
        // will be displayed in random order. 
        //setPaintOrder (Pedestrian.class, Vehicle.class); // Commented out to use Z-sort instead

        // set up background -- If you change this, make 100% sure
        // that your chosen image is the same size as the World
        background = new GreenfootImage ("smurfVillage3.jpg");
        // Set critical variables - will affect lane drawing
        laneCount = 4;
        laneHeight = 60;
        spaceBetweenLanes = 6;
        splitAtCenter = false;
        twoWayTraffic = false;

        // Init lane spawner objects 
        laneSpawners = new VehicleSpawner[laneCount];

        // Prepare lanes method - draws the lanes
        lanePositionsY = prepareLanes (this, background, laneSpawners, 330, laneHeight, laneCount, spaceBetweenLanes, twoWayTraffic, splitAtCenter);
        laneSpawners[0].setSpeedModifier(0.8);
        laneSpawners[3].setSpeedModifier(1.4);

        elapsedTime = new SimpleTimer();

        
        addObject(new PoliceReport(), 190,45);
        addObject(new GargamelCount(), 220, 60);
        //setting the background of smurf village
        setBackground (background);
    }
    
    //starts background sound
    public void started()
    {
        bgm.playLoop();
    }
    
    //stops/ pauses background sound/ sound effects
    public void stopped()
    {
        bgm.stop();
        snowstormSound.pause();
        policeSiren.pause();
        //gargamelYelling.pause();
    }

    public void act () {
        
        
        //elapsedTime.mark();
        actCount++;
        spawn();
        zSort ((ArrayList<Actor>)(getObjects(Actor.class)), this);
        
        // Check if both SmurfPolice and Bus are in the world
        if (getObjects(SmurfPolice.class).size() > 0 && getObjects(Gargamel.class).size() > 0) {
            // Find the SmurfPolice and Bus objects
            SmurfPolice smurfPolice = getObjects(SmurfPolice.class).get(0);
            Gargamel bus = getObjects(Gargamel.class).get(0);
    
            // Get the x position of the SmurfPolice
            int smurfPoliceX = smurfPolice.getX();
    
            // Calculate the x position with an offset equal to the world width
            int offsetPosition = smurfPoliceX + getWidth();
    
            if (bus.getX() >= smurfPoliceX && bus.getX() <= offsetPosition) {
                // The Bus is ahead of the SmurfPolice in the same lane
                if (!bus.hasExplosion()) {
                   policeSiren.setVolume(65);
                   policeSiren.play();
                   bgm.setVolume(40);
                   //explosionSound.setVolume(100);
                   //explosionSound.play();
                   addObject(new BombGIF(), bus.getX(), bus.getY());
                    
                   addObject(new PoliceFlashing(100), smurfPoliceX, smurfPolice.getY());
                   // System.out.println("Trying to spawn a BlueFlashing at " + smurfPoliceX + ", " + smurfPolice.getY());
                   addObject(new BlueFlashing(100), smurfPoliceX, smurfPolice.getY());
                   removeObject(bus);
                   gargamelCount++;
                   bus.setExplosion(true);
                    
                    // Update the GargamelCount object's image with the current count
                   GargamelCount gargamelCountObject = getObjects(GargamelCount.class).get(0);
                   gargamelCountObject.setImage(new GreenfootImage("Number of Gargamels Exploded: " + gargamelCount, 22, Color.BLACK, new Color(0, 0, 0, 0)));
                }
            }
            else 
            {
                //policeSiren.stop()
            }
        }
        
        if (actCount == nextSnowstormAct){
            addObject (new Snowstorm(), 0, getHeight()/2); // start the Sandstorm
            snowstormSound.play();
            // Calculate the INTERVAL - how long between spawns?
            // This presents a minimum of 600 and a maximum of 899 (600 + rand(0-299)
            // which represents about 10 to 15 seconds.
            int actsUntilNextStorm = Greenfoot.getRandomNumber (300) + 600;
             
            // Add the current actCount (it just keeps counting up) to the desired
            // interval to determing when the next Sandstorm should spawn
            nextSnowstormAct = actCount + actsUntilNextStorm;
        }
    }

    private void spawn () {
        // Chance to spawn a vehicle
        if (Greenfoot.getRandomNumber (60) == 0){
            int lane = Greenfoot.getRandomNumber(laneCount);
            if (!laneSpawners[lane].isTouchingVehicle()){
                int vehicleType = Greenfoot.getRandomNumber(4);
                if (vehicleType == 0){
                    evilMeow.setVolume(60);
                    evilMeow.play();
                    addObject(new CatAzrael(laneSpawners[lane]), 0, 0);
                } else if (vehicleType == 1){
                    addObject(new Gargamel(laneSpawners[lane]), 0, 0);
                } else if (vehicleType == 2){
                    addObject(new SmurfPolice(laneSpawners[lane]), 0, 0);
                    //addObject(new Ambulance(laneSpawners[lane]), 0, 0);
                }
                else if (vehicleType == 3){
                    addObject(new Hackus(laneSpawners[lane]), 0, 0);
                }
            }
        }
        
        // Chance to spawn a Pedestrian
        // Number used to originally be 60
        if (Greenfoot.getRandomNumber (200) == 0){
            int xSpawnLocation = Greenfoot.getRandomNumber (600) + 100; // random between 99 and 699, so not near edges
            boolean spawnAtTop = Greenfoot.getRandomNumber(2) == 0 ? true : false;
            if (spawnAtTop){
                addObject (new Smurfette(1), xSpawnLocation, TOP_SPAWN);
                addObject (new PapaSmurf(1), xSpawnLocation, TOP_SPAWN);
            } else {
                addObject (new Smurfette(-1), xSpawnLocation, BOTTOM_SPAWN);
                addObject (new PapaSmurf(1), xSpawnLocation, TOP_SPAWN);
            }
        }
        
        //TESTING...
        //doesnt work well
        //removed rn
        //if(elapsedTime.millisElapsed() > 10000)
        //{
            //addObject (new Gargamel(1), Greenfoot.getRandomNumber (600) + 100, TOP_SPAWN);
            //elapsedTime.mark();
        //}
        
        //elapsedTime.mark();
        //if (actCount == nextSandstormAct){
            //commeting out to test lane change without snowstorn
            //addObject (new Sandstorm(), 0, getHeight()/2); // actually start the Sandstorm
            //snowstormSound.play();
            // Calculate the INTERVAL - how long between spawns?
            // This presents a minimum of 600 and a maximum of 899 (600 + rand(0-299)
            // which represents about 10 to 15 seconds.
            //int actsUntilNextStorm = Greenfoot.getRandomNumber (300) + 600;
             
            // Add the current actCount (it just keeps counting up) to the desired
            // interval to determing when the next Sandstorm should spawn
            //nextSandstormAct = actCount + actsUntilNextStorm;
            
        //}
                
    }

    /**
     *  Given a lane number (zero-indexed), return the y position
     *  in the centre of the lane. (doesn't factor offset, so 
     *  watch your offset, i.e. with Bus).
     *  
     *  @param lane the lane number (zero-indexed)
     *  @return int the y position of the lane's center, or -1 if invalid
     */
    public int getLaneY (int lane){
        if (lane < lanePositionsY.length){
            return lanePositionsY[lane];
        } 
        return -1;
    }

    /**
     * Given a y-position, return the lane number (zero-indexed).
     * Note that the y-position must be valid, and you should 
     * include the offset in your calculations before calling this method.
     * For example, if a Bus is in a lane at y=100, but is offset by -20,
     * it is actually in the lane located at y=80, so you should send
     * 80 to this method, not 100.
     * 
     * @param y - the y position of the lane the Vehicle is in
     * @return int the lane number, zero-indexed
     * 
     */
    public int getLane (int y){
        for (int i = 0; i < lanePositionsY.length; i++){
            if (y == lanePositionsY[i]){
                return i;
            }
        }
        return -1;
    }

    public static int[] prepareLanes (World world, GreenfootImage target, VehicleSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing, boolean twoWay, boolean centreSplit, int centreSpacing)
    {
        // Declare an array to store the y values as I calculate them
        int[] lanePositions = new int[lanes];
        // Pre-calculate half of the lane height, as this will frequently be used for drawing.
        // To help make it clear, the heightOffset is the distance from the centre of the lane (it's y position)
        // to the outer edge of the lane.
        int heightOffset = heightPerLane / 2;

        // draw top border
        target.setColor (BLACK_BORDER);
        target.fillRect (0, startY, target.getWidth(), spacing);

        // Main Loop to Calculate Positions and draw lanes
        for (int i = 0; i < lanes; i++){
            // calculate the position for the lane
            lanePositions[i] = startY + spacing + (i * (heightPerLane+spacing)) + heightOffset ;

            // draw lane
            target.setColor(VERMILLION_STREET); 
            // the lane body
            target.fillRect (0, lanePositions[i] - heightOffset, target.getWidth(), heightPerLane);
            // the lane spacing - where the white or yellow lines will get drawn
            target.fillRect(0, lanePositions[i] + heightOffset, target.getWidth(), spacing);

            // Place spawners and draw lines depending on whether its 2 way and centre split
            if (twoWay && centreSplit){
                // first half of the lanes go rightward (no option for left-hand drive, sorry UK students .. ?)
                if ( i < lanes / 2){
                    spawners[i] = new VehicleSpawner(false, heightPerLane, i);
                    world.addObject(spawners[i], target.getWidth(), lanePositions[i]);
                } else { // second half of the lanes go leftward
                    spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                    world.addObject(spawners[i], 0, lanePositions[i]);
                }

                // draw yellow lines if middle 
                if (i == lanes / 2){
                    target.setColor(YELLOW_LINE);
                    target.fillRect(0, lanePositions[i] - heightOffset - spacing, target.getWidth(), spacing);

                } else if (i > 0){ // draw white lines if not first lane
                    for (int j = 0; j < target.getWidth(); j += 120){
                        target.setColor (Color.WHITE);
                        target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                    }
                } 

            } else if (twoWay){ // not center split
                if ( i % 2 == 0){
                    spawners[i] = new VehicleSpawner(false, heightPerLane, i);
                    world.addObject(spawners[i], target.getWidth(), lanePositions[i]);
                } else {
                    spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                    world.addObject(spawners[i], 0, lanePositions[i]);
                }

                // draw Grey Border if between two "Streets"
                if (i > 0){ // but not in first position
                    if (i % 2 == 0){
                        target.setColor(BLACK_BORDER);
                        target.fillRect(0, lanePositions[i] - heightOffset - spacing, target.getWidth(), spacing);

                    } else { // draw dotted lines
                        for (int j = 0; j < target.getWidth(); j += 120){
                            target.setColor (YELLOW_LINE);
                            target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                        }
                    } 
                }
            } else { // One way traffic
                spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                world.addObject(spawners[i], 0, lanePositions[i]);
                if (i > 0){
                    for (int j = 0; j < target.getWidth(); j += 120){
                        target.setColor (Color.WHITE);
                        target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                    }
                }
            }
        }
        // draws bottom border
        target.setColor (BLACK_BORDER);
        target.fillRect (0, lanePositions[lanes-1] + heightOffset, target.getWidth(), spacing);

        return lanePositions;
    }

    /**
     * A z-sort method which will sort Actors so that Actors that are
     * displayed "higher" on the screen (lower y values) will show up underneath
     * Actors that are drawn "lower" on the screen (higher y values), creating a
     * better perspective. 
     */
    public static void zSort (ArrayList<Actor> actorsToSort, World world){
        ArrayList<ActorContent> acList = new ArrayList<ActorContent>();
        // Create a list of ActorContent objects and populate it with all Actors sent to be sorted
        for (Actor a : actorsToSort){
            acList.add (new ActorContent (a, a.getX(), a.getY()));
        }    
        // Sort the Actor, using the ActorContent comparitor (compares by y coordinate)
        Collections.sort(acList);
        // Replace the Actors from the ActorContent list into the World, inserting them one at a time
        // in the desired paint order (in this case lowest y value first, so objects further down the 
        // screen will appear in "front" of the ones above them).
        for (ActorContent a : acList){
            Actor actor  = a.getActor();
            world.removeObject(actor);
            world.addObject(actor, a.getX(), a.getY());
        }
    }

    /**
     * <p>The prepareLanes method is a static (standalone) method that takes a list of parameters about the desired roadway and then builds it.</p>
     * 
     * <p><b>Note:</b> So far, Centre-split is the only option, regardless of what values you send for that parameters.</p>
     *
     * <p>This method does three things:</p>
     * <ul>
     *  <li> Determines the Y coordinate for each lane (each lane is centered vertically around the position)</li>
     *  <li> Draws lanes onto the GreenfootImage target that is passed in at the specified / calculated positions. 
     *       (Nothing is returned, it just manipulates the object which affects the original).</li>
     *  <li> Places the VehicleSpawners (passed in via the array parameter spawners) into the World (also passed in via parameters).</li>
     * </ul>
     * 
     * <p> After this method is run, there is a visual road as well as the objects needed to spawn Vehicles. Examine the table below for an
     * in-depth description of what the roadway will look like and what each parameter/component represents.</p>
     * 
     * <pre>
     *                  <=== Start Y
     *  ||||||||||||||  <=== Top Border
     *  /------------\
     *  |            |  
     *  |      Y[0]  |  <=== Lane Position (Y) is the middle of the lane
     *  |            |
     *  \------------/
     *  [##] [##] [##| <== spacing ( where the lane lines or borders are )
     *  /------------\
     *  |            |  
     *  |      Y[1]  |
     *  |            |
     *  \------------/
     *  ||||||||||||||  <== Bottom Border
     * </pre>
     * 
     * @param world     The World that the VehicleSpawners will be added to
     * @param target    The GreenfootImage that the lanes will be drawn on, usually but not necessarily the background of the World.
     * @param spawners  An array of VehicleSpawner to be added to the World
     * @param startY    The top Y position where lanes (drawing) should start
     * @param heightPerLane The height of the desired lanes
     * @param lanes     The total number of lanes desired
     * @param spacing   The distance, in pixels, between each lane
     * @param twoWay    Should traffic flow both ways? Leave false for a one-way street (Not Yet Implemented)
     * @param centreSplit   Should the whole road be split in the middle? Or lots of parallel two-way streets? Must also be two-way street (twoWay == true) or else NO EFFECT
     * 
     */
    public static int[] prepareLanes (World world, GreenfootImage target, VehicleSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing, boolean twoWay, boolean centreSplit){
        return prepareLanes (world, target, spawners, startY, heightPerLane, lanes, spacing, twoWay, centreSplit, spacing);
    }

}

/**
 * Container to hold and Actor and an LOCAL position (so the data isn't lost when the Actor is temporarily
 * removed from the World).
 */
class ActorContent implements Comparable <ActorContent> {
    private Actor actor;
    private int xx, yy;
    public ActorContent(Actor actor, int xx, int yy){
        this.actor = actor;
        this.xx = xx;
        this.yy = yy;
    }

    public void setLocation (int x, int y){
        xx = x;
        yy = y;
    }

    public int getX() {
        return xx;
    }

    public int getY() {
        return yy;
    }

    public Actor getActor(){
        return actor;
    }

    public String toString () {
        return "Actor: " + actor + " at " + xx + ", " + yy;
    }

    public int compareTo (ActorContent a){
        return this.getY() - a.getY();
    }

}
