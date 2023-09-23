/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.motta.gerson.evaluatemathematicalexpression;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 *
 * @author Jr
 */
public class EvaluateMathematicalExpressionShould {
    
    private static final String SUBTRACTION_OPERATOR = "-";
    private static final String ADDITION_OPERATOR = "+";
    private static final String MULTIPLICATION_OPERATOR = "*";
    private static final String DIVISION_OPERATOR = "/";
    
    public EvaluateMathematicalExpressionShould() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of isOperand method, of class EvaluateMathematicalExpression.
     */
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "5", "8", "9", "10", "40"})
    public void isOperandReturnsTrueForNumericValues(String value) {
        assertTrue(MathExpressionEvaluator.isOperand(value), "isOperand test for " + value);
    }

    /**
     * Test of isOperator method, of class EvaluateMathematicalExpression.
     */
    @ParameterizedTest
    @ValueSource(strings = {ADDITION_OPERATOR, SUBTRACTION_OPERATOR, MULTIPLICATION_OPERATOR, DIVISION_OPERATOR})
    public void isOperatorReturnsTrueWhenItsParameterIsOneOfTheOperatorsConst(String value) {
        System.out.println("isOperator");
        assertTrue(MathExpressionEvaluator.isOperator(value));
    }

    /**
     * Test of normalizeElementsTokenizing method, of class EvaluateMathematicalExpression.
     */
    @Test
    public void testNormalizeElementsTokenizing() {
        System.out.println("normalizeElementsTokenizing");
        String expression = "4 + 5 * (5 -3)/ -34";
        List<String> expResult = Arrays.asList("4", "+", "5", "*", "(", "5", "+", "-3", ")", "/", "-34");
        List<String> result = MathExpressionEvaluator.normalizeElementsTokenizing(expression);
        assertLinesMatch(expResult, result);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {ADDITION_OPERATOR, SUBTRACTION_OPERATOR, MULTIPLICATION_OPERATOR, DIVISION_OPERATOR})
    public void testOperatorPriorityContainsOperators(String value) {
        assertTrue(MathExpressionEvaluator.operatorPriority.containsKey(value));
    }

    @ParameterizedTest
    @CsvSource({
        ADDITION_OPERATOR + ", 0",
        SUBTRACTION_OPERATOR + ", 0",
        MULTIPLICATION_OPERATOR + ", 1",
        DIVISION_OPERATOR + ", 1"
    })
    public void testOperatorPriorityValues(String operator, int expectedPriority) {
        int actualPriority = MathExpressionEvaluator.operatorPriority.get(operator);
        assertEquals(expectedPriority, actualPriority, "Operator priority test for " + operator);
    }

    @Test
    public void testOperatorPrioritySize() {
        assertEquals(5, MathExpressionEvaluator.operatorPriority.size());
    }

    @BeforeAll
    public static void printOperatorPriority() {
        System.out.println("Operator Priority Map:");
        MathExpressionEvaluator.operatorPriority.forEach((key, value) -> System.out.println(key + ": " + value));
    }
    
    
    @ParameterizedTest
    @CsvSource({
            "-123, -123",
            "12*-1, -12",
            "1-1, 0",
            "4-1, 3",
            "1 -1, 0",
            "1 - 1, 0",
            "1- -1, 2",
            "13 + 112, 125",
            "325 + 1124, 1449",
            "5 + 11 - 4, 12",
            "5 * 11 - 20 + 5, 40",
            "1 + 2 - 3 * (4 / 6), 1",
            "(1 + 2) - 3 * (4 / 6), 1",
            "((1 + 2) - 3) * (4 / 6), 0",
            "-123, -123",
            "12*-1, -12",
            "12* 123/-(-5 + 2), 492",
            "(-5 + 2), -3",
            "-(-5 + 2), 3",
            "(3 + 2), 5",
            "(3 + 5) * 10, 80",
            "12* 123/(-5 + 2), -492",
            "((80 - (19))), 61.0",
            "(1 - 2) + -(-(-(-4))), 3.0",
            "(123.45*(678.90 / (-2.5+ 11.5)-(((80 -(19))) *33.25)) / 20), -12053.760875",
            "-(123.45*(678.90 / (-2.5+ 11.5)-(((80 -(19))) *33.25)) / 20), 12053.760875",
            "(123.45*(678.90 / (-2.5+ 11.5)-(((80 -(19))) *33.25))), -241075.2175",
            "(13 - 2)/ -(-11), 1",
    })
    public void evaluateMathematicalExpression(String expression, double expected) {
        double result;
        System.out.println("****** Expression: " + expression);
        MathExpressionEvaluator evaluator = new MathExpressionEvaluator();
        result = evaluator.evaluate(expression);
        System.out.println("result: " + result);
        //assertEquals(expected, result);
        assertEquals(expected, result, "Test failed for input: " + expression);
    }
    
    
    @ParameterizedTest
    @CsvSource({
            //"(-5 + 2), -3",
            //"-(-5 + 2), 3",
            "13 + 112, 125",
    })
    public void evaluateMathematicalExpression2(String expression, double expected) {
        double result;
        System.out.println("****** Expression: " + expression);
        MathExpressionEvaluator evaluator = new MathExpressionEvaluator();
        result = evaluator.evaluate(expression);
        System.out.println("result: " + result);
        //assertEquals(expected, result);
        assertEquals(expected, result, "Test failed for input: " + expression);
    }
}
