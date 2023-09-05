import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * About controller class that controls the About page UI that the user selects the questions mark
 * icon. Presents the user with a friendly description of the app and its functionality.
 *
 * @author Feras Yahya
 */
public class AboutController {

    //GUI Controls
    @FXML Button returnButton;


    @FXML private void initialize() {

    }

    @FXML private void onReturnClicked() {
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
    }

}
