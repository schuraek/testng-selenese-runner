package de.kosch.listener;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import de.kosch.annotations.SeleneseMethodInfo;
import de.kosch.filehandling.SeleneseFileHandler;
import de.kosch.runner.SeleneseRunnerBuilder;
import jp.vmi.selenium.selenese.Runner;
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
		String[] selenesePaths = resolveSelenesePaths(testMethod.getGroups(), seleneseMethodInfo);
		log.info("Start: " + testMethod.getMethodName());
		Result result = SeleneseRunnerBuilder.createRunnerIfNotExists(seleneseMethodInfo, globalConfParameter).run(selenesePaths);
		if (result.isFailed()) {
			throw new AssertionError(result.getMessage());
		}
	}


	
	/**
	 * locates all seleneses depends groups and annotation
	 * 
	 * @param groups
	 * @param seleneseMethodInfo
	 * @return
	 */
	protected String[] resolveSelenesePaths(String[] groups, SeleneseMethodInfo seleneseMethodInfo) {
		String[] selenesePath = seleneseMethodInfo.preconditionMode() ? seleneseMethodInfo.chainedParents()
				: seleneseMethodInfo.selenesePath();
		SeleneseFileHandler seleneseFileHandler = new SeleneseFileHandler(groups, selenesePath);
		String[] selenesePaths = seleneseFileHandler.getAllFilePaths();
		if (selenesePaths.length == 0)
			new IllegalStateException("no seleneses were founded");
		return selenesePaths;
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		Runner runner = SeleneseRunnerBuilder.getThreadLocalRunner();
		if (runner != null) {
			runner.finish();
		}
	}
}
