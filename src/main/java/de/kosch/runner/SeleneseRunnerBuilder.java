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

    private static ThreadLocal<RunnerContext> threadLocalRunner = new ThreadLocal<RunnerContext>();

    private static Runner createRunnerIfNotExists(SeleneseMethodInfo seleneseMethodInfo, String globalConfParameter) {
        if (threadLocalRunner.get() != null) {
            return threadLocalRunner.get().getRunner();
        }
        String configPath = seleneseMethodInfo == null ? StringUtils.EMPTY : seleneseMethodInfo.configPath();
        ConfigFileHandler configFileHandler = new ConfigFileHandler(globalConfParameter, configPath);
        String[] cliParamters = addCLIParamters(seleneseMethodInfo);
        IConfig config = configFileHandler.getComposedConfig(cliParamters);
        Runner runner = new Runner();
        threadLocalRunner.set(new RunnerContext(runner, config));
        new SeleneseRunnerSetup(runner, config).setupRunner();
        return runner;
    }

    private static String[] addCLIParamters(SeleneseMethodInfo seleneseMethodInfo) {
        if (seleneseMethodInfo == null) {
            return new String[0];
        }
        List<String> cliArgs = new ArrayList<String>();
        if (StringUtils.isNoneEmpty(seleneseMethodInfo.driver())) {
            cliArgs.add("-d");
            cliArgs.add(seleneseMethodInfo.driver());
        }
        cliArgs.addAll(Arrays.asList(seleneseMethodInfo.configArgs()));
        return cliArgs.toArray(new String[cliArgs.size()]);
    }

    public static Runner getThreadLocalRunner(SeleneseMethodInfo seleneseMethodInfo, String globalConfParameter) {
        return getRunnerContext(seleneseMethodInfo, globalConfParameter).getRunner();
    }

    public static RunnerContext getRunnerContext(SeleneseMethodInfo seleneseMethodInfo, String globalConfParameter) {
        createRunnerIfNotExists(seleneseMethodInfo, globalConfParameter);
        return threadLocalRunner.get();

    }

}
