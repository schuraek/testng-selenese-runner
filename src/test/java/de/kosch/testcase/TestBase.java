package de.kosch.testcase;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.kosch.annotations.SeleneseMethodInfo;
import jp.vmi.selenium.selenese.Runner;

@Test
public class TestBase {


    @SeleneseMethodInfo(driver ="HTMLUNIT", selenesePath = "TestLogin.html")
	@Test(groups = "kundenverwaltung")
	public void test1(Runner runner) {
        long threadId = Thread.currentThread().getId();
        System.err.println("THREAD_ID 1TEST " +threadId);
		Assert.assertEquals(true, true);
	}

    @SeleneseMethodInfo(selenesePath = "ArtikelLoeschen.html")
    @Test(groups = "artikelverwaltung")
    public void test2() {
        long threadId = Thread.currentThread().getId();
        System.err.println("THREAD_ID 2TEST " +threadId);
        Assert.assertEquals(true, true);
    }
    
    
    @SeleneseMethodInfo(driver ="FIREFOX", selenesePath = "TestLogin.html")
	@Test(groups = "kundenverwaltung")
	public void test3(Runner runner) {
        long threadId = Thread.currentThread().getId();
        System.err.println("THREAD_ID 3 TEST " +threadId);
		Assert.assertEquals(true, true);
	}

}
