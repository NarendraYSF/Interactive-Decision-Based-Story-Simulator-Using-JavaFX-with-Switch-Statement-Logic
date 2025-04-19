import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Titik masuk utama untuk aplikasi Cerita Interaktif.
 * Kelas ini meluncurkan aplikasi JavaFX dan menyiapkan primary stage.
 */
public class  StorySimulatorApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Memuat file FXML untuk UI utama
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();

        // Mengonfigurasi dan menampilkan primary stage
        primaryStage.setTitle("Story Simulator App");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Titik masuk aplikasi
     */
    public static void main(String[] args) {
        launch(args);
    }
}

