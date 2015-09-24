package de.kosch.filehandling;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import jp.vmi.selenium.selenese.config.DefaultConfig;
import jp.vmi.selenium.selenese.config.IConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * resolves config properties
 * 
 * @author Schuraev
 *
 */
public class ConfigFileHandler extends ResourceHandler {

    private static final Logger log = LoggerFactory.getLogger(ConfigFileHandler.class);

    static final String TESTNG_SELENESE_CONF_FILENAME = "testng-selenese.conf";

    private static final String[] DEFAULT_CONF_PATHS = new String[] { "default.conf" };


    public ConfigFileHandler(String... paths) {
        super(ArrayUtils.addAll(DEFAULT_CONF_PATHS, paths));
    }

    public IConfig getComposedConfig(String... args) {
        DefaultConfig config = new DefaultConfig(args);
        try {
            File target = new File(FileUtils.getTempDirectory(), TESTNG_SELENESE_CONF_FILENAME);
            config.loadFrom(getJoinedFile(target).getAbsolutePath());
        } catch (IOException | URISyntaxException e) {
            log.error("Failure by loading config files", e);
        }
        return config;
    }

}
