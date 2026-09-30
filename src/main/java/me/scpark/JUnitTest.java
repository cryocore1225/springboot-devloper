package me.scpark;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.*;

public class JUnitTest {

    @DisplayName("1+2:3")
    @Test
    public void junitTest() {
        int a = 1;
        int b = 2;
        int sum = 3;


        // expected value, actual value
        Assertions.assertEquals(sum, a + b);
    }
    @Test
    public void junitFailTest(){
        int a=1;
        int b=3;
        int sum=4;
        System.out.println("1+2=3");
        Assertions.assertEquals(sum, a + b);

    }
    @BeforeEach
    public void prepare(){
        System.out.println("test ready");
    }
    @AfterEach
    public void clean(){
        System.out.println("clean after test");
    }
    @BeforeAll
    public static void prepareAll(){
        System.out.println("delete test");
    }
    @AfterAll
    public static void cleanAll(){
        System.out.println("clean after all test");
    }
}