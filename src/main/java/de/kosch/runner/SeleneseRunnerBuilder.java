package de.kosch.runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.kosch.annotations.SeleneseMethodInfo;
import de.kosch.filehandling.ConfigFileHandler;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.config.IConfig;

public class SeleneseRunnerBuilder {

    private static ThreadLocal<Runner> threadLocalRunner = new ThreadLocal<Runner>();

    public static Runner createRunnerIfNotExists(SeleneseMethodInfo seleneseMethodInfo, String globalConfParameter) {
        if (threadLocalRunner.get() != null) {
            return threadLocalRunner.get();
        }
        threadLocalRunner.set(new Runner());
        ConfigFileHandler configFileHandler = new ConfigFileHandler(globalConfParameter,
            seleneseMethodInfo.configPath());
        IConfig config = configFileHandler.getComposedConfig(addCLIParamters(seleneseMethodInfo));
        new SeleneseRunnerSetup(threadLocalRunner.get(), config).setupRunner();
        return threadLocalRunner.get();
    }

    private static String[] addCLIParamters(SeleneseMethodInfo seleneseMethodInfo) {
        List<String> cliArgs = new ArrayList<String>();
        if (StringUtils.isNoneEmpty(seleneseMethodInfo.driver())) {
            cliArgs.add("-d");
            cliArgs.add(seleneseMethodInfo.driver());
        }
        cliArgs.addAll(Arrays.asList(seleneseMethodInfo.configArgs()));
        return cliArgs.toArray(new String[cliArgs.size()]);
    }

    public static Runner getThreadLocalRunner() {
        return threadLocalRunner.get();
    }
}
