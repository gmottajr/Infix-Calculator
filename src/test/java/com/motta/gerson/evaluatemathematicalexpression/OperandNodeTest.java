package com.motta.gerson.evaluatemathematicalexpression;

import com.motta.gerson.evaluatemathematicalexpression.MathExpressionEvaluator.OperandNode;
import com.motta.gerson.evaluatemathematicalexpression.MathExpressionEvaluator.OperatorNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OperandNodeTest {
    private final MathExpressionEvaluator evaluator;

    private static final String SUBTRACTION_OPERATOR = "-";
    private static final String ADDITION_OPERATOR = "+";
    private static final String OPEN_PARENTHESIS = "(";

    public OperandNodeTest() {
        evaluator = new MathExpressionEvaluator();
    }
    @Test
    public void getValue_Success() {
        OperandNode node = evaluator.buildOperandNode(null, "123.45");
        assertEquals(123.45, node.getValue());
    }

    @Test
    public void getValue_Failure() {
        OperandNode node = evaluator.buildOperandNode(null, "notANumber");
        assertEquals(0, node.getValue());
    }

    @Test
    public void evaluate_Success() {
        OperandNode node = evaluator.buildOperandNode(null, "123.45");
        assertEquals(123.45, node.evaluate());
    }

    @Test
    public void evaluate_Failure() {
        OperandNode node = evaluator.buildOperandNode(null, "notANumber");
        assertEquals(0, node.evaluate());
    }

    @Test
    public void evaluateWithInversionSignal_Success() {
        OperatorNode parent = evaluator.buildOperatorNode(OPEN_PARENTHESIS, true, true, null);
        OperatorNode nodeOperator = evaluator.buildOperatorNode(SUBTRACTION_OPERATOR, true, false, parent);
        OperandNode node = evaluator.buildOperandNode(parent, "123.45");
        nodeOperator.setRight(node);
        assertEquals(-123.45, nodeOperator.evaluate());
    }

    @Test
    public void getIsParenthesized_Success() {
        OperatorNode parent = evaluator.buildOperatorNode(OPEN_PARENTHESIS, true, false, null);
        OperandNode node = evaluator.buildOperandNode(parent, "123.45");
        assertTrue(node.getIsParenthesized());
    }

    @Test
    public void getIsParenthesized_Failure() {
        OperandNode node = evaluator.buildOperandNode(null,"123.45");
        assertFalse(node.getIsParenthesized());
    }

    @Test
    public void isOperand_Success() {
        OperandNode node = evaluator.buildOperandNode(null, "123.45");
        assertTrue(node.isOperand());
    }

    @Test
    public void isOperand_Failure() {
        OperatorNode operatorNode = evaluator.buildOperatorNode(ADDITION_OPERATOR, false, false, null);
        assertFalse(operatorNode.isOperand());
    }
}
