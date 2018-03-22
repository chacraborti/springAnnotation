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
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
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

    Logger root;

    @Mock
    private Appender mockAppender;

    @Before
    public void setUp() throws Exception {
        root = LogManager.getRootLogger();
        root.addAppender(mockAppender);
        root.setLevel(Level.INFO);
    }


    @Test
    public void logIfAnnotationPresent() throws InvocationTargetException, IllegalAccessException {
        TestAnnotated testAnnotated = new TestAnnotatedImpl();
        TestAnnotated objectBefore = (TestAnnotated) postProcessor.postProcessBeforeInitialization(testAnnotated, "NAME");
        TestAnnotated objectAfter = (TestAnnotated) postProcessor.postProcessAfterInitialization(objectBefore, "NAME");
        objectAfter.doSmth();
        verify(mockAppender, times(1)).doAppend(captorLoggingEvent.capture());
        List<LoggingEvent> loggingEventAllValues = captorLoggingEvent.getAllValues();
        LoggingEvent loggingEvent0 = loggingEventAllValues.get(0);
        assertThat("", loggingEvent0.getRenderedMessage(), containsString("Method"));
        assertThat("", loggingEvent0.getRenderedMessage(), containsString(testAnnotated.getClass().getName()));
        assertThat("", Level.INFO, is(loggingEvent0.getLevel()));

//        Method[] methods = TestAnnotated.class.getMethods();
//        List<Method> methodsAnnotatedList = new ArrayList<>();
//        for (int i = 0; i < methods.length; i++) {
//            if (methods[i].isAnnotationPresent(MethodTimeCount.class)) {
//                Method method = methods[i];
//                methodsAnnotatedList.add(method);
//                method.invoke(objectAfter);
//            }
//        }
//        assertThat(methodsAnnotatedList.size(), new GreaterThan<>(0));

    }

    @Test
    public void LogNothingIfAnnotationNotPresent() {
        TestAnnotated testAnnotated = new TestAnnotatedImpl();
        TestAnnotated objectBefore = (TestAnnotated) postProcessor.postProcessBeforeInitialization(testAnnotated, "NAME");
        TestAnnotated objectAfter = (TestAnnotated) postProcessor.postProcessAfterInitialization(objectBefore, "NAME");

        objectAfter.doSmthElse();
        verify(mockAppender, times(0)).doAppend(captorLoggingEvent.capture());
    }

    @Test
    public void LogNothingOverloadedMethodWithNoAnnotation() {
        TestAnnotated testAnnotated = new TestAnnotatedImpl();
        TestAnnotated objectBefore = (TestAnnotated) postProcessor.postProcessBeforeInitialization(testAnnotated, "NAME");
        TestAnnotated objectAfter = (TestAnnotated) postProcessor.postProcessAfterInitialization(objectBefore, "NAME");
        objectAfter.doSmth("Test");
        verify(mockAppender, times(0)).doAppend(captorLoggingEvent.capture());
    }

    @Test
    public void LogNothingMethodsWithoutAnnotationWithTheSameNameInAnotherClass() {
        TestAnnotated testAnnotated = new TestAnnotatedImpl();
        TestAnnotated objectBefore = (TestAnnotated) postProcessor.postProcessBeforeInitialization(testAnnotated, "NAME");
        TestAnnotated objectAfter = (TestAnnotated) postProcessor.postProcessAfterInitialization(objectBefore, "NAME");
        AnotherTestAnnotated anotherTestAnnotated = new AnotherTestAnnotated();
        anotherTestAnnotated.doSmth();
        verify(mockAppender, times(0)).doAppend(captorLoggingEvent.capture());
    }


    class TestAnnotatedImpl implements TestAnnotated {

        @MethodTimeCount
        @Override
        public void doSmth() {
        }

        @Override
        public void doSmth(String s) {
        }

        @Override
        public void doSmthElse() {
        }
    }

    public interface TestAnnotated {


        void doSmth();

        void doSmth(String s);

        void doSmthElse();
    }

    class AnotherTestAnnotated {

        public void doSmth() {
        }

        public void doSmth(String s) {
        }

        public void doSmthElse() {
        }
    }
}