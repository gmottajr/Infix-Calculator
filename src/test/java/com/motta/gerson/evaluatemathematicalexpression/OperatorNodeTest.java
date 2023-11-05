package com.motta.gerson.evaluatemathematicalexpression;


import com.motta.gerson.evaluatemathematicalexpression.MathExpressionEvaluator.OperatorNode;
import com.motta.gerson.evaluatemathematicalexpression.MathExpressionEvaluator.OperandNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class OperatorNodeTest {
    // Constants used for testing
    private static final String SUBTRACTION_OPERATOR = "-";
    private static final String ADDITION_OPERATOR = "+";
    private static final String MULTIPLICATION_OPERATOR = "*";
    private static final String DIVISION_OPERATOR = "/";
    private static final String OPEN_PARENTHESIS = "(";

    private MathExpressionEvaluator evaluator;

    public OperatorNodeTest(){
        evaluator = new MathExpressionEvaluator();
    }
    @Test
    public void testIsParenthesized_Success() {
        OperatorNode operatorNode = evaluator.buildOperatorNode(OPEN_PARENTHESIS, true, false, null);
        assertTrue(operatorNode.getIsParenthesized());
    }

    @Test
    public void testIsParenthesized_Failure() {
        OperatorNode operatorNode = evaluator.buildOperatorNode(OPEN_PARENTHESIS, false, false, null);
        assertFalse(operatorNode.getIsParenthesized());
    }

    @Test
    public void testHasParentParenthesized_Success() {
        OperatorNode parent = evaluator.buildOperatorNode(OPEN_PARENTHESIS, true, false, null);
        OperatorNode child = evaluator.buildOperatorNode(ADDITION_OPERATOR, true, true, parent);
        assertTrue(child.hasParentParenthesized());
    }

    @Test
    public void testHasParentParenthesized_Failure() {
        OperatorNode parent = evaluator.buildOperatorNode(ADDITION_OPERATOR, true, false, null);
        MathExpressionEvaluator.OperandNode child = evaluator.buildOperandNode(parent, "30");
        assertFalse(parent.hasParentParenthesized());
    }

    @Test
    public void testIsAddition_Success() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(ADDITION_OPERATOR, false, false, null);
        assertTrue(operatorNode.isAddition());
    }

    @Test
    public void testIsAddition_Failure() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(SUBTRACTION_OPERATOR, false, false, null);
        assertFalse(operatorNode.isAddition());
    }

    @Test
    public void testIsMaxPrecedence_Success() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(MULTIPLICATION_OPERATOR, false, false, null);
        assertTrue(operatorNode.isMaxPrecedence());
    }

    @Test
    public void testIsMaxPrecedenceWithParentheses_Success() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(OPEN_PARENTHESIS, false, false, null);
        assertTrue(operatorNode.isMaxPrecedence());
    }

    @Test
    public void testIsMaxPrecedence_Failure() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(ADDITION_OPERATOR, false, false, null);
        assertFalse(operatorNode.isMaxPrecedence());
    }

    @Test
    public void testIsFull_Success() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(ADDITION_OPERATOR, false, false, null);
        operatorNode.setRight(evaluator.buildOperandNode(operatorNode, "6"));
        operatorNode.setLeft(evaluator.buildOperandNode(operatorNode, "56"));
        assertTrue(operatorNode.isFull());
    }

    @Test
    public void testIsFull_Failure() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(ADDITION_OPERATOR, false, false, null);
        operatorNode.setRight(evaluator.buildOperandNode(operatorNode, "6"));
        assertFalse(operatorNode.isFull());
    }

    @Test
    public void testEvaluate_Success() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(ADDITION_OPERATOR, false, false, null);
        operatorNode.setRight(evaluator.buildOperandNode(operatorNode, "6"));
        operatorNode.setLeft(evaluator.buildOperandNode(operatorNode, "56"));
        assertEquals(62.0, operatorNode.evaluate(), 0.001);
    }

    @Test
    public void testEvaluateInvertSignal_Success() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode parentNode = evaluator.buildOperatorNode(OPEN_PARENTHESIS, true, true, null);
        OperatorNode operatorNode = evaluator.buildOperatorNode(ADDITION_OPERATOR, true, false, parentNode);
        operatorNode.setRight(evaluator.buildOperandNode(parentNode, "6"));
        operatorNode.setLeft(evaluator.buildOperandNode(parentNode, "6"));
        assertEquals(-12.0, operatorNode.evaluate(), 0.001);
    }

    @Test
    public void testEvaluateParenthesized_Success() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode parentNode1 = evaluator.buildOperatorNode(OPEN_PARENTHESIS, true, true, null);
        OperatorNode operatorNode = evaluator.buildOperatorNode(ADDITION_OPERATOR, true, false, parentNode1);
        operatorNode.setRight(evaluator.buildOperandNode(parentNode1, "6"));
        operatorNode.setLeft(evaluator.buildOperandNode(parentNode1, "6"));

        OperatorNode multiplyOperatorNode = evaluator.buildOperatorNode(MULTIPLICATION_OPERATOR, false, false, null);

        multiplyOperatorNode.setRight(parentNode1);
        multiplyOperatorNode.setLeft(evaluator.buildOperandNode(null, "10"));
        assertEquals(-120.0, operatorNode.evaluate(), 0.001);
    }

    @Test
    public void testIsOperand_Success() {
        evaluator = new MathExpressionEvaluator();
        MathExpressionEvaluator.OperandNode operandNode = evaluator.buildOperandNode(null, "90");
        assertTrue(operandNode.isOperand());
    }

    @Test
    public void testIsOperand_Failure() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(ADDITION_OPERATOR, false, false, null);
        assertFalse(operatorNode.isOperand());
    }

    @Test
    public void testIsOperator_Success() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(ADDITION_OPERATOR, false, false, null);
        assertTrue(operatorNode.isOperator());
    }

    @Test
    public void testIsOperatorAsParentheses_Failure() {
        evaluator = new MathExpressionEvaluator();
        OperatorNode operatorNode = evaluator.buildOperatorNode(OPEN_PARENTHESIS, true, true, null);
        assertFalse(operatorNode.isOperator());
    }

    @Test
    public void testIsOperator_Failure() {
        evaluator = new MathExpressionEvaluator();
        MathExpressionEvaluator.OperandNode operandNode = evaluator.buildOperandNode(null, "90");
        assertFalse(operandNode.isOperator());
    }

    @Test
    void isLeaf_Success() {
        OperatorNode leafNode = evaluator.buildOperatorNode(MULTIPLICATION_OPERATOR, false, false, null);
        assertTrue(leafNode.isLeaf(), "Leaf node should have no children");
    }

    @Test
    void isLeafOperand_Success() {
        OperandNode leafNode = evaluator.buildOperandNode(null, "4");
        assertTrue(leafNode.isLeaf(), "Leaf node should have no children");
    }

    @Test
    void isLeaf_Failure() {
        OperatorNode parentNode = evaluator.buildOperatorNode(DIVISION_OPERATOR, false, false, null);
        OperandNode leftChild = evaluator.buildOperandNode(parentNode, "16");
        OperandNode rightChild = evaluator.buildOperandNode(parentNode, "4");
        parentNode.setLeft(leftChild);  // Assuming these methods exist
        parentNode.setRight(rightChild);

        assertFalse(parentNode.isLeaf(), "Node with children should not be a leaf");
    }
}
