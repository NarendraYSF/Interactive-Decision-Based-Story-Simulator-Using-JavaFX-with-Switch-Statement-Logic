
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kelas controller yang menangani interaksi UI dan menghubungkan UI ke logika cerita.
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
        // Menginisialisasi pengelola cerita
        storyManager = new StoryManager();

        // Pembaruan UI awal
        updateUI();

        // Mengatur tombol reset
        resetButton.setOnAction(event -> {
            storyManager.resetGame();
            updateUI();
        });
    }

    /**
     * Memperbarui UI berdasarkan keadaan cerita saat ini
     */
    private void updateUI() {
        // Memperbarui teks cerita
        storyTextLabel.setText(storyManager.getCurrentSceneDescription());

        // Memperbarui tampilan skor moralitas
        int morality = storyManager.getMoralityScore();
        moralityLabel.setText("Moralitas: " + morality);

        // Perbarui bilah moralitas (dinormalisasi antara -100 dan 100)
        double normalizedMorality = (morality + 100) / 200.0;
        normalizedMorality = Math.max(0, Math.min(1, normalizedMorality));
        moralityBar.setProgress(normalizedMorality);

        // Mengatur warna berdasarkan moralitas menggunakan struktur if bertingkat seperti switch
        if (morality > 30) {
            moralityBar.setStyle("-fx-accent: green;");
        } else if (morality > 0) {
            moralityBar.setStyle("-fx-accent: lightgreen;");
        } else if (morality > -30) {
            moralityBar.setStyle("-fx-accent: orange;");
        } else {
            moralityBar.setStyle("-fx-accent: red;");
        }

        // Memperbarui teks inventory
        StringBuilder inventoryText = new StringBuilder("Tas: ");
        if (storyManager.hasWeapon()) {
            inventoryText.append("Senjata Legendaris ");
        }
        if (storyManager.hasArtifact()) {
            inventoryText.append("Artefak Kuno ");
        }
        if (!storyManager.hasWeapon() && !storyManager.hasArtifact()) {
            inventoryText.append("Kosong");
        }
        inventoryLabel.setText(inventoryText.toString());

        // Menghapus dan memperbarui pilihan
        choicesContainer.getChildren().clear();

        // Dapatkan pilihan yang tersedia dari StoryManager
        StoryManager.ChoiceType[] choices = storyManager.getAvailableChoices();

        // Periksa status akhir
        if (storyManager.getCurrentScene() == StoryManager.SceneID.GAME_OVER ||
                storyManager.getCurrentScene() == StoryManager.SceneID.VICTORY) {
            resetButton.setVisible(true);
        } else {
            resetButton.setVisible(false);

            // Menambahkan tombol pilihan secara dinamis berdasarkan pilihan yang tersedia
            for (StoryManager.ChoiceType choice : choices) {
                Button choiceButton = new Button(storyManager.getChoiceButtonText(choice));
                choiceButton.setPrefWidth(300);

                // Menyimpan pilihan dalam variabel akhir untuk digunakan dalam lambda
                final StoryManager.ChoiceType buttonChoice = choice;

                // Mengatur tindakan untuk tombol ini
                choiceButton.setOnAction(event -> {
                    storyManager.handleChoice(buttonChoice);
                    updateUI();
                });

                choicesContainer.getChildren().add(choiceButton);
            }
        }
    }
}
