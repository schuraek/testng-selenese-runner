package de.kosch.listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.webdriver.WebDriverManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer2;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.annotations.IConfigurationAnnotation;
import org.testng.annotations.IDataProviderAnnotation;
import org.testng.annotations.IFactoryAnnotation;
import org.testng.annotations.ITestAnnotation;

import de.kosch.annotations.SeleneseMethodInfo;
import de.kosch.testcase.SeleneseDataProvider;

public class SeleneseSuiteListener implements ISuiteListener, IAnnotationTransformer2 {

    private static final Logger log = LoggerFactory.getLogger(SeleneseMethodListener.class);

    @Override
    public void onStart(ISuite suite) {
        suite.addListener(new SeleneseMethodListener());
    }

    @Override
    public void onFinish(ISuite suite) {
        WebDriverManager.quitDriversOnAllManagers();
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        boolean annotationPresent = testMethod != null && testMethod.isAnnotationPresent(SeleneseMethodInfo.class);
        if (!annotationPresent) {
            return;
        }
        if (StringUtils.isNotEmpty(annotation.getDataProvider())) {
            log.info("Method: " + testMethod + " has already instance from dataprovider "
                + annotation.getDataProvider());
        }
        Parameter[] parameters = testMethod.getParameters();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                if (parameter.getType().equals(Runner.class)) {
                    annotation.setDataProviderClass(SeleneseDataProvider.class);
                    annotation.setDataProvider(SeleneseDataProvider.SELENESE_DATAPROVIDER_NAME);
                    break;
                }
            }
        }
    }

    @Override
    public void transform(IConfigurationAnnotation annotation, Class testClass, Constructor testConstructor,
        Method testMethod) {
        System.out.println("TEST 2");
    }

    @Override
    public void transform(IDataProviderAnnotation annotation, Method method) {
        System.out.println("TEST 3");

    }

    @Override
    public void transform(IFactoryAnnotation annotation, Method method) {
        System.out.println("TEST 4");

    }

}
