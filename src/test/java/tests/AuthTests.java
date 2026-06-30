package tests;

import base.BaseTest;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.NavbarPage;
import pages.RegisterPage;
import utils.ConfigReader;

import java.time.Duration;

public class AuthTests extends BaseTest {

    @Test(groups = {"sanity", "auth", "FRD_2_1"}, description = "FRD_2.1: Login page should render username, password, and sign-in controls")
    public void FRD_201_loginPageRenders() {
        LoginPage loginPage = new LoginPage(driver).open();
        Assert.assertTrue(loginPage.isDisplayed(), "Login page should be displayed.");
    }

    @Test(groups = {"regression", "auth", "FRD_2_1"}, description = "FRD_2.1.6: Invalid credentials should keep the user on login and show validation/error feedback")
    public void FRD_202_invalidLoginStaysOnLoginPage() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login("invalid_user", "invalid_password");
        Assert.assertTrue(loginPage.waitForErrorOrLoginPage(), "Invalid login should not leave the login page.");
    }

    @Test(groups = {"regression", "auth", "FRD_2_2"}, description = "FRD_2.2: Register page should support moviegoer registration")
    public void FRD_203_registerPageRendersForMoviegoer() {
        RegisterPage registerPage = new RegisterPage(driver).open().chooseMoviegoer();
        Assert.assertTrue(registerPage.isDisplayed(), "Register page should be displayed for moviegoer.");
    }

    @Test(groups = {"regression", "auth", "FRD_2_2"}, description = "FRD_2.2.3: Theater-owner registration should ask for theater name and location")
    public void FRD_204_theaterOwnerRegistrationFieldsAppear() {
        RegisterPage registerPage = new RegisterPage(driver).open().chooseTheaterOwner();
        Assert.assertTrue(registerPage.theaterOwnerFieldsVisible(), "Theater owner fields should be visible.");
    }

    @Test(groups = {"regression", "auth", "security"}, description = "Protected movie catalog route should redirect unauthenticated users to login")
    public void FRD_205_protectedMoviesRouteRedirectsToLogin() {
        driver.get(ConfigReader.baseUrl() + "/movies");
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWaitSeconds", 20)))
                .until(ExpectedConditions.urlContains("/login"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"), "Unauthenticated user should be redirected to login.");
    }

    @Test(groups = {"smoke", "auth", "FRD_2_1"}, description = "FRD_2.1.3-2.1.4: Valid moviegoer credentials should create a logged-in session")
    public void FRD_206_validMoviegoerLoginCreatesSession() {
        loginAsUser();
        Assert.assertTrue(new NavbarPage(driver).isLogoutVisible(), "Logout button should be visible after login.");
    }

    @Test(groups = {"regression", "auth", "FRD_2_1"}, description = "FRD_2.1: Logout should end the session and return to login")
    public void FRD_207_logoutReturnsToLogin() {
        loginAsUser();
        new NavbarPage(driver).logout();
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"), "Logout should return to login.");
    }
}
