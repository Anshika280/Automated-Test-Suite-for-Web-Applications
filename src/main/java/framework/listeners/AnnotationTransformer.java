package framework.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG Annotation Transformer that dynamically attaches the custom RetryAnalyzer
 * to all @Test methods across the framework without requiring manual annotation parameters.
 */
public class AnnotationTransformer implements IAnnotationTransformer {

    private static final Logger log = LoggerFactory.getLogger(AnnotationTransformer.class);

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        if (annotation.getRetryAnalyzerClass() == null ||
                annotation.getRetryAnalyzerClass().getName().contains("DisabledRetryAnalyzer")) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
