/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.motta.gerson.evaluatemathematicalexpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;

/**
 *
 * @author Gerson Jr
 */
public class EvaluateMathematicalExpression {

    private static final String SUBTRACTION_OPERATOR = "-";
    private static final String ADDITION_OPERATOR = "+";
    private static final String MULTIPLICATION_OPERATOR = "*";
    private static final String DIVISION_OPERATOR = "/";
    private static final String OPEN_PARENTHESIS = "(";
    private static final String CLOSE_PARENTHESIS = ")";
    private static final String OPERATORS_CONST = "-+*/^";

    public static final Map<String, Integer> operatorPriority = new HashMap<String, Integer>() {
        {
            put(ADDITION_OPERATOR, 0);
            put(SUBTRACTION_OPERATOR, 0);
            put(MULTIPLICATION_OPERATOR, 1);
            put(DIVISION_OPERATOR, 1);
        }
    };

    public static Boolean isOperand(String value) {
        try {
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Boolean isOperator(String value) {
        Boolean rst = OPERATORS_CONST.contains(value);
        return rst;
    }

    public static List<String> normalizeElementsTokenizing(String expression) {
        String rst = expression.replace(" ", "")
                .replace("--", "+")
                .replace("- -", "+")
                .replace("+-", "-")
                .replace("+ -", "-");

        rst = rst.replace(OPEN_PARENTHESIS, " ( ")
                .replace(CLOSE_PARENTHESIS, " ) ")
                .replace(SUBTRACTION_OPERATOR, " -")
                .replace("- ( ", " -( ")
                .replace(ADDITION_OPERATOR, " + ")
                .replace(MULTIPLICATION_OPERATOR, " * ")
                .replace(DIVISION_OPERATOR, " / ");

        String[] elements = rst.split(" ");
        List<String> result;
        result = new ArrayList<>();
        for (String element : elements) {
            if (!element.isEmpty()) {
                result.add(element);
            }
        }

        return result;
    }

    private boolean popConnectPush(Stack<ExpressionOperatorNode> opStack, Stack<ExpressionBase> nodeStack, boolean isClosing) {
        
        if (!isClosing && !opStack.isEmpty() && opStack.peek().getIsParenthesized() && nodeStack.size() <= 1) 
        {
            return false;
        }

        ExpressionOperatorNode temp = opStack.pop();
        ExpressionBase adjustingPrecedence = null;

        if (temp != null) {
            if (!nodeStack.isEmpty()) {
                ExpressionBase operandTempR = nodeStack.pop();

                if (temp.isMaxPrecedence() && (operandTempR instanceof ExpressionOperatorNode)
                        && !((ExpressionOperatorNode) operandTempR).getIsParenthesized()
                        && operatorPriority.get(temp.getInfo()) > operatorPriority.get(operandTempR.getInfo())) 
                {
                    
                    temp.setRight(((ExpressionOperatorNode) operandTempR).getLeft());
                    adjustingPrecedence = ((ExpressionOperatorNode) operandTempR).getRight();
                
                } 
                else 
                {
                    temp.setRight(operandTempR);
                }
            }

            if (!nodeStack.isEmpty()) {
                ExpressionBase operandTempL = nodeStack.pop();

                if (temp.isMaxPrecedence() && (operandTempL instanceof ExpressionOperatorNode)
                        && !((ExpressionOperatorNode) operandTempL).getIsParenthesized()
                        && operatorPriority.get(temp.getInfo()) > operatorPriority.get(operandTempL.getInfo())) 
                {
                    
                    temp.setLeft(((ExpressionOperatorNode) operandTempL).getRight());
                    adjustingPrecedence = ((ExpressionOperatorNode) operandTempL).getLeft();
                
                } 
                else 
                {
                    temp.setLeft(operandTempL);
                }
            }

            if (temp.isFull() && (temp.getIsParenthesized() || temp.isMaxPrecedence())) {
                double result = temp.evaluate();
                ExpressionOperandNode gotNode = new ExpressionOperandNode(Double.toString(result));
                nodeStack.push(gotNode);
            } else {
                nodeStack.push(temp);
            }
        }

        if (adjustingPrecedence != null) {
            nodeStack.push(adjustingPrecedence);
            if (adjustingPrecedence.isNegative()) {
                opStack.push(new ExpressionOperatorNode(ADDITION_OPERATOR, false, false));
            }
        }

        return true;
    }

    private static ExpressionOperatorNode checkIsParenthesized(Stack<ExpressionOperatorNode> operatorsStack) {
        return !operatorsStack.isEmpty() && operatorsStack.peek().getIsParenthesized() ? operatorsStack.peek() : null;
    }

    private static boolean isReadyToComposeExpression(Stack<ExpressionBase> nodeStack) {
        long count = nodeStack.stream().filter(item -> item.isNumber()).count();
        return count % 2 == 0;
    }

    private static boolean popOperatorsPushToNode(Stack<ExpressionOperatorNode> operatorsStack, Stack<ExpressionBase> nodeStack, String token) {
        return !operatorsStack.isEmpty() && !nodeStack.isEmpty()
                && !operatorsStack.peek().getInfo().equals(OPEN_PARENTHESIS)
                && operatorPriority.get(operatorsStack.peek().getInfo()) >= operatorPriority.get(token);
    }

    private ExpressionBase infixExpressionToTree(String exp) {
        List<String> tokens = normalizeElementsTokenizing(exp);

        Stack<ExpressionOperatorNode> operatorsStack = new Stack<>();
        Stack<ExpressionBase> nodeStack = new Stack<>();

        for (String token : tokens) 
        {
            if (isOperator(token)) 
            {
                boolean nodeStackPeekIsNegative = nodeStack.peek().isNegative();
                String nodeStackPeekInfo = nodeStack.peek().getInfo();
                boolean operatorsStackPeekIsAddition = !operatorsStack.isEmpty() && operatorsStack.peek().isAddition();
                if (token.equals(ADDITION_OPERATOR) && 
                        !operatorsStack.isEmpty() && 
                        operatorsStack.peek().isAddition() && 
                        !nodeStack.isEmpty() && 
                        nodeStackPeekIsNegative) {
                    continue;
                }

                while (popOperatorsPushToNode(operatorsStack, nodeStack, token)) 
                {
                    if (!popConnectPush(operatorsStack, nodeStack, false)) 
                    {
                        break;
                    }
                }
                
                ExpressionOperatorNode gotNode = new ExpressionOperatorNode(token, 
                        false, 
                        false,
                        (operatorsStack.isEmpty() ? null : operatorsStack.peek())
                );
                
                operatorsStack.push(gotNode);
                
            } 
            else if (token.equals(OPEN_PARENTHESIS)) 
            {
                
                ExpressionOperatorNode gotNode = new ExpressionOperatorNode(token, 
                        true,
                        false,
                checkIsParenthesized(operatorsStack));
                
                operatorsStack.push(gotNode);
                
            } 
            else if (token.equals("-(")) 
            {
                
                operatorsStack.push(new ExpressionOperatorNode(OPEN_PARENTHESIS,
                        true,
                        true,
                        checkIsParenthesized(operatorsStack)));
                
            } 
            else if (token.equals(CLOSE_PARENTHESIS)) 
            {
                while (!operatorsStack.isEmpty() && !operatorsStack.peek().getInfo().equals(OPEN_PARENTHESIS)) {
                    if (!popConnectPush(operatorsStack, nodeStack, true)) {
                        break;
                    }
                }

                // Discard the '('
                if (!operatorsStack.isEmpty() && operatorsStack.peek().getInfo().equals(OPEN_PARENTHESIS)) 
                {
                    operatorsStack.pop();
                }
            } 
            else if (isOperand(token)) 
            {
                if (token.contains(SUBTRACTION_OPERATOR)) 
                {
                    while (popOperatorsPushToNode(operatorsStack, nodeStack, SUBTRACTION_OPERATOR)) 
                    {
                        if (!popConnectPush(operatorsStack, nodeStack, false)) 
                        {
                            break;
                        }
                    }
                }

                boolean goodToGo = isReadyToComposeExpression(nodeStack);
                if (!nodeStack.isEmpty() && 
                        !nodeStack.peek().isNumber()
                        && !((ExpressionOperatorNode) nodeStack.peek()).isFull()) 
                {
                    ((ExpressionOperatorNode) nodeStack.peek()).balanceNode(new ExpressionOperandNode(token));
                } 
                else 
                {
                    nodeStack.push(new ExpressionOperandNode(token));
                }

                if (token.contains(SUBTRACTION_OPERATOR)) 
                {
                    if (operatorsStack.stream()
                            .filter(item -> !item.getInfo().equals(OPEN_PARENTHESIS))
                            .count() == 0
                            || goodToGo && !nodeStack.isEmpty() && nodeStack.peek().isNumber()) 
                    {
                        operatorsStack.push(new ExpressionOperatorNode(ADDITION_OPERATOR, 
                                false,
                                false, 
                                checkIsParenthesized(operatorsStack)));
                        
                        while (popOperatorsPushToNode(operatorsStack, nodeStack, ADDITION_OPERATOR)) 
                        {
                            if (!popConnectPush(operatorsStack, nodeStack, false)) {
                                break;
                            }
                        }
                    }
                }
            }
        } // end for

        while (!operatorsStack.isEmpty()) 
        {
            if (!popConnectPush(operatorsStack, nodeStack, true)) {
                break;
            }
        }

        while (nodeStack.size() > 1) 
        {
            if (operatorsStack.isEmpty()) 
            {
                operatorsStack.push(new ExpressionOperatorNode(ADDITION_OPERATOR, 
                        false,
                        false));
            }

            if (!popConnectPush(operatorsStack, nodeStack, true)) {
                break;
            }
        }

        return nodeStack.peek();
    }

    public double evaluate(String expression) {
        ExpressionBase root = this.infixExpressionToTree(expression);
        return root.evaluate();
    }

    private abstract class ExpressionBase {

        protected String info;

        protected ExpressionBase(String info) {
            this.info = info;
        }

        public Boolean isNumber() {
            try {
                Double.valueOf(getInfo());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public Boolean isNegative() {
            return isNumber() ? 0 > Double.parseDouble(info) : false;
        }

        public abstract double evaluate();

        public String getInfo() {
            return info;
        }
    }

    private abstract class MathExpressionNode extends ExpressionBase {

        private static final Set<String> operatorKeys = new HashSet<>(Set.of(
                ADDITION_OPERATOR,
                SUBTRACTION_OPERATOR,
                MULTIPLICATION_OPERATOR,
                DIVISION_OPERATOR
        ));
        
        private static final Map<String, BiFunction<Double, Double, Double>> operations = new HashMap<>();

        static {
            operations.put(ADDITION_OPERATOR, (x, y) -> x + y);
            operations.put(SUBTRACTION_OPERATOR, (x, y) -> x - y);
            operations.put(MULTIPLICATION_OPERATOR, (x, y) -> x * y);
            operations.put(DIVISION_OPERATOR, (x, y) -> x / y);
            //operations.put("MultiplicationInvertOperator", (x, y) -> x * -y);
        }

        private ExpressionBase left;
        private ExpressionBase right;

        public MathExpressionNode(String info) {
            super(info);
        }

        public Boolean isLeaf() {
            return left == null && right == null;
        }

        public void balanceNode(ExpressionBase value) {
            if (left == null) {
                setLeft(value);
            }

            if (right == null) {
                setRight(value);
            }
        }

        @Override
        public double evaluate() {
            double result = 0.0;

            if (!isLeaf()) {
                if (operatorKeys.contains(info)) {
                    if (left != null && right != null) 
                    {
                        BiFunction<Double, Double, Double> operatorFunction = operations.get(info);
                        if (operatorFunction != null) {
                            result = operatorFunction.apply(left.evaluate(), right.evaluate());
                        } 
                        else 
                        {
                            System.out.println("Error!");
                        }
                        //result = operations.get(info).apply(left.evaluate(), right.evaluate());
                    }

                    if (left != null && right == null) {
                        result = left.evaluate();
                    }

                    if (left == null && right != null) {
                        result = right.evaluate();
                    }
                }

                if ((left != null && right != null) && 
                        (right.getInfo().equals(SUBTRACTION_OPERATOR) || 
                        left.getInfo().equals(SUBTRACTION_OPERATOR))) 
                {
                    result = operations.get(ADDITION_OPERATOR).apply(left.evaluate(), right.evaluate());
                }

                return result;
            } 
            else 
            {
                if (isNumber()) 
                {
                    return Double.parseDouble(getInfo());
                } 
                else 
                {
                    return (getRight() != null) ? getRight().evaluate() : (getLeft() != null) ? getLeft().evaluate() : 0;
                }
            }
        }

        /**
         * @return the Left
         */
        public ExpressionBase getLeft() {
            return left;
        }

        /**
         * @param Left the Left to set
         */
        public void setLeft(ExpressionBase Left) {
            this.left = Left;
        }

        /**
         * @return the Right
         */
        public ExpressionBase getRight() {
            return right;
        }

        /**
         * @param Right the Right to set
         */
        public void setRight(ExpressionBase Right) {
            this.right = Right;
        }
    }

    private class ExpressionOperatorNode extends MathExpressionNode {

        private boolean isParenthesized;
        private boolean invertSignal;
        private ExpressionOperatorNode parent;

        public ExpressionOperatorNode(String info, boolean pIsParenthesized, boolean pInvertSignal) {
            super(info);
            isParenthesized = pIsParenthesized;
            invertSignal = pInvertSignal;
        }

        private ExpressionOperatorNode(String info, boolean pIsParenthesized, boolean pInvertSignal, ExpressionOperatorNode parent) {
            super(info);
            this.isParenthesized = pIsParenthesized;
            this.parent = parent;
            this.invertSignal = pInvertSignal;
        }

        public boolean getIsParenthesized() {
            return isParenthesized || (parent != null && parent.getIsParenthesized());
        }

        public boolean isAddition() {
            return (info == ADDITION_OPERATOR);
        }

        public boolean isMaxPrecedence() {
            int infoPriority = operatorPriority.get(this.getInfo());
            int maxPriority = operatorPriority.values().stream().mapToInt(Integer::intValue).max().orElse(Integer.MIN_VALUE);
            return infoPriority == maxPriority;
        }

        public boolean isFull() {
            return getRight() != null && getLeft() != null;
        }

        @Override
        public double evaluate() {
            double rst = super.evaluate();

            return invertSignal || (parent != null && parent.isParenthesized && parent.invertSignal) ? (rst * -1) : rst;
        }
    }

    private class ExpressionOperandNode extends ExpressionBase {

        public ExpressionOperandNode(String info) {
            super(info);
        }

        public double getValue() {
            try {
                Double rst = Double.valueOf(getInfo());
                return rst;
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        @Override
        public double evaluate() {
            return getValue();
        }

    }

}
