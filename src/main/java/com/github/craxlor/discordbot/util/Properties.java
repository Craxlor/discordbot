package com.github.craxlor.discordbot.util;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Nullable;

public class Properties {

    public static long DEV_ID = 324151726330609664l;

    @Nullable
    public static String get(String key) {
        try {
            java.util.Properties properties = new java.util.Properties();
            FileInputStream fis = new FileInputStream("resources/gradle.properties");
            properties.load(fis);
            fis.close();
            return properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }
}
