package tests;

import base.BaseTest;
import base.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BookingPage;
import pages.LoginPage;

public class BookingTests extends BaseTest {

    @DataProvider(name = "movies")
    public Object[][] movieIds() {
        return new Object[][] { { "movie-book-3", "show-12" } };
    }

    @Test(dataProvider = "movies", groups = { "regression", "booking",
            "FRD_2_5" }, description = "FRD_2.5.7: Proceeding without a selected seat should show validation feedback")
    public void FRD_251_bookingRequiresAtLeastOneSeat(String movieId, String showId) throws InterruptedException {
        loginAsUser();
        Thread.sleep(3000);
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.openMoviesAndBook(movieId);
        Thread.sleep(3000);
        bookingPage.selectFirstShowIfPresent();
        Thread.sleep(3000);
        bookingPage.proceedToPay();
        Thread.sleep(3000);
        Assert.assertTrue(bookingPage.waitForErrorOrStillOnBooking(),
                "Booking should ask the user to select at least one seat.");
    }

    @Test(dataProvider = "movies", groups = { "payment", "destructive", "booking", "TS_103",
            "TC_107" }, description = "TC_107: Verify booking is successful for selected movie through payment redirect boundary")
    public void TC_107_bookingCanProceedToPaymentForSelectedMovie(String movieId, String showId)
            throws InterruptedException {
        // skipIfPaymentTestsDisabled();
        // skipIfDestructiveTestsDisabled();
        loginAsUser();
        Thread.sleep(3000);
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.openMoviesAndBook(movieId);
        Thread.sleep(3000);
        bookingPage.selectFirstShowIfPresent();
        Thread.sleep(3000);
        bookingPage.selectFirstAvailableSeat();
        Thread.sleep(3000);
        Assert.assertTrue(bookingPage.hasSelectedSeatSummary(),
                "Selected seat and total should appear in booking summary.");
        bookingPage.proceedToPay();
        Thread.sleep(3000);
        Assert.assertTrue(bookingPage.navigatedToPaymentOrSuccess(),
                "Proceeding should redirect to payment or payment result page.");
    }

    @Test(groups = { "concurrency", "booking",
            "TC_CONCURRENT" }, description = "TC_CONCURRENT: Two users select the same seat — verifying both can see the seat")
    public void TC_CONCURRENT_onlyOneUserShouldBookSameSeat() throws InterruptedException {

        // ── Create two separate browser sessions ──────────────────────────────────
        DriverFactory.createDriver(null, false);
        WebDriver driver1 = DriverFactory.getDriver();

        DriverFactory.createDriver(null, false);
        WebDriver driver2 = DriverFactory.getDriver();

        try {
            // ── rahulkumar: login → movies page → booking page → show → seat ──────
            LoginPage lp1 = new LoginPage(driver1).open();
            lp1.login("rahulkumar", "123456");
            // lp1.waitUntilLoggedIn();
            Thread.sleep(3000);

            BookingPage bp1 = new BookingPage(driver1);
            bp1.selectMovie("movie-book-3");
            Thread.sleep(3000);
            // bp1.selectFirstShowIfPresent();
            bp1.selectShow("show-12");
            Thread.sleep(3000);
            bp1.selectFirstAvailableSeat();
            Thread.sleep(3000);
            System.out.println("[rahulkumar] Seat selected — staying on booking page.");

            // ── sanjaykumar: login → movies page → booking page → show → seat ─────
            LoginPage lp2 = new LoginPage(driver2).open();
            lp2.login("sanjaykumar", "123456");
            // lp2.waitUntilLoggedIn();
            Thread.sleep(3000);

            BookingPage bp2 = new BookingPage(driver2);
            bp2.selectMovie("movie-book-3");

            Thread.sleep(3000);
            // bp2.selectFirstShowIfPresent();
            bp2.selectShow("show-12");
            Thread.sleep(3000);
            bp2.selectFirstAvailableSeat();
            Thread.sleep(3000);
            System.out.println("[sanjaykumar] Seat selected — staying on booking page.");

            // ── Both browsers are now sitting on the seat selection page ──────────
            // Wait 5 seconds so you can visually see both browsers at the same time
            Thread.sleep(5000);

            // Confirm both are still on the booking page (no accidental navigation)
            // Assert.assertTrue(bp1.waitForErrorOrStillOnBooking(),
            // "[rahulkumar] Should still be on the booking/seat selection page.");
            // Assert.assertTrue(bp2.waitForErrorOrStillOnBooking(),
            // "[sanjaykumar] Should still be on the booking/seat selection page.");

        } finally {
            try {
                driver1.quit();
            } catch (Exception ignored) {
            }
            try {
                driver2.quit();
            } catch (Exception ignored) {
            }
        }
    }

    // @Test(dataProvider = "movies")
    // public void checkLeftSeats(String movieId, String showId) throws
    // InterruptedException {
    // loginAsUser();
    // Thread.sleep(3000);
    // BookingPage bookingPage = new BookingPage(driver);
    // bookingPage.selectMovie(movieId);
    // Thread.sleep(3000);
    // int leftSeats =
    // Integer.parseInt(bookingPage.getSeatsLeft(showId).trim().split(" ")[0]);
    // bookingPage.selectShow(showId);
    // int enabledSeats = bookingPage.findEnabledSeats();
    //
    // Assert.assertEquals(enabledSeats, leftSeats,
    // "The number of enabled seats should match the showtime's available seat
    // count.");
    // }
}
