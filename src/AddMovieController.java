import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Add/Edit menu controller class that controls the add/edit menu UI that the user sees upon pressing the
 * add/edit button. If the user presses the edit button, the text fields are pre-filled with the selected
 * movie's information. Note that if there is no active selection, the edit menu does not open if the button 
 * is pressed.
 *
 * @author Feras Yahya
 */
public class AddMovieController {

    //GUI Controls
    @FXML private TextField titleField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private TextField directorField;
    @FXML private ComboBox<Movie.Genre> genreComboBox;
    @FXML private TextField yearField;

    private MovieList masterMovieList_addMenu = MainController.masterMovieList;
    //private Stage stage;

    //private boolean editMode = false;
    private Movie selectedMovie;

    @FXML private void initialize() {
        // Initialize genreComboBox with genre options
        System.out.println("Add/Edit menu");

        /*Determine if there is a selected movie, if the user selected a movie, 
        and clicked edit button, then the UI will pre-fill text fields with selected movie 
        information for editing an existing movie
        */
        selectedMovie = masterMovieList_addMenu.getSelectedMovie(); 
        if (selectedMovie != null) setSelectedMovie(selectedMovie);
        

        //Adds the genre to the combo box
        for (Movie.Genre genre : Movie.Genre.values()){
            genreComboBox.getItems().add(genre);
        }

    }

    @FXML private void onSaveClicked() {
        Alert missingFieldsError = new Alert(AlertType.ERROR,
                "Missing fields required!");
        
        Alert invalidYearError = new Alert(AlertType.ERROR,
                "Invalid year entered!");
        Stage stage = (Stage) saveButton.getScene().getWindow();

        //Determines whether to create a new movie or simply edit an existing movie in the movie list
        if (selectedMovie != null) {
            // Update the selected movie with the edited values
            selectedMovie.setTitle(titleField.getText());
            selectedMovie.setDirector(directorField.getText());
            selectedMovie.setGenre((genreComboBox.getValue()));
            selectedMovie.setYear(Integer.parseInt(yearField.getText()));

            // Save the movie list to the file
            masterMovieList_addMenu.saveToFile();

            // Close the add movie window
            stage.close();
        } else {
            // Get user input from fields
            String title = titleField.getText();
            String director = directorField.getText();
            Movie.Genre genre = genreComboBox.getValue();
            try {

                if (title == "" || director == "" || genre == null || yearField.getText() == "") {
                    Optional<ButtonType> errorResult = missingFieldsError.showAndWait();
                    if (errorResult.isPresent() || !errorResult.isPresent()) {

                    }

                } else {
                    int year = Integer.parseInt(yearField.getText());

                    // Create a new Movie object
                    Movie newMovie = new Movie(title, director, genre, year);

                    // Add the new movie to the movie list
                    masterMovieList_addMenu.addMovie(newMovie);

                    // Save the movie list to the file

                    masterMovieList_addMenu.saveToFile();
                    // Save the movie list to the file

                    // Close the add movie window
                    stage.close();
                }
            } catch (NumberFormatException e) {
                Optional<ButtonType> errorResult = invalidYearError.showAndWait();
                if (errorResult.isPresent() || !errorResult.isPresent()) {
                    yearField.clear();
                }
            }

        }

    }

    @FXML private void onCancelClicked() {
        // Close the add movie window
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    /*
     * This method is called when the user clicks the edit button. The method 
     * copies the fields of the selected movie and inserts them in the 
     * appropriate text fields
     */
    private void setSelectedMovie(Movie selectedMovie) {
        titleField.setText(selectedMovie.getTitle());
        directorField.setText(selectedMovie.getDirector());
        genreComboBox.setValue(selectedMovie.getGenre());
        yearField.setText(String.valueOf(selectedMovie.getYear()));
    }
}