package org.cloudfoundry.samples.music.selenium.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractPage {

  protected final WebDriver driver;
  protected final WebDriverWait wait;

  protected AbstractPage(WebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  protected WebElement getClickableElement(By by) {
    return wait.until(ExpectedConditions.elementToBeClickable(by));
  }

  protected WebElement getClickableElement(WebElement source, By by) {
    WebElement webElement = source.findElement(by);
    return wait.until(ExpectedConditions.elementToBeClickable(webElement));
  }

  protected WebElement getVisibleElement(By by) {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  protected void fillInput(By by, String value) {
    WebElement input = getClickableElement(by);
    input.clear();
    input.sendKeys(value);
    wait.until(ExpectedConditions.attributeToBe(by, "value", value));
  }
}
