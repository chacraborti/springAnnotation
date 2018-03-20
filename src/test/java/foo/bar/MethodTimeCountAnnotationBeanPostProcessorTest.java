package foo.bar;

import foo.bar.annotation.MethodTimeCount;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.internal.matchers.GreaterThan;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
//@ContextConfiguration("classpath:spring-config.xml")
public class MethodTimeCountAnnotationBeanPostProcessorTest {

    private MethodTimeCountAnnotationBeanPostProcessor postProcessor =
            new MethodTimeCountAnnotationBeanPostProcessor();

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @Mock
    private Appender mockAppender;

    @Before
    public void setUp() throws Exception {
        Logger root = LogManager.getRootLogger();
        root.addAppender(mockAppender);
        root.setLevel(Level.INFO);
    }


    @Test
    public void logIfAnnotationPresent() throws InvocationTargetException, IllegalAccessException {
        TestAnnotated testAnnotated = new TestAnnotatedImpl();
        Object objectBefore = postProcessor.postProcessBeforeInitialization(testAnnotated, "NAME");
        TestAnnotated objectAfter = (TestAnnotated) postProcessor.postProcessAfterInitialization(objectBefore, "NAME");
//        objectAfter.doSmth();

        Method [] methods = TestAnnotated.class.getMethods();
        List<Method> methodsAnnotatedList = new ArrayList<>();
        for (int i = 0; i < methods.length; i++) {
            if(methods[i].isAnnotationPresent(MethodTimeCount.class)) {
                methods[i].invoke(objectAfter);
                methodsAnnotatedList.add(methods[i]);
            }
        }
        assertThat(methodsAnnotatedList.size(), new GreaterThan<>(0));
        verify(mockAppender, times(methodsAnnotatedList.size())).doAppend(captorLoggingEvent.capture());
        for (Method method: methodsAnnotatedList) {
            List<LoggingEvent> loggingEventAllValues = captorLoggingEvent.getAllValues();
            LoggingEvent loggingEvent0 = loggingEventAllValues.get(0);
            assertThat("", loggingEvent0.getRenderedMessage(), containsString("Method"));
            assertThat("", loggingEvent0.getRenderedMessage(), containsString(method.getName()));
            assertThat("", Level.INFO, is(loggingEvent0.getLevel()));
        }
    }

    @Test
    public void LogNothingIfAnnotationNotPresent() {
        TestAnnotated testAnnotated = new TestAnnotatedImpl();
        Object objectBefore = postProcessor.postProcessBeforeInitialization(testAnnotated, "NAME");
        TestAnnotated objectAfter = (TestAnnotated) postProcessor.postProcessAfterInitialization(objectBefore, "NAME");
        objectAfter.doSmth("TEST");
        objectAfter.doSmthElse();

        Method [] methods = testAnnotated.getClass().getMethods();
        List<Method> methodsNotAnnotatedList = new ArrayList<>();
        for (int i = 0; i < methods.length; i++) {
            if(!methods[i].isAnnotationPresent(MethodTimeCount.class)) {
                methodsNotAnnotatedList.add(methods[i]);
            }
        }
        assertThat(methodsNotAnnotatedList.size(), new GreaterThan<>(0));

        for (Method method: methodsNotAnnotatedList) {
            verify(mockAppender, times(0));
        }

    }

    @Test
    public void LogNothingOverloadedMethodWithNoAnnotation() {
        TestAnnotated testAnnotated = new TestAnnotatedImpl();
        Object objectBefore = postProcessor.postProcessBeforeInitialization(testAnnotated, "NAME");
        Object objectAfter = postProcessor.postProcessAfterInitialization(objectBefore, "NAME");
        testAnnotated.doSmth();

        verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
        List<LoggingEvent> loggingEventAllValues = captorLoggingEvent.getAllValues();
        LoggingEvent loggingEvent0 = loggingEventAllValues.get(0);
        assertThat("", loggingEvent0.getRenderedMessage(), containsString("Method"));
        assertThat("", loggingEvent0.getRenderedMessage(), containsString(testAnnotated.getClass().getMethods()[0].getName()));
        assertThat("", Level.INFO, is(loggingEvent0.getLevel()));
    }

    @Test
    public void LogNothingMethodsWithoutAnnotationWithTheSameNameInAnotherClass() {

    }


    class TestAnnotatedImpl implements TestAnnotated {

        @MethodTimeCount
        public void doSmth(){}

        @Override
        public void doSmth(String s){}

        @Override
        public void doSmthElse(){}
    }
}