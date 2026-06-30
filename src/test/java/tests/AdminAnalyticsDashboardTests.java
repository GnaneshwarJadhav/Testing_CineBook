package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.AnalyticsPage;

public class AdminAnalyticsDashboardTests extends BaseTest {

    @Test(groups = {"regression", "admin", "admin-analytics", "FRD_12_1", "TS_503"},
            description = "TC_503: Verify default Analytics dashboard metrics and sections")
    public void TC_503_analyticsDashboardShowsDefaultMetricsAndSections() {
        loginAsAdmin();
        AnalyticsPage page = new AnalyticsPage(driver).open();

        Assert.assertTrue(page.isDisplayed(), "Analytics dashboard page should be displayed.");
        Assert.assertTrue(page.hasKpis(), "Revenue, seats sold, upcoming shows, and cancellation KPIs should be visible.");
        Assert.assertTrue(page.hasDashboardSections(),
                "Ticket Bookings, Show Occupancy, Top Performing Shows, Top Rated Movies, and Audience Interest sections should be visible.");
    }

    @Test(groups = {"regression", "admin", "admin-analytics", "FRD_12_1", "TS_503"},
            description = "TC_504: Verify the functionality of the Refresh button")
    public void TC_504_analyticsRefreshRefetchesWithoutPageReload() {
        loginAsAdmin();
        AnalyticsPage page = new AnalyticsPage(driver).open();

        String beforeRefreshUrl = page.currentUrl();
        String beforeRefreshMetrics = page.metricSnapshot();
        Assert.assertFalse(beforeRefreshMetrics.isBlank(), "Current metric values should be visible before refresh.");

        page.refresh();

        Assert.assertEquals(page.currentUrl(), beforeRefreshUrl, "Refresh should not require or trigger navigation.");
        Assert.assertTrue(page.hasKpis(), "Analytics KPI cards should remain visible after refresh.");
        Assert.assertFalse(page.metricSnapshot().isBlank(), "Metric values should be visible after refresh.");
    }

    @Test(groups = {"regression", "admin", "admin-analytics", "FRD_12_1", "TS_503"},
            description = "TC_505: Verify analytics filtering and grouping controls")
    public void TC_505_analyticsFilteringAndGroupingControlsWork() {
        loginAsAdmin();
        AnalyticsPage page = new AnalyticsPage(driver).open();

        Assert.assertTrue(page.movieFilterIsAvailable(), "All movies filter should be visible.");
        if (!page.selectFirstMovieFilterIfAvailable()) {
            throw new SkipException("No movie options are available for analytics filter validation.");
        }

        page.selectGrouping("hour");
        Assert.assertTrue(page.ticketBookingsHeaderText().contains("Ticket Bookings"),
                "Ticket Bookings section should remain visible after selecting By hour.");

        page.selectGrouping("day");
        Assert.assertTrue(page.ticketBookingsHeaderText().contains("Ticket Bookings"),
                "Ticket Bookings section should remain visible after selecting By day.");

        page.selectGrouping("week");
        Assert.assertTrue(page.ticketBookingsHeaderText().contains("Ticket Bookings"),
                "Ticket Bookings section should remain visible after selecting By week.");
    }

    @Test(groups = {"known-defect", "DF_001", "admin", "admin-analytics", "FRD_12_1", "TS_504"},
            description = "TC_506: Verify empty states for specific analytics sections")
    public void TC_506_analyticsShowsEmptyStatesForRatingsAndInterest() {
        loginAsAdmin();
        AnalyticsPage page = new AnalyticsPage(driver).open();

        Assert.assertTrue(page.isDisplayed(), "Analytics dashboard page should be displayed.");
        Assert.assertTrue(page.hasNoRatingsEmptyState(), "Top Rated Movies should show the no-ratings empty state.");
        Assert.assertTrue(page.hasNoAudienceInterestEmptyState(),
                "Audience Interest should show the no-interest empty state.");
    }
}
