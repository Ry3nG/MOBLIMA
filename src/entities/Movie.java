package entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static utils.Helper.formatAsTable;

/**
 * Encapsulates movie details
 *
 * @author Crystal Cheong
 * @version 1.0
 */
public class Movie {
  private int id;
  private String title;
  private String synopsis;
  private String director;
  private List<String> castList;
  private int runtime;
  private LocalDate releaseDate;
  private boolean isBlockbuster;
  private ShowStatus showStatus;
  private ContentRating contentRating;
  private double overallRating;

  /**
   * Default constructor
   *
   * @param id:int
   * @param title:String
   * @param synopsis:String
   * @param director:String
   * @param castList:List<String>
   * @param runtime:int
   * @param releaseDate:LocalDate
   * @param isBlockbuster:boolean
   * @param showStatus:ShowStatus
   * @param contentRating:ContentRating
   * @param overallRating:double
   */
  public Movie(int id, String title, String synopsis, String director, List<String> castList, int runtime, LocalDate releaseDate, boolean isBlockbuster, ShowStatus showStatus, ContentRating contentRating, double overallRating) {
    this.id = id;
    this.title = title;
    this.synopsis = synopsis;
    this.director = director;
    this.castList = castList;
    this.runtime = runtime;
    this.releaseDate = releaseDate;
    this.isBlockbuster = isBlockbuster;
    this.showStatus = showStatus;
    this.contentRating = contentRating;
    this.overallRating = overallRating;
  }

  /**
   * Clone constructor
   *
   * @param cloneMovie:Movie
   */
  public Movie(Movie cloneMovie) {
    this(
        cloneMovie.id,
        cloneMovie.title,
        cloneMovie.synopsis,
        cloneMovie.director,
        cloneMovie.castList,
        cloneMovie.runtime,
        cloneMovie.releaseDate,
        cloneMovie.isBlockbuster,
        cloneMovie.showStatus,
        cloneMovie.contentRating,
        cloneMovie.overallRating
    );
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSynopsis() {
    return synopsis;
  }

  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  public String getDirector() {
    return director;
  }

  public void setDirector(String director) {
    this.director = director;
  }

  public List<String> getCastList() {
    return castList;
  }

  public void setCastList(List<String> castList) {
    this.castList = castList;
  }

  public int getRuntime() {
    return runtime;
  }

  public void setRuntime(int runtime) {
    this.runtime = runtime;
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  public boolean isBlockbuster() {
    return isBlockbuster;
  }

  public void setBlockbuster(boolean blockbuster) {
    isBlockbuster = blockbuster;
  }

  public ShowStatus getShowStatus() {
    return showStatus;
  }

  public void setShowStatus(ShowStatus showStatus) {
    this.showStatus = showStatus;
  }

  public ContentRating getContentRating() {
    return contentRating;
  }

  public void setContentRating(ContentRating contentRating) {
    this.contentRating = contentRating;
  }

  public double getOverallRating() {
    return overallRating;
  }

  public void setOverallRating(double overallRating) {
    this.overallRating = overallRating;
  }

  /**
   * Retrieves the TMDB link of the Movie<br/>
   * Example: <a href="https://www.themoviedb.org/movie/634649">Spider-Man: No Way Home</a>
   *
   * @return link:String
   */
  public String getUrl() {
    return "https://www.themoviedb.org/movie/" + this.id;
  }

  /**
   * Pretty print Movie object
   *
   * @return strMovie:String
   */
  @Override
  public String toString() {

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Title:", this.title));
    rows.add(Arrays.asList("Runtime:", this.runtime + " minutes"));
    rows.add(Arrays.asList("Synopsis:", this.synopsis));
    rows.add(Arrays.asList("Review Rating:", this.overallRating + " /" + Double.toString(5)));
    rows.add(Arrays.asList("Content Rating:", this.contentRating.toString()));
    rows.add(Arrays.asList("Showing Status:", this.showStatus.toString()));
    rows.add(Arrays.asList("Blockbuster Status:", (this.isBlockbuster ? "BLOCKBUSTER" : "NON-BLOCKBUSTER")));
    rows.add(Arrays.asList("Directed By:", this.director));
    rows.add(Arrays.asList("Cast:", this.castList.toString()));
    rows.add(Arrays.asList("Link:", this.getUrl()));

    return formatAsTable(rows);
  }

  /**
   * Compares Movie ID to determine object equality
   *
   * @param obj:Object
   * @return isEqual:boolean
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Movie && ((Movie) obj).id == (this.id);
  }

  @Override
  public int hashCode() {
    int prime = 31;
    return prime + Objects.hashCode(this.id);
  }

  /**
   * Constant values of all possible Movie showing status
   *
   * @author Crystal Cheong
   * @version 1.0
   */
  public enum ShowStatus {

    COMING_SOON("Coming Soon"),
    PREVIEW("Preview"),
    NOW_SHOWING("Now Showing"),
    END_SHOWING("End of Showing");

    private final String displayName;

    /**
     * Default constructor to initialize Enum value with display name
     *
     * @param displayName:String
     */
    ShowStatus(String displayName) {
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  /**
   * Constant values of all possible film classification ratings<br/>
   * Reference: <a href="https://www.imda.gov.sg/regulations-and-licensing-listing/content-standards-and-classification/standards-and-classification/films">IMDA Film Classification Ratings</a>
   *
   * @author Crystal Cheong
   * @version 1.0
   */
  public enum ContentRating {
    /**
     * General - Suitable for all ages
     */
    G,
    /**
     * Parental Guidance - Suitable for most but parents should guide their young.
     */
    PG,
    /**
     * Parental Guidance 13 - Suitable for persons aged 13 and above but parent guidance is advised for children below 13.
     */
    PG13,
    /**
     * No Children under 16 - Restricted to persons over 16 years of age.
     */
    NC16,
    /**
     * Mature 18 - Restricted to persons 18 years and above.
     */
    M18,
    /**
     * Restricted 21 - Strictly for adults aged 12 and above. Films under this category are restricted to be screened in licensed venues only.
     */
    R21,
  }
}
