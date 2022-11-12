package moblima.entities;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static moblima.utils.Helper.formatAsTable;

/**
 * The type Movie.
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
   * Instantiates a new Movie.
   *
   * @param id            the id
   * @param title         the title
   * @param synopsis      the synopsis
   * @param director      the director
   * @param castList      the cast list
   * @param runtime       the runtime
   * @param releaseDate   the release date
   * @param isBlockbuster the is blockbuster
   * @param showStatus    the show status
   * @param contentRating the content rating
   * @param overallRating the overall rating
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
   * Instantiates a new Movie.
   *
   * @param cloneMovie the clone movie
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

  /**
   * Gets id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets title.
   *
   * @param title the title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets synopsis.
   *
   * @return the synopsis
   */
  public String getSynopsis() {
    return synopsis;
  }

  /**
   * Sets synopsis.
   *
   * @param synopsis the synopsis
   */
  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  /**
   * Gets director.
   *
   * @return the director
   */
  public String getDirector() {
    return director;
  }

  /**
   * Sets director.
   *
   * @param director the director
   */
  public void setDirector(String director) {
    this.director = director;
  }

  /**
   * Gets cast list.
   *
   * @return the cast list
   */
  public List<String> getCastList() {
    return castList;
  }

  /**
   * Sets cast list.
   *
   * @param castList the cast list
   */
  public void setCastList(List<String> castList) {
    this.castList = castList;
  }

  /**
   * Gets runtime.
   *
   * @return the runtime
   */
  public int getRuntime() {
    return runtime;
  }

  /**
   * Sets runtime.
   *
   * @param runtime the runtime
   */
  public void setRuntime(int runtime) {
    this.runtime = runtime;
  }

  /**
   * Gets release date.
   *
   * @return the release date
   */
  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  /**
   * Sets release date.
   *
   * @param releaseDate the release date
   */
  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * Is blockbuster boolean.
   *
   * @return the boolean
   */
  public boolean isBlockbuster() {
    return isBlockbuster;
  }

  /**
   * Sets blockbuster.
   *
   * @param blockbuster the blockbuster
   */
  public void setBlockbuster(boolean blockbuster) {
    isBlockbuster = blockbuster;
  }

  /**
   * Gets show status.
   *
   * @return the show status
   */
  public ShowStatus getShowStatus() {
    return showStatus;
  }

  /**
   * Sets show status.
   *
   * @param showStatus the show status
   */
  public void setShowStatus(ShowStatus showStatus) {
    this.showStatus = showStatus;
  }

  /**
   * Gets content rating.
   *
   * @return the content rating
   */
  public ContentRating getContentRating() {
    return contentRating;
  }

  /**
   * Sets content rating.
   *
   * @param contentRating the content rating
   */
  public void setContentRating(ContentRating contentRating) {
    this.contentRating = contentRating;
  }

  /**
   * Gets overall rating.
   *
   * @return the overall rating
   */
  public double getOverallRating() {
    return overallRating;
  }

  /**
   * Sets overall rating.
   *
   * @param overallRating the overall rating
   */
  public void setOverallRating(double overallRating) {
    this.overallRating = overallRating;
  }

  /**
   * Gets url.
   *
   * @return the url
   */
  public String getUrl() {
    return "https://www.themoviedb.org/movie/" + this.id;
  }

  /**
   * To string rows list.
   *
   * @param truncate          the truncate
   * @param showOverallRating the show overall rating
   * @return the list
   */
  public List<List<String>> toStringRows(boolean truncate, boolean showOverallRating) {
    int maxCast = 3;
    boolean isNotTruncated = (this.castList.size() <= maxCast);
    List<String> subCastList = isNotTruncated ? this.castList : this.castList.subList(0, maxCast);
    String displayCastList = subCastList.toString().replace("[", "").replace("]", "");
    if (!isNotTruncated) {
      int excessCastCount = this.castList.size() - subCastList.size();
      if (excessCastCount > 0) displayCastList += " & " + excessCastCount + " others";
    }

    String displaySynopsis = truncate ? StringUtils.abbreviate(this.synopsis, 150) : this.synopsis;
    String displayRating = (this.overallRating == 0 || !showOverallRating) ? "NA" : this.overallRating + " /" + Double.toString(5);

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Title:", this.title));
    rows.add(Arrays.asList("Runtime:", this.runtime + " minutes"));
    rows.add(Arrays.asList("Synopsis:", displaySynopsis));
    rows.add(Arrays.asList("Review Rating:", displayRating));
    rows.add(Arrays.asList("Content Rating:", this.contentRating.toString()));
    rows.add(Arrays.asList("Showing Status:", this.showStatus.toString()));
    rows.add(Arrays.asList("Blockbuster Status:", (this.isBlockbuster ? "BLOCKBUSTER" : "NON-BLOCKBUSTER")));
    rows.add(Arrays.asList("Directed By:", this.director));
    rows.add(Arrays.asList("Cast:", displayCastList));
    rows.add(Arrays.asList("Link:", this.getUrl()));

    return rows;
  }

  /**
   * To string string.
   *
   * @param truncate the truncate
   * @return the string
   */
  public String toString(boolean truncate) {
    return formatAsTable(toStringRows(truncate, true));
  }

  @Override
  public String toString() {
    return this.toString(false);
  }

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
   * The enum Show status.
   */
  public enum ShowStatus {

    /**
     * The Coming soon.
     */
    COMING_SOON("Coming Soon"),
    /**
     * Preview show status.
     */
    PREVIEW("Preview"),
    /**
     * The Now showing.
     */
    NOW_SHOWING("Now Showing"),
    /**
     * The End showing.
     */
    END_SHOWING("End of Showing");

    private final String displayName;

    ShowStatus(String displayName) {
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  /**
   * The enum Content rating.
   */
  public enum ContentRating {
    /**
     * G content rating.
     */
    G,
    /**
     * Pg content rating.
     */
    PG,
    /**
     * Pg 13 content rating.
     */
    PG13,
    /**
     * Nc 16 content rating.
     */
    NC16,
    /**
     * M 18 content rating.
     */
    M18,
    /**
     * R 21 content rating.
     */
    R21,
  }
}
