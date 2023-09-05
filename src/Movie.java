import java.io.Serializable;

/**
 * Movie class that defines the movie objects.
 *
 * @author Feras Yahya
 */
public class Movie implements Serializable, Comparable<Movie> {

    /**
     * Enums
     * 
     * Describe the different movie Genres that are available
     */
    public enum Genre{HORROR, ACTION, DRAMA, THRILLER, ROMANCE, COMEDY, ADVENTURE, SCIENCE_FICTION, CRIME, ANIMATION};
    public enum SORT{YEAR, GENRE, DIRECTOR, TITLE};
    public enum FILTER{YEAR, GENRE, DIRECTOR};

    /**
     * Fields
     */
    private Genre genre;
    protected String title;
    private String director;
    private int year;

    /**
     * Parameterized constructor
     * 
     * @param title     the movie's title
     * @param director  the movie's director
     * @param genre     the movie's genre
     * @param year      the year in which the movie was released
     */
    public Movie(String title, String director, Genre genre, int year) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.year = year;
    }

    /**
     * Public methods
    */
    /**
     * Returns the movie's title
     * 
     * @return the title that belongs to that specific movie
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the movie's title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the movie's director
     * 
     * @return the director that belongs to that specific movie
     */
    public String getDirector() {
        return director;
    }

    /**
     * Sets the movie's director
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * Returns the movie's genre
     * 
     * @return the genre in which the movie falls in
     */
    public Genre getGenre() {
        return genre;
    }

    /**
     * Sets the movie's genre
     */
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    /**
     * Returns the movie's year of release
     * 
     * @return the year in which the movie was released
     */
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Returns the movie's information as a string 
     * 
     * @return a string representation of the movie's information
     */
    @Override
    public String toString() {
        return "Title: " + title + "\nDirector: " + director + "\nGenre: " + genre + "\nYear: " + year;
    }

    /**
     * Compares two movie objects based on their titles
     * 
     * @param movie the Movie object being compared to the original movie 
     * 
     * @return an integer that indicates the result of the comparison. 0 indicates movie titles are equal,
     * 1 indicates original movie objects is less than the compared movie, -1 indicates that original movie object is greater than 
     * the compared movie
     */
    @Override
    public int compareTo(Movie movie) {

        // Objects are not of the same type

        // Compare them based on name
        return this.genre.compareTo(movie.genre);
    }
}