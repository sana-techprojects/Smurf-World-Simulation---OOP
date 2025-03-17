import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class PoliceReport here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PoliceReport extends Actor
{
    GreenfootImage report = new GreenfootImage("policeDepartementReportImage.png");
    /**
     * Act - do whatever the PoliceReport wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public PoliceReport()
    {
        report.scale(380,100);
        setImage(report);
    }
    public void act()
    {
        // Add your action code here.
    }
}
