package org.cloudfoundry.samples.music.selenium;

import static org.assertj.core.api.Assertions.assertThat;

import org.cloudfoundry.samples.music.selenium.model.Album;
import org.cloudfoundry.samples.music.selenium.pages.AlbumsPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AlbumTest {

  private WebDriver driver;
  private Album album;

  @BeforeEach
  public void setUp() {
    driver = new EdgeDriver();
    driver.manage().window().maximize();
  }

  @AfterEach
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  @Order(0)
  public void addAlbumTest() {
    AlbumsPage albumsPage = new AlbumsPage(driver);
    albumsPage.open();

    album = new Album("Горгород", "Oxxxymiron", 2015, "Хип-хоп");
    albumsPage.addAlbum(album);

    assertThat(albumsPage.getAlbums()).contains(album);
  }

  @Test
  @Order(1)
  public void updateAlbumTest() {
    AlbumsPage albumsPage = new AlbumsPage(driver);
    albumsPage.open();

    Album newAlbum = new Album("Красота и уродство", album.getArtist(), 2021, album.getGenre());
    albumsPage.updateAlbum(album, newAlbum);

    album = newAlbum;
    assertThat(albumsPage.getAlbums()).contains(album);
  }

  @Test
  @Order(2)
  public void deleteAlbumTest() {
    AlbumsPage albumsPage = new AlbumsPage(driver);
    albumsPage.open();

    albumsPage.deleteAlbum(album);

    assertThat(albumsPage.getAlbums()).doesNotContain(album);
  }
}
