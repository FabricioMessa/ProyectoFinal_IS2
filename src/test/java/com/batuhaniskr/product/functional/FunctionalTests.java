package com.batuhaniskr.product.functional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionalTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true); 
        options.addArguments("--no-sandbox"); 
        options.addArguments("--disable-dev-shm-usage"); 
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginPage_ShouldLoad() {
        driver.get(BASE_URL + "/login");
        WebElement form = driver.findElement(By.tagName("form"));
        assertThat(form).isNotNull();
    }
    @org.junit.Ignore
    @Test
    public void testLogin_WithValidCredentials_ShouldRedirectToProducts() {
        driver.get(BASE_URL + "/login");

        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        WebElement passwordField = driver.findElement(By.name("password"));

        usernameField.sendKeys("admin@admin.com");
        passwordField.sendKeys("admin");
        
        passwordField.submit();

        wait.until(ExpectedConditions.urlContains("/products"));
        assertThat(driver.getCurrentUrl()).contains("/products");
    }

    @Test
    public void testProductsPage_RequiresAuthentication() {
        driver.get(BASE_URL + "/products");
        wait.until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    public void testAddProductPage_RequiresAuthentication() {
        driver.get(BASE_URL + "/products/add");
        wait.until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    public void testAccessDenied_ForUnauthorizedUser() {
        driver.get(BASE_URL + "/access-denied");
        WebElement body = driver.findElement(By.tagName("body"));
        assertThat(body.getText()).isNotEmpty();
    }
}