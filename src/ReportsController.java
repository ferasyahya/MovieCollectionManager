import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Reports controller class that controls the reports UI that the user sees when pressing the
 * reports button.
 *
 * @author Feras Yahya
 */
public class ReportsController {

    // GUI Controls
    @FXML private Button returnButton;
    @FXML private Button clearButton;
    @FXML private Button saveButton;
    @FXML private TextArea reportArea;
    @FXML private ComboBox<String> reportsComboBox;

    private MovieList masterMovieList_reportsMenu = MainController.masterMovieList;
    private MovieList adjustedMovieList;

    @FXML private void initialize() {
        // Initialize genreComboBox with genre options

        reportsComboBox.getItems().addAll("Year", "Genre", "Director", null);
        adjustedMovieList = new MovieList();
        System.out.println("Reports");

    }

    @FXML private void handleReportsAction() {
        String reportType = reportsComboBox.getValue();
        if (reportType != null)
            openReportsInputDialog(reportType);
    }

    private void openReportsInputDialog(String ReportType) {

        Alert error = new Alert(AlertType.ERROR,
                "You did not select anything!");

        // The options in this combo box actively update as the user enters a movie with
        // a new year, genre or director.
        ComboBox<String> activeOptions = new ComboBox<>();
        // Use a set to store the report field options to ensure there are no
        // duplicates.
        Set<String> options = new HashSet<>();

        try {
            /*
             * Determine which method to invoke based on the user's selection. Convert the
             * user's selection from a string
             * to an invokable method
             */
            Method method = Movie.class.getMethod("get" + ReportType);
            for (Movie movies : masterMovieList_reportsMenu.get_movies()) {
                /*
                 * Once we have determined which method to invoke based on the user's input,
                 * invoke that method on the movies objects
                 * and store the contents returned by that method in the set.
                 */
                options.add(method.invoke(movies).toString());
            }
            // Another for loop to add the contents of the set into the combo box.
            for (String option : options) {
                activeOptions.getItems().add(option);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        //Display a new pane for the user to select from
        GridPane gridPane = new GridPane();
        gridPane.add(activeOptions, 0, 0);

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Report information ");
        alert.setHeaderText("Please select the " + ReportType + ":");
        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedOption = activeOptions.getValue();
            System.out.println("Selected option: " + selectedOption);

            //Display an error if the user does not select a report type
            if (selectedOption == null) {
                error.showAndWait();
                reportsComboBox.setValue(null);

            } else {
                adjustedMovieList.copyList(masterMovieList_reportsMenu);
                adjustedMovieList.filterBy(ReportType, selectedOption);
                reportArea.clear();
                reportArea.setWrapText(true);
                reportArea.appendText(
                        "There are " + adjustedMovieList.get_movies().size() + " movies for the " + ReportType + " "
                                + selectedOption);
                reportArea.appendText("\nThese movies are: ");
                int counter = 0;
                for (Movie movies : adjustedMovieList.get_movies()) {

                    reportArea.appendText("\n\tMovie " + ++counter + "\n" + movies);
                }
                reportsComboBox.setValue(null);

            }

        } else {
            reportsComboBox.setValue(null);
            System.out.println("Dialog canceled");
        }


    }

    @FXML private void onReturnClicked() {
        // Close the add movie window
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
    }

    @FXML private void onClearClicked() {
        reportArea.clear();
    }

    @FXML private void onSaveClicked(){
        PrintWriter writer = null;

        try {
            File fileDescriptor = new File("Reports.txt");
            writer = new PrintWriter(fileDescriptor);
            writer.println(reportArea.getText());
        } catch (FileNotFoundException e) {
            // TODO: handle exception
        }finally{
            if (writer != null){
                writer.close();
            }
        }
    }

}