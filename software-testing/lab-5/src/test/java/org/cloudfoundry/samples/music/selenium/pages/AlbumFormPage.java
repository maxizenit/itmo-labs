package org.cloudfoundry.samples.music.selenium.pages;

import org.cloudfoundry.samples.music.selenium.model.Album;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AlbumFormPage extends AbstractPage {

  private static final By TITLE = By.id("title");
  private static final By ARTIST = By.id("artist");
  private static final By RELEASE_YEAR = By.id("releaseYear");
  private static final By GENRE = By.id("genre");
  private static final By OK_BUTTON = By.id("ok-button");

  public AlbumFormPage(WebDriver driver) {
    super(driver);
  }

  public void fillFields(Album album) {
    fillInput(TITLE, album.getTitle());
    fillInput(ARTIST, album.getArtist());
    fillInput(RELEASE_YEAR, album.getReleaseYear().toString());
    fillInput(GENRE, album.getGenre());
  }

  public void clickOk() {
    getClickableElement(OK_BUTTON).click();
  }
}
