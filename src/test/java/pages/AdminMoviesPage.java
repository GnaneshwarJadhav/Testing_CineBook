package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;
import java.util.ArrayList;

import java.util.List;

import static org.openqa.selenium.By.xpath;

public class AdminMoviesPage extends BasePage {
    private final By title = id("manage-movies-title");
    private final By form = id("mm-form");
    private final By titleInput = id("mm-input-title");
    private final By genreInput = id("mm-input-genre");
    private final By durationInput = id("mm-input-duration");
    private final By languageInput=id("mm-input-languages");
    private final By posterInput = id("mm-input-poster");
    private final By trailerInput = id("mm-input-trailer");
    private final By priceInput = id("mm-input-price");
    private final By submitButton = id("mm-submit");
    private final By table = id("mm-table");
    private final By emptyTable = id("mm-table-empty");
    private  final By movieTitlesLocator=By.xpath("//td[starts-with(@id, 'manage-movies-row-title-')]");

    public AdminMoviesPage(WebDriver driver) {
        super(driver);
    }

    public AdminMoviesPage open() {
        driver.get(ConfigReader.baseUrl() + "/manage-movies");
        visible(title);
        return this;
    }

    public List<String> getAllMovieTitles() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(movieTitlesLocator));

        List<WebElement> elements = driver.findElements(movieTitlesLocator);
        List<String> titles = new ArrayList<>();

        for (WebElement element : elements) {
            String text = element.getText().trim();
            if (!text.isEmpty()) {
                titles.add(text);
            }
        }
        return titles;
    }

    public void fillDetails(String title,String genre,String duration,String language,String posterUrl,String trailerUrl,String price){
        type(titleInput,title);
        type(genreInput,genre);
        type(languageInput,language);
        type(durationInput,duration);
        type(posterInput,posterUrl);
        type(trailerInput,trailerUrl);
        type(priceInput,price);
    }

    public boolean isDisplayed() {
        return isVisible(title) && isVisible(form);
    }

    public boolean hasMovieFormFields() {
        return isVisible(titleInput) && isVisible(genreInput) && isVisible(durationInput)
                && isVisible(posterInput) && isVisible(trailerInput) && isVisible(priceInput);
    }

    public int getMovieCount() throws InterruptedException {
        Thread.sleep(2000);
        return Integer.parseInt(driver.findElement(By.id("mm-counter-value")).getText());
    }

    public String getFormErrormsg() throws InterruptedException {
        //Thread.sleep(3000);
        return driver.findElement(By.id("mm-form-error")).getText();
    }
    public void submitEmptyForm() {
        click(submitButton);
    }

    public boolean formStillDisplayed() {
        return isVisible(form);
    }

    public boolean hasTableOrEmptyState() {
        return isVisible(table) || isVisible(emptyTable);
    }
}
