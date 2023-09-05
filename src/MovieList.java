import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Movie class that defines the movie objects.
 *
 * @author Feras Yahya
 */
public class MovieList {

    /**
     * Fields
     */
    private ArrayList<Movie> _movies;
    private String filename;
    private Movie _selectedMovie;

    /**
     * Default constructor
     */
    public MovieList() {
        _movies = new ArrayList<>();
    }

    /**
     * Parameterized constructor
     * 
     * @param filename The file in which the movie list information will be stored
     *                 in
     */
    public MovieList(String filename) {
        this.filename = filename;
        _movies = new ArrayList<>();
        readFromFile();
    }

    /**
     * Public Methods
     */

    /**
     * Adds a movie object to the movie array list
     * 
     * @param movie The movie to be added to the array list
     */
    public void addMovie(Movie movie) {
        _movies.add(movie);
    }

    /**
     * Sets the currently selected movie as the selected movie to be referenced in
     * the edit menu.
     * 
     * @param selectedMovie The selected movie will be used to fill the text fields
     *                      in the edit menu when user
     *                      selects edit
     */
    public void setSelectedMovie(Movie selectedMovie) {
        _selectedMovie = selectedMovie;
    }

    /**
     * 
     * @return the selected movie to be referenced in the edit menu
     */

    public Movie getSelectedMovie() {
        return this._selectedMovie;
    }

    /**
     * Copies the content of the source list to the current list
     * 
     * @param source The source movie list from which the content will be copied to
     *               the list
     */
    public void copyList(MovieList source) {
        _movies.addAll(source._movies);
    }

    /**
     * 
     * @return the array list movie object
     */
    public ArrayList<Movie> get_movies() {
        return _movies;
    }

    /**
     * If a movie list was created with a filename, the movie list will store the
     * contents of its associated
     * file. If no file exists, or file is empty, the movie list will save its
     * content to the file, which will
     * be used to update the list next time the application is launched
     */
    public void readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            _movies = (ArrayList<Movie>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Handle exceptions (e.g., file not found)
        }
    }

    /**
     * Saves the content of the array movie list to a data file. The list is stored
     * as a single object
     * on the file.
     */
    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(_movies);
        } catch (IOException e) {
            // Handle exception
        }
    }

    /**
     * Helper method that sorts the movie list based on the sort criteria and
     * ascending/descending order
     * 
     * @param sortType  Type of the sort criteria. Possible criteria include Title,
     *                  Director, Genre and Year
     * @param ascending Sorts in ascending order if True, sorts in desceding order
     *                  if false
     */
    public void sortBy(String sortType, Boolean ascending) {
        switch (sortType) {
            case "Title":
                Collections.sort(_movies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie movie1, Movie movie2) {
                        if (ascending) {
                            return movie2.getTitle().compareTo(movie1.getTitle());
                        } else {
                            return movie1.getTitle().compareTo(movie2.getTitle());
                        }
                    }
                });
                break;

            case "Year":
                Collections.sort(_movies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie movie1, Movie movie2) {
                        if (ascending) {
                            return Integer.compare(movie2.getYear(), movie1.getYear());
                        } else {
                            return Integer.compare(movie1.getYear(), movie2.getYear());
                        }

                    }
                });
                break;

            case "Genre":
                Collections.sort(_movies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie movie1, Movie movie2) {
                        if (ascending) {
                            return movie2.getGenre().toString().compareTo(movie1.getGenre().toString());
                        } else {
                            return movie1.getGenre().toString().compareTo(movie2.getGenre().toString());
                        }

                    }
                });
                break;

            case "Director":
                Collections.sort(_movies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie movie1, Movie movie2) {
                        if (ascending) {
                            return movie2.getDirector().compareTo(movie1.getDirector());
                        } else {
                            return movie1.getDirector().compareTo(movie2.getDirector());
                        }

                    }
                });
                break;
            default:
                System.out.println("Inccorect sorting basis");
                break;
        }
    }

    /**
     * Filters the movie list based on the filter type and value. This is a
     * destructive method that changes
     * the contents of the list
     * 
     * @param filterType Type of the filter criteria. Possible criteria include
     *                   Director, Genre and Year
     * @param filterVal  The specific filter value to search for. For example Year
     *                   2023
     */
    public void filterBy(String filterType, String filterVal) {
        Iterator<Movie> iterator = _movies.iterator();
        while (iterator.hasNext()) {
            Movie movie = iterator.next();
            boolean shouldRemove = false;

            switch (filterType) {
                case "Director":
                    shouldRemove = !movie.getDirector().equals(filterVal);
                    break;
                case "Year":
                    shouldRemove = movie.getYear() != Integer.parseInt(filterVal);
                    break;
                case "Genre":
                    shouldRemove = !(movie.getGenre() == Movie.Genre.valueOf(filterVal));
                    break;
                default:
                    break;
            }

            if (shouldRemove) {
                iterator.remove();
            }
        }
    }

    /**
     * Prints a readable string representation of the total movies per genre that
     * are currently stored in the movie list
     * 
     * @return a string representation of the total movies per genre in the movie
     *         list
     */
    public String printMovieCountByGenre() {
        Map<String, Integer> genreCountMap = new HashMap<>();
        // Count the movies per genre
        for (Movie movie : _movies) {
            Movie.Genre genre = movie.getGenre();
            genreCountMap.put(String.valueOf(genre), genreCountMap.getOrDefault(String.valueOf(genre), 0) + 1);
        }

        // Print the movie count per genre
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : genreCountMap.entrySet()) {
            String genre = entry.getKey();
            int count = entry.getValue();
            result.append(String.valueOf(genre)).append(": ").append(count).append(" movies\n");
            // System.out.println(genre + ": " + count);
        }

        return result.toString();
    }

    /**
     * Prints a readable string representation of the total movies per year that are
     * currently stored in the movie list
     * 
     * @return a string representation of the total movies per year in the movie
     *         list
     */
    public String printMovieCountByYear() {
        Map<String, Integer> yearCountMap = new HashMap<>();
        // Count the movies per genre
        for (Movie movie : _movies) {
            int year = movie.getYear();
            yearCountMap.put(String.valueOf(year), yearCountMap.getOrDefault(String.valueOf(year), 0) + 1);
        }

        // Print the movie count per genre
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : yearCountMap.entrySet()) {
            String genre = entry.getKey();
            int count = entry.getValue();
            result.append(String.valueOf(genre)).append(": ").append(count).append(" movies\n");
            // System.out.println(genre + ": " + count);
        }

        return result.toString();
    }

    /**
     * Prints a readable string representation of the total movies per director that are
     * currently stored in the movie list
     * 
     * @return a string representation of the total movies per director in the movie
     *         list
     */
    public String printMovieCountByDirector() {
        Map<String, Integer> directorCountMap = new HashMap<>();
        // Count the movies per genre
        for (Movie movie : _movies) {
            directorCountMap.put(movie.getDirector(), directorCountMap.getOrDefault(movie.getDirector(), 0) + 1);
        }

        // Print the movie count per genre
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : directorCountMap.entrySet()) {
            String director = entry.getKey();
            int count = entry.getValue();
            result.append(String.valueOf(director)).append(": ").append(count).append(" movies\n");
            // System.out.println(genre + ": " + count);
        }

        return result.toString();
    }

    /**
     * Searchs for a specific movie in the movie list based on the movie's title
     * Returns null if no matching result is found
     * 
     * @param movieTitle the title of the movie to seach for
     * @return associated movie object with the title. Returns null if no matching
     *         movie object is found
     */
    public Movie searchByTitle(String movieTitle) {
        for (Movie movie : _movies) {
            if (movieTitle == movie.getTitle()) {
                return movie;
            }
        }
        return null;
    }

}

// public void printMovies() {
// for (Movie movie : _movies) {
// System.out.println(movie.getTitle() + " year: " + movie.getYear());
// }
// }