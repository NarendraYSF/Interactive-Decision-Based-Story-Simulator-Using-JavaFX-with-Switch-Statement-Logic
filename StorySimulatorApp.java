import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the Interactive Story Simulator application.
 * This class launches the JavaFX application and sets up the primary stage.
 */
public class StorySimulatorApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file for the main UI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();

        // Configure and display the primary stage
        primaryStage.setTitle("Interactive Story Simulator");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Application entry point
     */
    public static void main(String[] args) {
        launch(args);
    }
}
