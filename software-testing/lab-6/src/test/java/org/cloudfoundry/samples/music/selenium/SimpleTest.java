package org.cloudfoundry.samples.music.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SimpleTest {

    @Test
    void firstSeleniumTest() {
        // select which driver to use
        WebDriver browser = new FirefoxDriver();

        // visit a page
        browser.get("http://localhost:8080");

        // find an HTML element in the page
        WebElement welcomeHeader = browser.findElement(By.tagName("h1"));

        // assert it contains what we want
        assertThat(welcomeHeader.getText()).isEqualTo("Albums");

        // close the browser and the selenium session
        browser.close();
    }
}