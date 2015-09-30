package de.kosch.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import de.kosch.listener.SeleneseMethodListener;
import de.kosch.testcase.webresources.WebServer;

@Test
public class TestBase {

    private static final Logger log = LoggerFactory.getLogger(SeleneseMethodListener.class);

    private WebServer server;

    @BeforeSuite
    public void beforeSuite() {
        server = new WebServer();
        server.start();
        log.info("Server started, base url is " + server.getBaseURL());
        System.out.println("TEST");
    }

    @AfterSuite
    public void afterSuite() {
        server.stop();
    }
}
