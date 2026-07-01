package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BookingPage;
import utils.ExcelUtils;

import java.awt.print.Book;
import java.util.List;
import java.util.Map;

public class BookingTests extends BaseTest {

    @DataProvider(name = "movies")
    public Object[][] movieIds() {

        List<Map<String, String>> excelData =
                ExcelUtils.readSheet("BookingData.xlsx", "shows");
        Object[][] data = new Object[excelData.size()][2];

        for (int i = 0; i < excelData.size(); i++) {
            Map<String, String> row = excelData.get(i);

            data[i][0] = row.get("movie-id");
            data[i][1] = row.get("show-id");
        }

        return data;

    }

    @Test(dataProvider = "movies", groups = { "regression", "booking"},
            description = "Proceeding without a selected seat should show validation feedback")
    public void bookingRequiresAtLeastOneSeat(String movieId, String showId) {
        loginAsUser();
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.openMoviesAndBook(movieId);
        if (!bookingPage.selectFirstShowIfPresent()) {
            throw new SkipException("No selectable shows are available for this movie.");
        }
        if (!bookingPage.hasConfirmButton()) {
            throw new SkipException("Booking confirmation button is not available for the selected show.");
        }
        bookingPage.proceedToPay();
        Assert.assertTrue(bookingPage.waitForErrorOrStillOnBooking(),
                "Booking should ask the user to select at least one seat.");
    }

    @Test(dataProvider = "movies", groups = { "booking", "regression"},
            description = "Verify already booked seats are disabled/unselectable.")
    public void alreadyBookedSeatsAreDisabledAndUnselectable(String movieId, String showId) {
        loginAsUser();
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.openMoviesAndBook(movieId);
        if (!bookingPage.selectFirstShowIfPresent()) {
            throw new SkipException("No selectable shows are available for this movie.");
        }
        WebElement firstBookedSeat = bookingPage.getFirstBookedSeat();
        Assert.assertNotNull(firstBookedSeat, "Expected at least one already-booked seat to be present for this show.");
        String seatId = firstBookedSeat.getAttribute("id");
        String currentUrl = bookingPage.getCurrentUrl();
        bookingPage.goToUrl(currentUrl);
        if (!bookingPage.selectFirstShowIfPresent()) {
            throw new SkipException("No selectable shows are available for this movie.");
        }
        bookingPage.selectBookedSeat(seatId);
        boolean clickable = bookingPage.proceedClickable();
        Assert.assertFalse(clickable, "Booked seat should not be selected");
        
    }

    @Test(dataProvider = "movies", groups = { "payment", "destructive", "booking"},
            description = "Verify booking is successful for selected movie through payment redirect boundary")
    public void bookingCanProceedToPaymentForSelectedMovie(String movieId, String showId) {
        loginAsUser();
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.openMoviesAndBook(movieId);
        if (!bookingPage.selectFirstShowIfPresent()) {
            throw new SkipException("No selectable shows are available for this movie.");
        }
        bookingPage.selectFirstAvailableSeat();
        Assert.assertTrue(bookingPage.hasSelectedSeatSummary(),
                "Selected seat and total should appear in booking summary.");
        bookingPage.proceedToPay();
        Assert.assertTrue(bookingPage.navigatedToPaymentOrSuccess(),
                "Proceeding should redirect to payment or payment result page.");
    }


    @Test(dataProvider = "movies", groups = { "regression", "booking"},
            description = "Verify the total price matches the selected movie and show seat")
    public void checkPrice(String movieId, String showId) {
        loginAsUser();
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.openMoviesAndBook(movieId);
        double ticketPrice = Double.parseDouble(bookingPage.getTicketPrice().substring(1));
        if (!bookingPage.selectFirstShowIfPresent()) {
            throw new SkipException("No selectable shows are available for this movie.");
        }
        bookingPage.selectFirstAvailableSeat();
        double actualTotalPrice = Double.parseDouble(bookingPage.getTotalPrice().substring(1));
        double expectedTotalPrice = ticketPrice+ticketPrice*0.18;
        Assert.assertEquals(actualTotalPrice, expectedTotalPrice, "Total Price calculation is incorrect.");
    }

    @Test(dataProvider = "movies", groups = { "regression", "booking"})
    public void checkSeatsLeft(String movieId, String showId){
        loginAsUser();
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.openMoviesAndBook(movieId);
        int expectedSeatsLeft = Integer.parseInt(bookingPage.getSeatsLeftText().split(" ")[0]);
        if (!bookingPage.selectFirstShowIfPresent()) {
            throw new SkipException("No selectable shows are available for this movie.");
        }
        int actualSeatsLeft = bookingPage.findEnabledSeats();
        Assert.assertEquals(actualSeatsLeft, expectedSeatsLeft, "Incorrect seat count in the seat selection page.");

    }
}
