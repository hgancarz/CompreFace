package com.exadel.hello;

public class HelloWorld {
    public static String sayHello() {
        return "hello world";
    }
}
EOF && git add -A && git diff --cached -- java/common/src/main/java/com/exadel/hello/HelloWorld.java
