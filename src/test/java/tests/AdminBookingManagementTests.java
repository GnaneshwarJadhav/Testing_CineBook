package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AdminBookingsPage;

public class AdminBookingManagementTests extends BaseTest {

    @Test(groups = {"regression", "admin", "admin-booking-management", "FRD_11_1", "TS_502"},
            description = "TC_501: Verify empty state when no bookings match the selected filters")
    public void TC_501_manageBookingsShowsEmptyStateForNoMatchingFilters() {
        loginAsAdmin();
        AdminBookingsPage page = new AdminBookingsPage(driver).open();

        Assert.assertTrue(page.applyMovieCancelledFilterWithNoMatches(),
                "A movie plus Cancelled status filter combination should yield zero booking rows.");
        Assert.assertEquals(page.emptyRowMessage(), "No bookings match the current filter.",
                "Expected empty-state message should be displayed.");
        Assert.assertEquals(page.ledgerRowCount(), 0, "Ledger table should not display booking rows.");
    }

    @Test(groups = {"regression", "admin", "admin-booking-management", "FRD_11_1", "TS_502"},
            description = "TC_502: Verify booking data is strictly scoped to the admin's theater")
    public void TC_502_manageBookingsDataIsScopedToAdminTheater() {
        loginAsAdmin();
        AdminBookingsPage page = new AdminBookingsPage(driver).open();

        Assert.assertTrue(page.isDisplayed(), "Manage Bookings page should be displayed.");
        Assert.assertTrue(page.hasTableOrEmptyRow(), "Ledger table or empty state should be displayed.");
        Assert.assertTrue(page.allLedgerRowsBelongToLoggedInTheater(),
                "No ledger row should display booking data from another theater.");
    }
}
