package com.harshil.logger.controller.aspect;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.harshil.logger.controller.annotation.Logging;
import com.harshil.logger.controller.annotation.NoLogging;
import com.harshil.logger.controller.utils.JsonUtil;

//@formatter:off
/**
 * This class is responsible for performing logging on Controller methods.
 * It used Spring AspectJ (not native Spring AOP) for weaving logging logic into matching controller methods.
 *
 * <p>Currently weaving is performed at runtime to maintain simplicity
 * as this eliminates need of AspectJ compiler. This can be changed to use compile-time
 * weaving to gain some performance benefit.
 *
 * <p><b>NOTE -</b> one need to pass the <code>-parameters</code> flag to Java compiler to preserver
 * formal parameter names in class files. Without this one cannot obtain method argument names via Reflections.
 * The <code>maven-compiler-plugin</code> is configured in <code>pom.xml</code> to pass this flag to compiler.
 *
 * <p>This aspect uses two annotations - {@link Logging}
 * and {@link NoLogging} to gain fine-grain control over
 * method logging behavior. The two annotations can be used on class-level or methods-level.
 *
 * When applied on method-level they override behavior specified on class-level. <br>
 * For example - <code>@Logging</code> on class and <code>@NoLogging</code> on method effectively
 * turns off logging for that particular method.
 *

 * @see <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html">
 *          Spring Documentation on Aspect Oriented Programming with Spring
 *      </a>
 */
//@formatter:on
//@Aspect
public abstract class ControllerAspect {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ControllerAspect.class);

    /**
     * Logs following data on INFO level about executing method -
     * <ul>
     * <li>Method name</li>
     * <li>Method argument name-value pair</li>
     * <li>Request details including referrer, HTTP method, URI and username</li>
     * </ul>
     *
     * @param proceedingJoinPoint the joinpoint object representing the target method
     */
    private static void logPreExecutionData(
            @Nonnull ProceedingJoinPoint proceedingJoinPoint,
            @Nullable RequestMapping methodRequestMapping) {
        MethodSignature methodSignature = (MethodSignature)proceedingJoinPoint.getSignature();

        String methodName = methodSignature.getName() + "()";
        Object argValues[] = proceedingJoinPoint.getArgs();
        String argNames[] = methodSignature.getParameterNames();
        String logContext = "dummy log context";
        Annotation annotations[][] = methodSignature.getMethod().getParameterAnnotations();

        StringBuilder preMessage = new StringBuilder().append(methodName);

        if (argValues.length > 0) {
            generateArgumentLogStatement(argNames, argValues, preMessage, annotations, methodRequestMapping);
        }

        preMessage.append(" called via ").append(logContext);
        LOG.info(preMessage.toString());
    }

    /**
     * Logs following data on INFO level about executed method -
     * <ul>
     * <li>Execution time of method in milliseconds</li>
     * </ul>
     *
     * Logs following data on DEBUG level about executed method -
     * <ul>
     * <li>JSON representation of object returned by method</li>
     * </ul>
     *
     * @param proceedingJoinPoint the jointpoint denoting the executed method
     * @param timer {@link StopWatch} object containing execution time of method
     * @param result the object returned by executed method
     * @param returnType class name of object returned by executed method
     */
    private static void logPostExecutionData(
            @Nonnull ProceedingJoinPoint proceedingJoinPoint,
            @Nonnull StopWatch timer,
            @Nullable Object result,
            @Nonnull String returnType,
            @Nullable RequestMapping methodRequestMapping,
            @Nullable RequestMapping classRequestMapping) {
        MethodSignature methodSignature = (MethodSignature)proceedingJoinPoint.getSignature();
        String methodName = methodSignature.getName() + "()";

        LOG.info(methodName + " took [" + timer.getTime() + " ms] to complete");

        if (LOG.isDebugEnabled()) {
            boolean needsSerialization = false;

            String produces[] = methodRequestMapping != null ? methodRequestMapping.produces() : new String[0];
            for (String produce : produces) {
                if (produce.equals(MediaType.APPLICATION_JSON_VALUE)) {
                    needsSerialization = true;
                    break;
                }
            }

            if (!needsSerialization) {
                produces = classRequestMapping != null ? classRequestMapping.produces() : new String[0];
                for (String produce : produces) {
                    if (produce.equals(MediaType.APPLICATION_JSON_VALUE)) {
                        needsSerialization = true;
                        break;
                    }
                }
            }

            StringBuilder postMessage = new StringBuilder().append(methodName).append(" returned: [");

            if (needsSerialization) {
                String resultClassName = result == null ? "null" : result.getClass().getName();
                resultClassName = returnType.equals("void") ? returnType : resultClassName;
                serialize(result, resultClassName, postMessage);
            } else {
                postMessage.append(result);
            }
            postMessage.append("]");
            LOG.debug(postMessage.toString());
        }
    }

    /**
     * Generated name-value pair of method's formal arguments. Appends the generated string in provided
     * {@link StringBuilder}
     *
     * @param argNames String[] containing method's formal argument names Order of names must correspond to order on arg
     *            values in argValues.
     * @param argValues String[] containing method's formal argument values. Order of values must correspond to order on
     *            arg names in argNames.
     * @param stringBuilder the {@link StringBuilder} to append argument data to.
     */
    private static void generateArgumentLogStatement(
            @Nonnull String[] argNames,
            @Nonnull Object[] argValues,
            @Nonnull StringBuilder stringBuilder,
            @Nonnull Annotation annotations[][],
            @Nullable RequestMapping methodRequestMapping) {
        boolean someArgNeedsSerialization = false;

        if (methodRequestMapping != null) {
            for (String consumes : methodRequestMapping.consumes()) {
                if (consumes.equals(MediaType.APPLICATION_JSON_VALUE)) {
                    someArgNeedsSerialization = true;
                    break;
                }
            }
        }

        stringBuilder.append(" called with arguments: ");

        for (int i = 0, length = argNames.length; i < length; ++i) {
            boolean needsSerialization = false;

            if (someArgNeedsSerialization) {
                // We only need to serialize a param if @RequestBody annotation is found.
                for (Annotation annotation : annotations[i]) {
                    if (annotation instanceof RequestBody) {
                        needsSerialization = true;
                        break;
                    }
                }
            }

            stringBuilder.append(argNames[i]).append(": [");
            if (needsSerialization) {
                String argClassName = argValues[i] == null ? "NULL" : argValues[i].getClass().getName();
                serialize(argValues[i], argClassName, stringBuilder);
            } else {
                stringBuilder.append(argValues[i]);
            }
            stringBuilder.append("]").append(i == (length - 1) ? "" : ", ");
        }
    }

    /**
     * Converts given object to its JSON representation via {@link JsonUtil}. The serialized JSON to then appended to
     * passed {@link StringBuilder} instance.
     *
     * <p>
     * Some exceptional cases -
     * <ol>
     * <li>For objects of file type the file size in bytes is printed.</li>
     * <li>Mocked objects are not serialized. Instead a message is printed indicating that the object is a mocked
     * object. Mocked objects are detected by presence of 'mock' substring in their class name.</li>
     * </ol>
     *
     * @param object the object to serialize
     * @param objClassName object's class name.
     * @param logMessage {@link StringBuilder} instance to append serialized JSON.
     */
    private static void serialize(
            @Nullable Object object,
            @Nonnull String objClassName,
            @Nonnull StringBuilder logMessage) {
        boolean serializedSuccessfully = false;
        Exception exception = null;

        // this is to distinguish between methods returning null value and methods returning void.
        // Object arg is null in both cases but objClassName is not.
        if (objClassName.toLowerCase().equals("void")) {
            logMessage.append(objClassName);
            serializedSuccessfully = true;
        }

        // try serializing assuming a perfectly serializable object.
        if (!serializedSuccessfully) {
            try {
                logMessage.append(JsonUtil.toJson(object));
                serializedSuccessfully = true;
            } catch (Exception e) {
                exception = e;
            }
        }

        // detect if its a mock object.
        if (!serializedSuccessfully && objClassName.toLowerCase().contains("mock")) {
            logMessage.append("Mocked Object");
            serializedSuccessfully = true;
        }

        // try getting file size assuming object is a file type object
        if (!serializedSuccessfully) {
            long fileSize = -1;

            if (object instanceof ByteArrayResource) {
                fileSize = ((ByteArrayResource)object).contentLength();
            } else if (object instanceof MultipartFile) {
                fileSize = ((MultipartFile)object).getSize();
            }

            if (fileSize != -1) {
                logMessage.append("file of size:[").append(fileSize).append(" B]");
                serializedSuccessfully = true;
            }
        }

        if (!serializedSuccessfully) {
            LOG.warn("Unable to process object of type " + objClassName + " for logging", exception);
        }
    }

    protected void allPublicControllerMethods() {
    }

    @Pointcut("@annotation(com.harshil.logger.controller.annotation.Logging) "
            + "|| @target(com.harshil.logger.controller.annotation.Logging)")
    protected void methodOrClassLoggingEnabled() {
    }

    @Pointcut("!@annotation(com.harshil.logger.controller.annotation.NoLogging)")
    protected void methodLoggingNotDisabled() {
    }

    @Around("allPublicControllerMethods() && methodLoggingNotDisabled() && methodOrClassLoggingEnabled()")
    public Object log(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        String returnType = null;
        RequestMapping methodRequestMapping = null;
        RequestMapping classRequestMapping = null;

        try {
            MethodSignature methodSignature = (MethodSignature)proceedingJoinPoint.getSignature();
            methodRequestMapping = methodSignature.getMethod().getAnnotation(RequestMapping.class);
            classRequestMapping = proceedingJoinPoint.getTarget().getClass().getAnnotation(RequestMapping.class);

            // this is required to distinguish between a returned value of null and no return value, as in case of
            // void return type.
            returnType = methodSignature.getReturnType().getName();

            logPreExecutionData(proceedingJoinPoint, methodRequestMapping);
        } catch (Exception e) {
            LOG.error("Exception occurred while processing pre-target proceed logic", e);
        }

        StopWatch timer = new StopWatch();
        try {
            timer.start();
            result = proceedingJoinPoint.proceed();
        } finally {
            timer.stop();
            if (returnType != null) {
                logPostExecutionData(
                        proceedingJoinPoint,
                        timer,
                        result,
                        returnType,
                        methodRequestMapping,
                        classRequestMapping);
            }
        }

        return result;
    }

    /**
     * Logs any exception thrown by method. This aspect is executed <b>AFTER</b> the exception has been thrown, so one
     * cannot swallow it over here.
     */
    @AfterThrowing(
            pointcut = "allPublicControllerMethods() && methodLoggingNotDisabled() && methodOrClassLoggingEnabled()",
            throwing = "t")
    public void onException(JoinPoint joinPoint, Throwable t) {
        String methodName = joinPoint.getSignature().getName() + "()";
        LOG.info(methodName + " threw exception: [" + t + "]");
    }
}
