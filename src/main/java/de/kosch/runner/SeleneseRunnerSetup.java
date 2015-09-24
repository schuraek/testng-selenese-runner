package de.kosch.runner;

import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.BASEURL;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.COMMAND_FACTORY;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.COOKIE_FILTER;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.DEFAULT_TIMEOUT_MILLISEC;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.DRIVER;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.HIGHLIGHT;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.HTML_RESULT;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.IGNORE_SCREENSHOT_COMMAND;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.ROLLUP;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.SCREENSHOT_ALL;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.SCREENSHOT_DIR;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.SCREENSHOT_ON_FAIL;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.SET_SPEED;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.TIMEOUT;
import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.XML_RESULT;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.config.IConfig;
import jp.vmi.selenium.selenese.log.CookieFilter;
import jp.vmi.selenium.selenese.log.CookieFilter.FilterType;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * handles initial config for selenese runner
 * @author Schuraev
 *
 */
public class SeleneseRunnerSetup {

    private static final Logger log = LoggerFactory.getLogger(SeleneseRunnerSetup.class);
    private Runner runner;
    private IConfig config;
    private WebDriverManager manager;

    public SeleneseRunnerSetup(Runner runner, IConfig config) {
        if (runner == null || config == null) {
            throw new IllegalArgumentException("either runner or config are null");
        }
        this.runner = runner;
        this.config = config;
        String driverName = config.getOptionValue(DRIVER);
        DriverOptions driverOptions = new DriverOptions(config);
        if (driverName == null) {
            if (driverOptions.has(DriverOption.FIREFOX))
                driverName = WebDriverManager.FIREFOX;
            else if (driverOptions.has(DriverOption.CHROMEDRIVER))
                driverName = WebDriverManager.CHROME;
            else if (driverOptions.has(DriverOption.IEDRIVER))
                driverName = WebDriverManager.IE;
            else if (driverOptions.has(DriverOption.PHANTOMJS))
                driverName = WebDriverManager.PHANTOMJS;
        }
        manager = WebDriverManager.newInstance();
        manager.setWebDriverFactory(driverName);
        manager.setDriverOptions(driverOptions);
    }

    /**
     * Setup Runner by configuration.
     *
     * @param runner
     *            Runner object.
     * @param config
     *            configuration.
     * @param filenames
     *            filenames of test-suites/test-cases.
     */
    public Runner setupRunner() {
        setupCommandFactory();
        setupSelenese();
        setupScreenshotsHandling();
        setupCookieFilter();
        setupResults();
        return runner;
    }

    protected void setupSelenese() {
        runner.setDriver(manager.get());
        runner.setWebDriverPreparator(manager);
        if (config.getOptionValueAsBoolean(HIGHLIGHT))
            runner.setHighlight(true);
        int timeout = NumberUtils.toInt(config.getOptionValue(TIMEOUT, DEFAULT_TIMEOUT_MILLISEC));
        if (timeout <= 0)
            throw new IllegalArgumentException("Invalid timeout value. (" + config.getOptionValue(TIMEOUT) + ")");
        runner.setTimeout(timeout);
        int speed = NumberUtils.toInt(config.getOptionValue(SET_SPEED, "0"));
        if (speed < 0)
            throw new IllegalArgumentException("Invalid speed value. (" + config.getOptionValue(SET_SPEED) + ")");
        runner.setInitialSpeed(speed);
        runner.setPrintStream(System.out);

        if (config.hasOption(BASEURL))
            runner.setOverridingBaseURL(config.getOptionValue(BASEURL));
        if (config.hasOption(ROLLUP)) {
            String[] rollups = config.getOptionValues(ROLLUP);
            for (String rollup : rollups)
                runner.getRollupRules().load(rollup);
        }
    }

    protected void setupScreenshotsHandling() {
        if (config.hasOption(SCREENSHOT_DIR))
            runner.setScreenshotDir(config.getOptionValue(SCREENSHOT_DIR));
        if (config.hasOption(SCREENSHOT_ALL))
            runner.setScreenshotAllDir(config.getOptionValue(SCREENSHOT_ALL));
        if (config.hasOption(SCREENSHOT_ON_FAIL))
            runner.setScreenshotOnFailDir(config.getOptionValue(SCREENSHOT_ON_FAIL));
        if (config.getOptionValueAsBoolean(IGNORE_SCREENSHOT_COMMAND))
            runner.setIgnoredScreenshotCommand(true);
    }

    protected void setupResults() {
        if (config.hasOption(XML_RESULT))
            runner.setJUnitResultDir(config.getOptionValue(XML_RESULT));
        if (config.hasOption(HTML_RESULT))
            runner.setHtmlResultDir(config.getOptionValue(HTML_RESULT));
    }

    protected void setupCommandFactory() {
        if (config.hasOption(COMMAND_FACTORY)) {
            String factoryName = config.getOptionValue(COMMAND_FACTORY);
            ICommandFactory factory;
            try {
                Class<?> factoryClass = Class.forName(factoryName);
                factory = (ICommandFactory) factoryClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("invalid user defined command factory: " + factoryName);
            }
            runner.getCommandFactory().registerCommandFactory(factory);
            log.info("Registered: {}", factoryName);
        }
    }

    protected void setupCookieFilter() {
        if (config.hasOption(COOKIE_FILTER)) {
            String cookieFilter = config.getOptionValue(COOKIE_FILTER);
            if (cookieFilter.length() < 2)
                throw new IllegalArgumentException("invalid cookie filter format: " + cookieFilter);
            FilterType filterType;
            switch (cookieFilter.charAt(0)) {
            case '+':
                filterType = FilterType.PASS;
                break;
            case '-':
                filterType = FilterType.SKIP;
                break;
            default:
                throw new IllegalArgumentException("invalid cookie filter format: " + cookieFilter);
            }
            String pattern = cookieFilter.substring(1);
            runner.setCookieFilter(new CookieFilter(filterType, pattern));
        }
    }

}
