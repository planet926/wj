package com.jvm;

public class MainTest {

    public static void main(String[] args) {
        ConcurrentModificationExceptionTest cmet = new ConcurrentModificationExceptionTest();
        cmet.test1();
    }

}
