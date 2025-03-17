import greenfoot.*;

public class Smurfette extends Pedestrian {
    GreenfootImage smurfette = new GreenfootImage("smurfette.png"); // Load an image for Smurfette.
    SimpleTimer timer = new SimpleTimer(); // Create a timer for controlling dialogue display timing.
    private String[] dialogues; // An array to store dialogues for Smurfette.
    private int currentDialogueIndex; // Index to keep track of the current dialogue being displayed.

    // Constructor for Smurfette, taking the direction parameter and initializing Smurfette's attributes.
    public Smurfette(int direction) {
        super(direction);
        smurfette.scale(95, 95); // Scale the image of Smurfette to a specific size.
        setImage(smurfette); // Set the image of this actor to the scaled Smurfette image.
        dialogues = new String[]{"Smurf Police! \nSAVE US!", "HELP! \nIt's Gargamel!", "Papa! \nBE CAREFUL", "SOS!", "RUN PAPA!"};
        currentDialogueIndex = Greenfoot.getRandomNumber(5); // Initialize the dialogue index with a random value.
        displayNextDialogue(); // Display the initial dialogue.
    }

    // The act method, which is called whenever the 'Act' or 'Run' button is pressed in the environment.
    public void act() {
        super.act(); // Call the act method of the parent class (Pedestrian).

        // Display dialogues automatically after a certain time interval.
        if (timer.millisElapsed() > 3000) {
            displayNextDialogue(); // Display the next dialogue.
        }
    }

    // Display the next dialogue in the dialogues array.
    private void displayNextDialogue() {
        if (currentDialogueIndex < dialogues.length) {
            String dialogue = dialogues[currentDialogueIndex];

            // Clear the previous text on the image.
            GreenfootImage imageWithDialogue = new GreenfootImage(smurfette);

            imageWithDialogue.setColor(Color.BLACK); // Set the text color to black.
            imageWithDialogue.setFont(new Font("Calibri", 13)); // Customize the font and size here.

            // Adjust the x and y coordinates to fit the text within the image.
            int x = 10;
            int y = 8; // You can adjust this value to position the text as desired.

            imageWithDialogue.drawString(dialogue, x, y); // Draw the dialogue text on the image.

            // Set the updated image with the dialogue as the new image for Smurfette.
            setImage(imageWithDialogue);

            currentDialogueIndex++; // Move to the next dialogue in the array.
        } else {
            // All dialogues have been displayed, so reset the index to start from the beginning.
            currentDialogueIndex = 0;

            // Clear the displayed text by creating a new image without text.
            GreenfootImage imageWithDialogue = new GreenfootImage(smurfette);

            // Set the updated image as the new image for Smurfette, effectively clearing the text.
            setImage(imageWithDialogue);
        }

        timer.mark(); // Reset the timer to control the timing of dialogue display.
    }
}
