import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.lang.String;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Main controller class that controls the main UI that the user sees upon launching the
 * application.
 *
 * @author Feras Yahya
 */
public class MainController {

    static MovieList masterMovieList = new MovieList("movies.dat");
    private MovieList adjustableMovieList;

    //GUI Controls
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button reportsButton;
    @FXML private Button resetButton;
    @FXML private ListView<String> movieListView;
    @FXML private TextField searchField;
    @FXML private TextField activeFilterField;
    @FXML private TextArea generalField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ComboBox<String> generalStatsComboBox;
    @FXML private TextArea detailsTextArea;
    @FXML private ToggleButton sortOrder;

    //A field that will determine what order the movie list will be sorted in based on the current sort state of the movie list 
    private boolean sorted = false;

    @FXML private void initialize() {

        System.out.println("Initialize ran");

        filterComboBox.getItems().addAll("Year", "Genre", "Director", null);
        generalStatsComboBox.getItems().addAll("Year", "Genre", "Director");
        sortComboBox.getItems().addAll("Year", "Genre", "Director", "Title", null);

        // This is the movie list that will be used to filter and sort the movie list as
        // per the user's selection
        adjustableMovieList = new MovieList();

        generalStats();
        updateMovieList(masterMovieList);

        /*
        Selection  listner that will constantly monitor if the user has selected a movie 
        from the movie list view. If a movie is selected, its information will be displayed in the
        display text area. If no movie is selected, the display text area will be cleared
        */
        movieListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Movie selectedMovie = masterMovieList.searchByTitle(newValue);

                if (selectedMovie != null) {
                    // Display the details of the selected movie in the details tab
                    detailsTextArea.setText(selectedMovie.toString());

                    System.out.println(
                            "Selected movie is " + selectedMovie.getTitle() + ". Displaying info in details tab");
                }
            } else {
                // Clear the details if no movie is selected
                detailsTextArea.clear();

                System.out.println("No active selection");
            }
        });

        /*
        Search field listner that will constantly monitor the contents of 
        the search text field to determine if the user is search for a movie
        */
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearchAction(newValue);
        });

        /*
        Sort order listner that will constantly the current sorting state of the movie list
        and determine whether the sort order toggle button is currently set to ascending or
        descending order 
        */
        sortOrder.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Sorted: " + sorted);
            if (newValue) {
                sortOrder.setText("Descending");
                if (sorted) {
                    handleSortAction();
                }
            } else {
                sortOrder.setText("Ascending");
                if (sorted) {
                    handleSortAction();
                }
            }
        });

    }

    @FXML private void openAboutMenu(){
        
        try {
            Parent root = FXMLLoader.load(getClass().getResource("About.fxml"));

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("About Page");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void onResetClicked() {
        resetLayout();
    }

    @FXML private void onAddClicked() {
        try {
            
            Parent root = FXMLLoader.load(getClass().getResource("AddMovie.fxml"));

            Stage addMovieStage = new Stage();
            addMovieStage.initModality(Modality.APPLICATION_MODAL);
            addMovieStage.setTitle("Add Movie");
            addMovieStage.setScene(new Scene(root));

            addMovieStage.showAndWait();

            // Refresh the movie list view after adding a movie
            updateMovieList(masterMovieList); 
            generalStats();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void onEditClicked() {

        // Store the current movie that the user has selected from the movie list.
        String selectedMovieTitle = movieListView.getSelectionModel().getSelectedItem();
        if (selectedMovieTitle != null) {
            // Get the selected movie object
            Movie selectedMovie = masterMovieList.searchByTitle(selectedMovieTitle);
            
            if (selectedMovie != null) {
                masterMovieList.setSelectedMovie(selectedMovie);
                try {
                
                    Parent root = FXMLLoader.load(getClass().getResource("AddMovie.fxml"));
                    Stage editMoviStage = new Stage();
                    editMoviStage.initModality(Modality.APPLICATION_MODAL);
                    editMoviStage.setTitle("Edit movie");
                    editMoviStage.setScene(new Scene(root));
                    editMoviStage.showAndWait();

                    // Refresh the movie list view after editing the movie
                    updateMovieList(masterMovieList); 
                    generalStats();
                    masterMovieList.setSelectedMovie(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML private void onDeleteClicked() {
        String selectedMovieTitle = movieListView.getSelectionModel().getSelectedItem();
        if (selectedMovieTitle != null) {
            // Display confirmation dialog
            System.out.println("Confirm deletion of " + selectedMovieTitle);
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Delete Movie");
            confirmationDialog.setHeaderText("Confirm Deletion");
            confirmationDialog.setContentText("Are you sure you want to delete the selected movie?");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                // Get the selected movie object
                Movie movieToDelete = masterMovieList.searchByTitle(selectedMovieTitle);

                if (movieToDelete != null) {
                    // Remove the movie from the movie list
                    System.out.println(selectedMovieTitle + " successfully deleted");

                    masterMovieList.get_movies().remove(movieToDelete);

                    // Save the updated movie list to the file
                    masterMovieList.saveToFile();

                    // Refresh the movie list view
                    updateMovieList(masterMovieList);
                    generalStats();

                }
            } else {
                System.out.println("Delete request canceled. " + selectedMovieTitle + " is still in movie list");
            }
        }
    }

    @FXML private void handleSearchAction(String searchTerm) {
        /*When the user searches for a movie, if there is an active filter, 
        it will be cleared and the search will encompass the entire movie list 
        not the currently filtered one as the filter will be cleared
        */
        activeFilterField.clear();
        if (searchTerm != "") {
            adjustableMovieList.get_movies().clear();

            // System.out.println("There is no active filters or sorting, searching master
            // list...");

            for (Movie movie : masterMovieList.get_movies()) {
                if (movie.getTitle().toLowerCase().contains(searchTerm.toLowerCase())) {
                    adjustableMovieList.addMovie(movie);
                }
            }
            if (adjustableMovieList.get_movies().isEmpty()) {
                movieListView.getItems().clear();
                movieListView.getItems().add("Search yeilds no result..");
            } else {
                updateMovieList(adjustableMovieList);
            }
        } else {
            System.out.println("Search bar is empty");
            resetLayout();
        }

    }

    @FXML private void handleSortAction() {
        String selectedSort = sortComboBox.getValue();

        if (selectedSort != null) {
            if (adjustableMovieList.get_movies().isEmpty()) {
                adjustableMovieList.copyList(masterMovieList);
                adjustableMovieList.sortBy(selectedSort, sortOrder.isSelected());
            } else {
                adjustableMovieList.sortBy(selectedSort, sortOrder.isSelected());
            }
            System.out.println("Sorting by " + selectedSort);
            sorted = true;
            updateMovieList(adjustableMovieList);
        }

    }

    @FXML private void handleFilterAction() {
        String selectedFilter = filterComboBox.getValue();
        if (selectedFilter != null) {
            openFilterInputDialog(selectedFilter);
        }
    }

    @FXML private void onReportsClicked() {
        try {


            Parent root = FXMLLoader.load(getClass().getResource("Reports.fxml"));

            Stage reportsStage = new Stage();
            reportsStage.initModality(Modality.APPLICATION_MODAL);
            reportsStage.setTitle("Reports");
            reportsStage.setScene(new Scene(root));
            reportsStage.showAndWait();

            // Refresh the movie list view after adding a movie
            updateMovieList(masterMovieList); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void onComboBoxChanged(){
        generalStats();
    }

    /**
     * Helper methods
     */

    /**
     * Opens the filter input dialog responsible for generating the various options 
     * based on the user's selected filter type. The method utilizes a set to store 
     * a unique entry of the current available directors, genres and years of release
     * that is updated actively as the user enters new movies with new directors, genres 
     * and years of release
     * 
     * @param filterType The type of filter to generate the various options based on
     */
    private void openFilterInputDialog(String filterType) {
        System.out.println("Filter menu opened...");
        Alert error = new Alert(AlertType.ERROR,
                "You did not select anything!");

        // The options in this combo box actively update as the user enters a movie with
        // a new year, genre or director.
        ComboBox<String> activeOptions = new ComboBox<>();

        //Utilize a set (since sets dont allow duplicates) to create a unique list of the current options 
        //in the user's movie list based on the selected filter. 
        Set<String> options = new HashSet<>();

        try {
            //Determine the method to be called based on the user's selection. For example, if user selected Year,  
            // the method to be called will be getYear()
            Method method = Movie.class.getMethod("get" + filterType);
            for (Movie movies : masterMovieList.get_movies()) {
                options.add(method.invoke(movies).toString());
            }
            for (String option : options) {
                activeOptions.getItems().add(option);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // TODO: handle exception
        }

        GridPane gridPane = new GridPane();
        gridPane.add(activeOptions, 0, 0);

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Please select an option:");
        alert.getDialogPane().setContent(gridPane);
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String selectedOption = activeOptions.getValue();
                System.out.println("Selected filter option: " + selectedOption);

                if (selectedOption == null) {
                    error.showAndWait();
                    filterComboBox.setValue(null);

                } else {
                    // adjustableMovieList = masterMovieList.filterBy(filterType, selectedOption);
                    if (adjustableMovieList.get_movies().isEmpty()) {

                        System.out.println("Filter yeileded result");
                        adjustableMovieList.copyList(masterMovieList);

                    }
                    adjustableMovieList.filterBy(filterType, selectedOption);
                    if (adjustableMovieList.get_movies().isEmpty()) {
                        System.out.println("Filter yeileded nothing");

                        movieListView.getItems().clear();
                        movieListView.getItems().add("Filter yeilds nothing..");
                    } else {
                        updateMovieList(adjustableMovieList);

                    }

                    //Print out the active filter in the appropriate text field
                    activeFilterField.setText(filterType + ": " + selectedOption);

                }

            } else {
                System.out.println("Filter selection canceled. Exiting to main view");
            }
        });

    }

    /**
     * Displays general statistics about the movie list to include total movies, movies per genre, movies per year of release
     * and movies per director.
     *  
     */
    private void generalStats() {

        String generalStats = generalStatsComboBox.getValue();

        if (generalStats == "Genre"){
            generalField.clear();
            generalField.appendText("Total movies: " + masterMovieList.get_movies().size());
            generalField.appendText("\nBy Genre:\n" + masterMovieList.printMovieCountByGenre());
        }else if (generalStats == "Year") {
            generalField.clear();
            generalField.appendText("Total movies: " + masterMovieList.get_movies().size());
            generalField.appendText("\nBy Year\n" + masterMovieList.printMovieCountByYear());
        }else if (generalStats == "Director"){
            generalField.clear();
            generalField.appendText("Total movies: " + masterMovieList.get_movies().size());
            generalField.appendText("\nBy Director:\n" + masterMovieList.printMovieCountByDirector());

        }else{
            generalField.clear();
            generalField.appendText("Total movies: " + masterMovieList.get_movies().size());
        }

    }

    /**
     * 
     * @param ml The movie list to be displayed in the list view
     */
    private void updateMovieList(MovieList ml) {
        movieListView.getItems().clear(); // Clear the movie list view

        for (Movie movie : ml.get_movies()) {
            movieListView.getItems().add(movie.getTitle()); // Add movies from the movie list to the movie list view
        }

    }

    /**
     * Resets the overall layout of the UI by clearing all filters, sorts, and search values. Displays the original
     * movie list according to the order in which the movies were originally added.
     * 
     */
    private void resetLayout() {
        System.out.println("Reseting layout...");
        sorted = false;
        sortOrder.setSelected(false);
        activeFilterField.clear();
        filterComboBox.setValue(null);
        sortComboBox.setValue(null);
        adjustableMovieList.get_movies().clear();
        searchField.clear();
        updateMovieList(masterMovieList);
    }
}