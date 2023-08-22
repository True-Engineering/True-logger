package ru.trueengineering.lib.logger.autoconfigure.aspect;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author m.yastrebov
 */
public class PackageBasedMethodMatcherPointcut extends StaticMethodMatcherPointcut {

    private final List<Pattern> packageNamePatterns;

    public PackageBasedMethodMatcherPointcut(List<Pattern> packageNamePatterns) {
        this.packageNamePatterns = packageNamePatterns;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return !Modifier.isStatic(method.getModifiers()) && !Modifier.isPrivate(method.getModifiers());
    }

    @Override
    public ClassFilter getClassFilter() {
        return clazz -> clazz.getPackage() != null && isControllerPackage(clazz.getPackage().getName());
    }

    private boolean isControllerPackage(String packageName) {
        for (Pattern packageNamePattern : packageNamePatterns) {
            if (packageNamePattern.matcher(packageName).find()) {
                return true;
            }
        }
        return false;
    }
}
