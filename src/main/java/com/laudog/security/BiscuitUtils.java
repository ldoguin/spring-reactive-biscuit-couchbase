package com.laudog.security;

public class BiscuitUtils {

    public static String createFact(String fact, String term){
        return String.format("%s(\"%s\")", fact, term);
    }

    public static String createFactCheck(String fact, String term){
        return String.format("check if %s(\"%s\")", fact, term);
    }

    public static String createFactCheck(String fact){
        return String.format("check if %s", fact);
    }
}
