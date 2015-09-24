package de.kosch.testcase;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import de.kosch.annotations.SeleneseMethodInfo;
import de.kosch.listener.Constants;
import de.kosch.runner.SeleneseRunnerBuilder;

public class SeleneseDataProvider {

	@DataProvider(name = Constants.SELENESE_DATAPROVIDER_NAME)
	public static Object[][] resolveRunner(ITestContext testContext, Method method) {
		SeleneseMethodInfo seleneseMethodInfo = method.getAnnotation(SeleneseMethodInfo.class);
		String globalConfParameter = testContext.getCurrentXmlTest().getParameter(Constants.SELENESE_TEST_CONFIG_NAME);
		return new Object[][] {
				{ SeleneseRunnerBuilder.createRunnerIfNotExists(seleneseMethodInfo, globalConfParameter) } };
	}
}
