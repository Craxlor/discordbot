package com.github.craxlor.discordbot.util;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import com.github.craxlor.discordbot.command.Command;

/**
 * {@link https://www.baeldung.com/java-find-all-classes-in-package}
 */
@SuppressWarnings("rawtypes")
public class AccessingAllClassesInPackage {

    public static Set<Class> findAllClassesUsingReflectionsLibrary(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.SubTypes);
        Set<Class> set = reflections.getSubTypesOf(Command.class).stream().collect(Collectors.toSet());
        set.removeIf(cls -> Modifier.isAbstract(cls.getModifiers()));
        return set;
    }
}
