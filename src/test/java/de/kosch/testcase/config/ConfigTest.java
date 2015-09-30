package de.kosch.testcase.config;

import jp.vmi.selenium.selenese.Runner;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.kosch.annotations.SeleneseMethodInfo;
import de.kosch.testcase.TestBase;

public class ConfigTest extends TestBase{


    @SeleneseMethodInfo(driver ="HTMLUNIT", selenesePath = "FormsInput.html")
    @Test(groups = "config")
    public void testDriverOverrideDriverHTMLUnit(Runner runner) {
        long threadId = Thread.currentThread().getId();
        System.err.println("THREAD_ID: " +threadId);
        Assert.assertEquals(true, true);
    }
    
    @SeleneseMethodInfo(driver ="FIREFOX", selenesePath = "FormsInput.html")
    @Test(groups = "config")
    public void testDriverOverrideFirefox(Runner runner) {
        long threadId = Thread.currentThread().getId();
        System.err.println("THREAD_ID: " +threadId);
        Assert.assertEquals(true, true);
    }

}
