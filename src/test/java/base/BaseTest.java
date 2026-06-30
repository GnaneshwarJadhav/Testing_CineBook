package base;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ScreenshotUtils;

public abstract class BaseTest {
    protected WebDriver driver;

    // Holds the failure screenshot path so the listener can retrieve it after the driver is quit.
    public static final ThreadLocal<String> FAILURE_SCREENSHOT = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("") String browser, @Optional("") String headless) {
        ConfigReader.init();
        String headlessOverride = System.getProperty("headless");
        boolean headlessMode = headlessOverride != null && !headlessOverride.isBlank()
                ? Boolean.parseBoolean(headlessOverride)
                : headless == null || headless.isBlank()
                ? ConfigReader.getBoolean("headless", false)
                : Boolean.parseBoolean(headless);
        String browserName = System.getProperty("browser", browser);
        DriverFactory.createDriver(browserName, headlessMode);
        driver = DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Capture screenshot BEFORE quitting the driver so it is available even for
        // data-provider tests where @AfterMethod runs before onTestFailure fires.
        FAILURE_SCREENSHOT.remove();
        if (result.getStatus() == ITestResult.FAILURE
                && ConfigReader.getBoolean("screenshotOnFailure", true)) {
            String path = ScreenshotUtils.capture(DriverFactory.getDriver(), result.getMethod().getMethodName());
            if (!path.isBlank()) {
                FAILURE_SCREENSHOT.set(path);
            }
        }
        DriverFactory.quitDriver();
    }

    protected void loginAsUser() {
        loginWith("user.username", "user.password", "moviegoer");
    }

    protected void loginAsAdmin() {
        loginWith("admin.username", "admin.password", "admin");
    }

    protected void skipIfPaymentTestsDisabled() {
        if (!ConfigReader.getBoolean("allowPaymentTests", false)) {
            throw new SkipException("Payment tests are disabled. Set allowPaymentTests=true to run Stripe redirect coverage.");
        }
    }

    protected void skipIfDestructiveTestsDisabled() {
        if (!ConfigReader.getBoolean("allowDestructiveTests", false)) {
            throw new SkipException("Destructive tests are disabled. Set allowDestructiveTests=true to create/update/cancel data.");
        }
    }

    private void loginWith(String usernameKey, String passwordKey, String roleLabel) {
        String username = ConfigReader.get(usernameKey);
        String password = ConfigReader.get(passwordKey);
        if (ConfigReader.isPlaceholderCredential(username) || ConfigReader.isPlaceholderCredential(password)) {
            throw new SkipException("Set " + usernameKey + " and " + passwordKey + " in config.properties or with -D options to run " + roleLabel + " tests.");
        }
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login(username, password);
        loginPage.waitUntilLoggedIn();
    }
}
