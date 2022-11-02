package entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static utils.Helper.formatAsTable;

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

  public String getUrl() {
    return "https://www.themoviedb.org/movie/" + this.id;
  }

  @Override
  public String toString() {

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Title:", this.title));
    rows.add(Arrays.asList("Runtime:", this.runtime + " minutes"));
    rows.add(Arrays.asList("Synopsis:", this.synopsis));
    rows.add(Arrays.asList("Review Rating:", Double.toString(this.overallRating) + " /" + Double.toString(5)));
    rows.add(Arrays.asList("Content Rating:", this.contentRating.toString()));
    rows.add(Arrays.asList("Showing Status:", this.showStatus.toString()));
    rows.add(Arrays.asList("Blockbuster Status:", (this.isBlockbuster ? "BLOCKBUSTER" : "NON-BLOCKBUSTER")));
    rows.add(Arrays.asList("Directed By:", this.director));
    rows.add(Arrays.asList("Cast:", this.castList.toString()));
    rows.add(Arrays.asList("Link:", this.getUrl()));

    return formatAsTable(rows);
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

  public enum ShowStatus {

    COMING_SOON("Coming Soon"),
    PREVIEW("Preview"),
    NOW_SHOWING("Now Showing"),
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

  public enum ContentRating {
    PR, // (general) – Suitable for all ages.
    PG, // (parental guidance) – Suitable for most but parents should guide their young.
    PG13, // (parental guidance 13) – Suitable for persons aged 13 and above but parental guidance is advised for children below 13.
    NC16, // (no children under 16) – Restricted to persons over 16 years of age.
    M18, // (mature 18) – Restricted to persons 18 years and above.
    R21, // (restricted 21) – Strictly for adults aged 21 and above. Films under this category are restricted to be screened in licensed venues only.
  }
}
