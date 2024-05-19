import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.*;

public class TestNG {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass(alwaysRun = true)
    public void init()
    {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get("https://makeup.com.ua/ua/");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void searchFieldTest()
    {
        WebElement searchButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        driver.findElement(
                                By.xpath("/html/body/div[1]/div[2]/header/div[2]/div/div[1]/div[2]")
                        )
                )
        );
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].click();", searchButton);

        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(
                        driver.findElement(
                                By.id("search-input")
                        )
                )
        );

        javascriptExecutor.executeScript("arguments[0].value='lamel';", searchInput);

        searchInput.sendKeys(Keys.ENTER);

        WebElement searchResult = wait.until(
                ExpectedConditions.elementToBeClickable(
                        driver.findElement(
                                By.xpath("/html/body/div[1]/div[2]/div/div/div[1]/strong")
                        )
                )
        );

        //expected count can be different. it is equals to 111 for the date that test has been written
        assertEquals(searchResult.getText(), "111");
    }

    @Test
    public void authorizationTest() {
        driver.get("https://makeup.com.ua/ua/");
        loginInMakeup();

        WebElement authorizedElement = wait.until(
                ExpectedConditions.elementToBeClickable(
                    driver.findElement(
                            By.className("authorized")
                    )
            )
        );
        authorizedElement.click();

        WebElement pageHeader = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.className("page-header")
                )
        );
        assertTrue(pageHeader.getText().contains("Контактна інформація"));
    }

    @Test(dependsOnMethods = {"authorizationTest"})
    public void addToFavourites() {
        driver.get("https://makeup.com.ua/ua/");

        WebElement productLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.className("simple-slider-list__name")
                )
        );
        productLink.click();

        WebElement favouriteButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.className("product__to-favourite")
                )
        );
        String favButtonClassName = favouriteButton.getAttribute("class");
        if (favButtonClassName.contains("active")) {
            favouriteButton.click();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
                fail();
            }
        }
        favouriteButton.click();

        WebElement favouritePopup = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.className("favourite-popup")
                )
        );

        WebElement specifyMessageBlock = favouritePopup.findElement(
                By.className("specify-message-block")
        );
        wait.until(
                ExpectedConditions.textToBePresentInElement(
                        specifyMessageBlock,
                        "Хочете отримувати сповіщення про зниження ціни на товар?"
                )
        );

        WebElement secondaryButton = favouritePopup.findElement(By.className("button_secondary"));
        wait.until(ExpectedConditions.elementToBeClickable(secondaryButton));
        secondaryButton.click();
    }

    @Test(dependsOnMethods = {"authorizationTest"})
    public void changeUserInfo() {
        driver.get("https://makeup.com.ua/ua/");

        WebElement authorizedElement = wait.until(ExpectedConditions.elementToBeClickable(By.className("authorized")));
        authorizedElement.click();

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameField.clear();
        nameField.sendKeys("Rename");

        WebElement surnameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("surname")));
        surnameField.clear();
        surnameField.sendKeys("Surname");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("private-office__submit-button")));
        submitButton.click();

        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup__window")));

        WebElement closeIcon = wait.until(ExpectedConditions.elementToBeClickable(popup.findElement(By.className("close-icon"))));
        closeIcon.click();

        WebElement nameValue = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        assertEquals(nameValue.getAttribute("value"), "Rename");

        WebElement surnameValue = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("surname")));
        assertEquals(surnameValue.getAttribute("value"), "Surname");
    }

    @Test(dependsOnMethods = {"authorizationTest"})
    public void subscribeToNewsletter() {
        driver.get("https://makeup.com.ua/ua/");

        WebElement authorizedElement = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.className("authorized")
                )
        );
        authorizedElement.click();

        WebElement footerSubscribeHeader = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.className("footer-subscribe-header")
                )
        );
        assertTrue(footerSubscribeHeader.isDisplayed());

        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("footer-subscribe-email")
                )
        );
        emailInput.sendKeys("jocah62329@nimadir.com");

        WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.className("footer-submit")
                )
        );
        submitButton.click();

        WebElement popupContent = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id=\"popup__window\"]")
                )
        );
        assertTrue(popupContent.isDisplayed());
    }


    protected void loginInMakeup() {
        WebElement headerOffice = wait.until(ExpectedConditions.elementToBeClickable(By.className("header-office")));
        headerOffice.click();

        WebElement loginField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login")));
        loginField.sendKeys("jocah62329@nimadir.com");

        WebElement passwordField = driver.findElement(By.id("pw"));
        passwordField.sendKeys("Test!123");
        passwordField.submit();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("authorized")));
    }
}
