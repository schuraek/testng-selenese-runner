package de.kosch.listener;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import de.kosch.annotations.SeleneseMethodInfo;
import de.kosch.filehandling.SeleneseFileHandler;
import de.kosch.runner.RunnerContext;
import de.kosch.runner.SeleneseRunnerBuilder;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.config.IConfig;
import jp.vmi.selenium.selenese.result.Result;

/**
 * main class for handling seleneses operates and
 * searches in TestMethod after SeleneseMethodInfo annotation
 * 
 * @author Schuraev
 *
 */
public class SeleneseMethodListener implements IInvokedMethodListener {

    private static final Logger log = LoggerFactory.getLogger(SeleneseMethodListener.class);

    @Override
    public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        ITestNGMethod testMethod = invokedMethod.getTestMethod();
        Method method = testMethod.getConstructorOrMethod().getMethod();
        boolean annotationPresent = method != null && method.isAnnotationPresent(SeleneseMethodInfo.class);
        if (!annotationPresent) {
            return;
        }
        SeleneseMethodInfo seleneseMethodInfo = method.getAnnotation(SeleneseMethodInfo.class);
        String globalConfParameter = testMethod.getXmlTest().getParameter(Constants.SELENESE_TEST_CONFIG_NAME);
        RunnerContext runnerContext = SeleneseRunnerBuilder.getRunnerContext(seleneseMethodInfo, globalConfParameter);
        String[] selenesePathsFromMethod = seleneseMethodInfo.preconditionMode() ? seleneseMethodInfo.chainedParents()
            : seleneseMethodInfo.selenesePath();
        IConfig config = runnerContext.getConfig();
        Set<String> composedSelenesePathsSet = Arrays.stream(testMethod.getGroups())
            .map(element -> Paths.get(config.getOptionValue(Constants.SELENESE_TEST_DIR), element).toString())
            .collect(Collectors.toSet());
        String[] composedGroupPaths = composedSelenesePathsSet.toArray(new String[composedSelenesePathsSet.size()]);
        String[] selenesePaths = resolveSelenesePaths(composedGroupPaths, selenesePathsFromMethod);
        log.info("Start: " + testMethod.getMethodName());
        Runner runner = runnerContext.getRunner();
        Result result = runner.run(selenesePaths);
        if (result.isFailed()) {
            throw new AssertionError(result.getMessage());
        }
    }

    /**
     * locates all seleneses depends groups and annotation
     * 
     * @param groups
     * @param selenesePathsFromMethod
     * @return
     */
    protected String[] resolveSelenesePaths(String[] groups, String[] selenesePathsFromMethod) {
        SeleneseFileHandler seleneseFileHandler = new SeleneseFileHandler(groups, selenesePathsFromMethod);
        Set<String> absolutePathsSet = Arrays.stream(seleneseFileHandler.getAllFiles())
            .map(file -> file.getAbsolutePath()).collect(Collectors.toSet());
        if (absolutePathsSet.isEmpty())
            new IllegalStateException("no seleneses were founded");
        return absolutePathsSet.toArray(new String[absolutePathsSet.size()]);
    }

    @Override
    public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        ITestNGMethod testMethod = invokedMethod.getTestMethod();
        Method method = testMethod.getConstructorOrMethod().getMethod();
        boolean annotationPresent = method != null && method.isAnnotationPresent(SeleneseMethodInfo.class);
        if (!annotationPresent) {
            return;
        }
        SeleneseMethodInfo seleneseMethodInfo = method.getAnnotation(SeleneseMethodInfo.class);
        String globalConfParameter = testMethod.getXmlTest().getParameter(Constants.SELENESE_TEST_CONFIG_NAME);
        Runner runner = SeleneseRunnerBuilder.getThreadLocalRunner(seleneseMethodInfo, globalConfParameter);
        if (runner != null) {
            runner.finish();
        }
    }
}
