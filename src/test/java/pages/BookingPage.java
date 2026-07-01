package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ConfigReader;

import java.util.List;

public class BookingPage extends BasePage {
    private final By page = id("booking-page");
    private final By confirmButton = id("booking-confirm");
    private final By total = id("booking-total");
    private final By seatCount = id("booking-seat-count");
    private final By allSeats = By.cssSelector("button[id^='seat-']");
    private final By bookedSeats = By.cssSelector(
            "[id^='seat-'].booked, [id^='seat-'].seat-booked, [id^='seat-'][data-booked='true'], [id^='seat-'][aria-disabled='true']"
    );
    private final By shows = By.cssSelector("[id^='show-']:not([disabled])");
    private final By ticketPrice = By.cssSelector(".text-base.font-bold.text-tomato-600");
    private final By totalPrice = By.id("booking-total");
    private final By seatsLeft = By.cssSelector("p[class='text-[0.65rem] font-semibold uppercase tracking-wider text-gray-500']");
    public BookingPage(WebDriver driver) {
        super(driver);
    }

    public BookingPage waitForBookingPage() {
        wait.until(ExpectedConditions.urlContains("/book"));
        visible(page);
        return this;
    }

    public BookingPage open(String path) {
        driver.get(ConfigReader.baseUrl() + path);
        visible(page);
        return this;
    }

    public void openMoviesAndBook(String movieId) {
        visible(id("movies-page"));
        //WebElement element = driver.findElement(By.id(movieId));
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("window.scrollBy(0, 700);");
        visible(By.id(movieId));
        click(By.id(movieId));
        waitForBookingPage();
    }

    public boolean isDisplayed() {
        return isVisible(page);
    }

    public boolean selectFirstShowIfPresent() {
        visible(shows);
        List<WebElement> showButtons = visibleElements(shows).stream()
                .filter(WebElement::isEnabled)
                .toList();
        if (showButtons.isEmpty()) {
            return false;
        }
        click(showButtons.get(0));
        return true;
    }

    public boolean selectFirstAvailableSeat() {
        visible(allSeats);
        List<WebElement> seats = driver.findElements(allSeats).stream()
                .filter(WebElement::isEnabled)
                .toList();
        if (seats.isEmpty()) {
            return false;
        }
        click(seats.get(0));
        return true;
    }

    public boolean hasSelectedSeatSummary() {
        return isVisible(seatCount) && !text(seatCount).isBlank() && isVisible(total);
    }

    public boolean hasConfirmButton() {
        return isVisible(confirmButton);
    }

    public void proceedToPay() {
        driver.findElement(confirmButton).click();
    }

    public boolean waitForErrorOrStillOnBooking() {
        return currentUrl().contains("/book");
    }

    public boolean navigatedToPaymentOrSuccess() {
        wait.until(driver -> {
            String url = driver.getCurrentUrl().toLowerCase();
            return url.contains("checkout.stripe") || url.contains("/payment/") || url.contains("stripe.com");
        });
        return true;
    }

    public int findEnabledSeats() {
        visible(bookedSeats);
        List<WebElement> seats = driver.findElements(allSeats);
        int enabledCount = 0;

        for (WebElement seat : seats) {
            if (seat.isEnabled() && !isSeatMarkedDisabled(seat)) {
                enabledCount++;
            }
        }

        return enabledCount;
    }

    public List<WebElement> getBookedSeats() {
        visible(bookedSeats);
        return driver.findElements(bookedSeats);
    }

    public boolean areBookedSeatsDisabled() {
        List<WebElement> seats = getBookedSeats();
        if (seats.isEmpty()) {
            return false;
        }

        for (WebElement seat : seats) {
            if (!isSeatMarkedDisabled(seat) || seat.isEnabled()) {
                return false;
            }
        }

        return true;
    }


    private boolean isSeatMarkedDisabled(WebElement seat) {
        String disabledAttr = seat.getAttribute("disabled");
        String ariaDisabled = seat.getAttribute("aria-disabled");
        String className = seat.getAttribute("class");

        return disabledAttr != null
                || "true".equalsIgnoreCase(ariaDisabled)
                || (className != null && (
                className.contains("disabled")
                        || className.contains("booked")
                        || className.contains("seat-booked")));
    }

    public String getTicketPrice(){
        visible(ticketPrice);
        return driver.findElement(ticketPrice).getText();
    }

    public String getTotalPrice(){
        visible(totalPrice);
        return driver.findElement(totalPrice).getText();
    }

    public String getSeatsLeftText(){
        visible(seatsLeft);
        return driver.findElement(seatsLeft).getText();
    }

    public String getCurrentUrl(){
        return driver.getCurrentUrl();
    }

    public void goToUrl(String url){
        driver.get(url);
    }

    public boolean proceedClickable(){
        return driver.findElement(confirmButton).isEnabled();
    }

    public WebElement getFirstBookedSeat() {
        try {
            visible(bookedSeats); // wait until at least one booked seat is visible
            List<WebElement> seats = driver.findElements(bookedSeats);

            return seats.isEmpty() ? null : seats.get(0);
        } catch (Exception e) {
            return null; // no booked seats found within wait period
        }
    }

    public void selectBookedSeat(String id){
        visible(By.id(id));
        driver.findElement(By.id(id)).click();
    }

}
