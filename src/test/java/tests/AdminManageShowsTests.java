package tests;

import base.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AdminShowsPage;

public class AdminManageShowsTests extends BaseTest {


    @BeforeMethod
    public void loginasAdmin(){
        loginAsAdmin();
    }

    @Test(groups = {"smoke"})
    public void checkingFilters(){
        AdminShowsPage page = new AdminShowsPage(driver).open();
        page.switchFilters();

    }
}
