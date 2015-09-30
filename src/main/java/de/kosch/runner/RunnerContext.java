package de.kosch.runner;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.config.IConfig;

public class RunnerContext {
    private final Runner runner;
    private final IConfig config;

    public RunnerContext(Runner runner, IConfig config) {
        super();
        this.runner = runner;
        this.config = config;
    }

    public Runner getRunner() {
        return runner;
    }

    public IConfig getConfig() {
        return config;
    }

}
