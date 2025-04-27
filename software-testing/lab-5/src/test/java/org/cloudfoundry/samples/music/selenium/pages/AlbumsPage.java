package org.cloudfoundry.samples.music.selenium.pages;

import java.util.List;
import org.cloudfoundry.samples.music.selenium.model.Album;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AlbumsPage extends AbstractPage {

  private static final By ADD_ALBUM_BUTTON = By.id("add-album-button");
  private static final By LIST_BUTTON = By.id("list-button");

  private final AlbumFormPage albumFormPage;
  private final ListPage listPage;
  private final StatusPage statusPage;

  public AlbumsPage(WebDriver driver) {
    super(driver);
    albumFormPage = new AlbumFormPage(driver);
    listPage = new ListPage(driver);
    statusPage = new StatusPage(driver);
  }

  public void open() {
    driver.get("http://localhost:8080");
  }

  public void addAlbum(Album album) {
    getClickableElement(ADD_ALBUM_BUTTON).click();
    albumFormPage.fillFields(album);
    albumFormPage.clickOk();
  }

  public void updateAlbum(Album oldAlbum, Album newAlbum) {
    openListPage();
    listPage.updateAlbum(oldAlbum, newAlbum);
    statusPage.waitStatusMessageIsAlbumSaved();
  }

  public void deleteAlbum(Album album) {
    openListPage();
    listPage.deleteAlbum(album);
    statusPage.waitStatusMessageIsAlbumDeleted();
  }

  public List<Album> getAlbums() {
    openListPage();
    return listPage.getAlbums();
  }

  private void openListPage() {
    getClickableElement(LIST_BUTTON).click();
  }
}
