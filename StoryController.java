import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class that handles UI interactions and connects the UI to the story logic.
 */
public class StoryController implements Initializable {

    @FXML private Label storyTextLabel;
    @FXML private VBox choicesContainer;
    @FXML private Label moralityLabel;
    @FXML private ProgressBar moralityBar;
    @FXML private Label inventoryLabel;
    @FXML private Button resetButton;

    private StoryManager storyManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the story manager
        storyManager = new StoryManager();

        // Initial UI update
        updateUI();

        // Set up reset button
        resetButton.setOnAction(event -> {
            storyManager.resetGame();
            updateUI();
        });
    }

    /**
     * Updates the UI based on the current state of the story
     */
    private void updateUI() {
        // Update story text
        storyTextLabel.setText(storyManager.getCurrentSceneDescription());

        // Update morality score display
        int morality = storyManager.getMoralityScore();
        moralityLabel.setText("Morality: " + morality);

        // Update morality bar (normalized between -100 and 100)
        double normalizedMorality = (morality + 100) / 200.0;
        normalizedMorality = Math.max(0, Math.min(1, normalizedMorality));
        moralityBar.setProgress(normalizedMorality);

        // Set color based on morality using a switch-like cascading if structure
        if (morality > 30) {
            moralityBar.setStyle("-fx-accent: green;");
        } else if (morality > 0) {
            moralityBar.setStyle("-fx-accent: lightgreen;");
        } else if (morality > -30) {
            moralityBar.setStyle("-fx-accent: orange;");
        } else {
            moralityBar.setStyle("-fx-accent: red;");
        }

        // Update inventory text
        StringBuilder inventoryText = new StringBuilder("Inventory: ");
        if (storyManager.hasWeapon()) {
            inventoryText.append("Mythical Weapon ");
        }
        if (storyManager.hasArtifact()) {
            inventoryText.append("Ancient Artifact ");
        }
        if (!storyManager.hasWeapon() && !storyManager.hasArtifact()) {
            inventoryText.append("Empty");
        }
        inventoryLabel.setText(inventoryText.toString());

        // Clear and update choices
        choicesContainer.getChildren().clear();

        // Get available choices from story manager
        StoryManager.ChoiceType[] choices = storyManager.getAvailableChoices();

        // Check for end state
        if (storyManager.getCurrentScene() == StoryManager.SceneID.GAME_OVER ||
                storyManager.getCurrentScene() == StoryManager.SceneID.VICTORY) {
            resetButton.setVisible(true);
        } else {
            resetButton.setVisible(false);

            // Add choice buttons dynamically based on available choices
            for (StoryManager.ChoiceType choice : choices) {
                Button choiceButton = new Button(storyManager.getChoiceButtonText(choice));
                choiceButton.setPrefWidth(300);

                // Store the choice in a final variable to use in lambda
                final StoryManager.ChoiceType buttonChoice = choice;

                // Set up the action for this button
                choiceButton.setOnAction(event -> {
                    storyManager.handleChoice(buttonChoice);
                    updateUI();
                });

                choicesContainer.getChildren().add(choiceButton);
            }
        }
    }
}
