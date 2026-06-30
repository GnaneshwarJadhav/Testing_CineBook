package listeners;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ExtentReportManager;

public class TestListener implements ITestListener {
    private static final Logger LOGGER = LogManager.getLogger(TestListener.class);
    private static final ExtentReports EXTENT = ExtentReportManager.getReporter();
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = EXTENT.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        extentTest.assignCategory(result.getMethod().getGroups());
        TEST.set(extentTest);
        LOGGER.info("Starting test: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TEST.get().pass("Passed");
        LOGGER.info("Passed: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Screenshot is captured in BaseTest.tearDown() before the driver is quit,
        // so it is reliably available here for both regular and data-provider tests.
        String screenshot = BaseTest.FAILURE_SCREENSHOT.get();
        if (screenshot == null) {
            screenshot = "";
        }
        TEST.get().fail(result.getThrowable());
        if (!screenshot.isBlank()) {
            TEST.get().addScreenCaptureFromPath(screenshot);
        }
        LOGGER.error("Failed: {}", result.getMethod().getMethodName(), result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        TEST.get().skip(result.getThrowable() == null ? "Skipped" : result.getThrowable().getMessage());
        LOGGER.info("Skipped: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        EXTENT.flush();
    }
}
