package org.cloudfoundry.samples.music.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class StatusPage extends AbstractPage {

  private static final By STATUS_MESSAGE = By.id("status-message");

  private static final String ALBUM_SAVED = "Album saved";
  private static final String ALBUM_DELETED = "Album deleted";

  public StatusPage(WebDriver driver) {
    super(driver);
  }

  public void waitStatusMessageIsAlbumSaved() {
    waitStatusMessage(ALBUM_SAVED);
  }

  public void waitStatusMessageIsAlbumDeleted() {
    waitStatusMessage(ALBUM_DELETED);
  }

  private void waitStatusMessage(String statusMessage) {
    wait.until(ExpectedConditions.textToBe(STATUS_MESSAGE, statusMessage));
  }
}
