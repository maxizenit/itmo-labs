package org.cloudfoundry.samples.music.selenium.pages;

import java.util.List;
import java.util.NoSuchElementException;
import org.cloudfoundry.samples.music.selenium.model.Album;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ListPage extends AbstractPage {

  private static final By ALBUMS_TABLE = By.id("albums-table");
  private static final By ROWS = By.cssSelector("tbody tr");
  private static final By TD = By.tagName("td");
  private static final By DROPDOWN_TOGGLE = By.id("dropdown-toggle");
  private static final By UPDATE_ACTION = By.id("update-action");
  private static final By DELETE_ACTION = By.id("delete-action");

  private final AlbumFormPage albumFormPage;

  public ListPage(WebDriver driver) {
    super(driver);
    albumFormPage = new AlbumFormPage(driver);
  }

  public List<Album> getAlbums() {
    List<WebElement> rows = getRows();
    return rows.stream()
        .map(
            row -> {
              List<WebElement> cells = row.findElements(By.tagName("td"));

              String title = cells.get(0).getText().trim();
              String artist = cells.get(1).getText().trim();
              String year = cells.get(2).getText().trim();
              String genre = cells.get(3).getText().trim();

              return new Album(title, artist, Integer.parseInt(year), genre);
            })
        .toList();
  }

  public void updateAlbum(Album oldAlbum, Album newAlbum) {
    WebElement albumRow = getAlbumRow(oldAlbum);
    clickDropdownAction(albumRow, UPDATE_ACTION);
    albumFormPage.fillFields(newAlbum);
    albumFormPage.clickOk();
  }

  public void deleteAlbum(Album album) {
    WebElement albumRow = getAlbumRow(album);
    clickDropdownAction(albumRow, DELETE_ACTION);
  }

  private List<WebElement> getRows() {
    WebElement table = getVisibleElement(ALBUMS_TABLE);
    return table.findElements(ROWS);
  }

  private WebElement getAlbumRow(Album album) {
    List<WebElement> rows = getRows();
    return rows.parallelStream()
        .filter(
            row -> {
              List<String> fields = row.findElements(TD).stream().map(WebElement::getText).toList();
              return fields.get(0).equals(album.getTitle())
                  && fields.get(1).equals(album.getArtist())
                  && fields.get(2).equals(album.getReleaseYear().toString())
                  && fields.get(3).equals(album.getGenre());
            })
        .findAny()
        .orElseThrow(NoSuchElementException::new);
  }

  private void clickDropdownAction(WebElement albumRow, By action) {
    getClickableElement(albumRow, DROPDOWN_TOGGLE).click();
    getClickableElement(albumRow, action).click();
  }
}
