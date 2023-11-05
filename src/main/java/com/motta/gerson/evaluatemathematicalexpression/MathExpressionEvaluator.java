/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.motta.gerson.evaluatemathematicalexpression;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 *
 * @author Gerson Jr
 */
public class MathExpressionEvaluator {

    private static final String SUBTRACTION_OPERATOR = "-";
    private static final String ADDITION_OPERATOR = "+";
    private static final String MULTIPLICATION_OPERATOR = "*";
    private static final String DIVISION_OPERATOR = "/";
    private static final String OPEN_PARENTHESIS = "(";
    private static  final String INVERT_SIGNAL_PARENTHESIS = "-(";
    private static final String CLOSE_PARENTHESIS = ")";
    private static final String OPERATORS_CONST = "-+*/^";

    public static final Map<String, Integer> operatorPriority = new HashMap<>() {
        {
            put(ADDITION_OPERATOR, 0);
            put(SUBTRACTION_OPERATOR, 0);
            put(MULTIPLICATION_OPERATOR, 1);
            put(DIVISION_OPERATOR, 1);
            put(OPEN_PARENTHESIS, 2);
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

    public static Boolean isOperator(String value)
    {
        return OPERATORS_CONST.contains(value);
    }

    public static List<String> normalizeElementsTokenizing(String expression) {
        String rst = expression.replace(" ", "").replace("--", "+").replace("- -", "+")
                .replace("-", "+-")
                .replace("/ +", "/")
                .replace("/+", "/")
                .replace("(+", "(")
                .replace("( +", "(")
                .replace("*+", "*")
                .replace("++", "+")
                .replace("+ +", "+")
                .replace(" ", "");

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

    public  OperatorNode buildOperatorNode(String pInfo, boolean pIsParenthesized, boolean pInvertSignal, OperatorNode pParent)
    {
        if (pParent == null)
        {
            return new OperatorNode(pInfo, pIsParenthesized, pInvertSignal);
        }
        else
        {
            return new OperatorNode(pInfo, pIsParenthesized, pInvertSignal, pParent);
        }
    }

    public OperandNode buildOperandNode(OperatorNode parent, String info)
    {
        if (parent == null)
        {
            return new OperandNode(info);
        }
        else
        {
            return  new OperandNode(parent, info);
        }
    }

    private boolean popConnectPush(Stack<MathNode> opStack, Stack<ExpressionBase> nodeStack, boolean isClosing) {
        
        if (!isClosing && !opStack.isEmpty() && opStack.peek().getIsParenthesized() && nodeStack.size() <= 1) 
        {
            return false;
        }

        MathNode temp = opStack.pop();
        ExpressionBase adjustingPrecedence = null;

        if (temp != null)
        {
            if (!nodeStack.isEmpty())
            {
                ExpressionBase operandTempR = nodeStack.pop();

                if (temp.isMaxPrecedence() && (operandTempR instanceof OperatorNode)
                        && !(operandTempR.getIsParenthesized()
                        && operatorPriority.get(temp.getInfo()) > operatorPriority.get(operandTempR.getInfo())))
                {
                    
                    temp.setRight(((OperatorNode) operandTempR).getLeft());
                    adjustingPrecedence = ((OperatorNode) operandTempR).getRight();
                
                } 
                else 
                {
                    temp.setRight(operandTempR);
                }
            }

            if (!nodeStack.isEmpty())
            {
                ExpressionBase operandTempL = nodeStack.pop();

                if (temp.isMaxPrecedence() && (operandTempL instanceof OperatorNode)
                        && !((OperatorNode) operandTempL).getIsParenthesized()
                        && operatorPriority.get(temp.getInfo()) > operatorPriority.get(operandTempL.getInfo())) 
                {
                    
                    temp.setLeft(((OperatorNode) operandTempL).getRight());
                    adjustingPrecedence = ((OperatorNode) operandTempL).getLeft();
                
                } 
                else 
                {
                    temp.setLeft(operandTempL);
                }
            }

            if (temp.isFull() && (temp.getIsParenthesized() || temp.isMaxPrecedence() ||
                    temp.getRight().isNumber() && temp.getRight().hasParent() &&
                            temp.getRight().getParent().getIsParenthesized())
            )
            {
                if(temp.getLeft() != null && temp.getRight() != null && temp.getLeft().getParent() != temp.getRight().getParent())
                {
                    double rstR = temp.getRight().isOperand() ?((OperandNode)temp.getRight()).evaluate(true) : temp.getRight().evaluate();
                    adjustingPrecedence = new OperandNode(checkIsParenthesized(opStack), Double.toString(rstR));
                    nodeStack.push(temp.getLeft());
                }
                else
                {
                    double result = temp.evaluate();
                    OperandNode newNode = new OperandNode(checkIsParenthesized(opStack), Double.toString(result));
                    boolean isOperator = temp.isOperator();
                    boolean hasParentParenthesized = (temp instanceof  OperatorNode) && ((OperatorNode)temp).hasParentParenthesized();
                    boolean isInvertSignal = temp.hasParent() && temp.getParent().isInvertSignal();
                    if (isClosing && isOperator && hasParentParenthesized && isInvertSignal)
                    {
                        result = newNode.evaluate(true);
                        if (opStack.peek() == newNode.getParent())
                            opStack.pop();

                        newNode = new OperandNode(checkIsParenthesized(opStack), Double.toString(result));
                    }
                    nodeStack.push(newNode);
                }
                //double result = temp.evaluate();
                //OperandNode gotNode = new OperandNode(Double.toString(result));
                //nodeStack.push(gotNode);
            }
            else
            {
                nodeStack.push(temp);
            }
        }

        if (adjustingPrecedence != null)
        {
            nodeStack.push(adjustingPrecedence);

            Stack<MathNode> filteredStack = opStack.stream()
                    .filter(n -> !n.info.equals((OPEN_PARENTHESIS)))
                    .collect(Collectors.toCollection(Stack::new));

            if (adjustingPrecedence.isNegative() && !opStack.isEmpty() && !filteredStack.isEmpty() && !filteredStack.peek().isAddition())
            {
                makeItAddition(opStack);
                //opStack.push(new OperatorNode(ADDITION_OPERATOR, false, false));
            }
        }

        return true;
    }

    private static MathNode checkIsParenthesized(Stack<MathNode> operatorsStack) {
        boolean notEmpty = !operatorsStack.isEmpty();
        boolean operatorsStackPeekIsParenthesized = notEmpty && operatorsStack.peek().getIsParenthesized();
        boolean isOperator = notEmpty && operatorsStack.peek().isOperator();
        boolean hasParent = notEmpty && operatorsStack.peek().hasParent();
        if (notEmpty && operatorsStackPeekIsParenthesized)
        {
            if(isOperator && hasParent)
            {
                return (MathNode) operatorsStack.peek().getParent();
            }
            else
            {
                return operatorsStack.peek();
            }
        }
        else
            return null;
    }

    private static boolean popOperatorsPushToNode(Stack<MathNode> operatorsStack, Stack<ExpressionBase> nodeStack, String token) {
        return !operatorsStack.isEmpty() && !nodeStack.isEmpty()
                && !operatorsStack.peek().getInfo().equals(OPEN_PARENTHESIS)
                && operatorPriority.get(operatorsStack.peek().getInfo()) >= operatorPriority.get(token);
    }

    private ExpressionBase infixExpressionToTree(String exp) {
        List<String> tokens = normalizeElementsTokenizing(exp);

        Stack<MathNode> operatorsStack = new Stack<>();
        Stack<ExpressionBase> nodeStack = new Stack<>();
        FinalAdjust(tokens);

        for (String token : tokens) 
        {
            if (isOperator(token)) 
            {
                boolean nodeStackPeekIsNegative = !nodeStack.isEmpty() && nodeStack.peek().isNegative();

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
                
                OperatorNode gotNode = new OperatorNode(token,
                        false, 
                        false,
                        (operatorsStack.isEmpty() ? null : operatorsStack.peek())
                );
                
                operatorsStack.push(gotNode);
                
            } 
            else if (token.equals(OPEN_PARENTHESIS)) 
            {
                
                ParenthesesNode gotNode = new ParenthesesNode(token,
                        false,
                checkIsParenthesized(operatorsStack));
                
                operatorsStack.push(gotNode);
                
            } 
            else if (token.equals(INVERT_SIGNAL_PARENTHESIS))
            {
                
                operatorsStack.push(new ParenthesesNode(OPEN_PARENTHESIS,
                        true,
                        checkIsParenthesized(operatorsStack)));
                
            } 
            else if (token.equals(CLOSE_PARENTHESIS)) 
            {
                boolean openParenthesisPopped = false;
                if(!operatorsStack.isEmpty() && operatorsStack.peek().getInfo().equals((OPEN_PARENTHESIS)))
                {
                    popConnectPush(operatorsStack, nodeStack, true);
                    openParenthesisPopped = true;
                }
                else
                {
                    while (!operatorsStack.isEmpty() && !operatorsStack.peek().getInfo().equals(OPEN_PARENTHESIS)) {
                        if (!popConnectPush(operatorsStack, nodeStack, true)) {
                            break;
                        }
                    }
                }

                // Discard the '('
                if (!openParenthesisPopped && (!operatorsStack.isEmpty() && operatorsStack.peek().getInfo().equals(OPEN_PARENTHESIS)))
                {
                    MathNode removingParenthese = operatorsStack.pop();
                    List<ExpressionBase> filteredItems = nodeStack.stream()
                            .filter(item -> item.getParent() == removingParenthese)
                            .toList();

                    MathNode newParent = checkIsParenthesized(operatorsStack);

                    for (ExpressionBase item : filteredItems) {
                        item.setParent(newParent);
                    }
                }
            } 
            else if (isOperand(token)) 
            {
                if (!nodeStack.isEmpty() && 
                        !nodeStack.peek().isNumber()
                        && !((MathNode) nodeStack.peek()).isFull())
                {
                    ((MathNode) nodeStack.peek()).balanceNode(new OperandNode(token));
                } 
                else 
                {
                    nodeStack.push(new OperandNode(checkIsParenthesized(operatorsStack), token));
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
                makeItAddition(operatorsStack);
            }

            if (!popConnectPush(operatorsStack, nodeStack, true)) {
                break;
            }
        }

        return nodeStack.peek();
    }

    private void makeItAddition(Stack<MathNode> operatorsStack)
    {
        operatorsStack.push(new OperatorNode(checkIsParenthesized(operatorsStack), ADDITION_OPERATOR));
    }
    private void FinalAdjust(List<String> tokens)
    {
        if(tokens.size() > 1 && tokens.get(0).equals(ADDITION_OPERATOR) && tokens.get(1).equals((INVERT_SIGNAL_PARENTHESIS)))
        {
            tokens.remove(0);
        }
    }
    public double evaluate(String expression) {
        ExpressionBase root = this.infixExpressionToTree(expression);
        return root.evaluate();
    }

    public abstract class ExpressionBase {

        protected ExpressionBase parent;
        protected boolean invertSignal;
        protected final String info;

        protected ExpressionBase(String info) {
            this.info = info;
        }

        protected ExpressionBase(ExpressionBase parent, String info)
        {
            this.info = info;
            this.parent = parent;
            this.invertSignal = parent != null && parent.isInvertSignal();
        }

        public boolean isNumber() {
            try {
                Double.valueOf(getInfo());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public Boolean isNegative() {
            return isNumber() && 0 > Double.parseDouble(info);
        }

        public abstract double evaluate();
        public boolean hasParent()
        {
            return this.parent != null;
        }

        public abstract boolean getIsParenthesized();

        public boolean isOperator()
        {
            return OPERATORS_CONST.contains(this.info);
        }
        public String getInfo() {
            return info;
        }

        public ExpressionBase getParent() {
            return this.parent;
        }

        public void setParent(ExpressionBase newParent) {
            this.parent = newParent;
        }

        public abstract boolean isOperand();
        public abstract boolean isLeaf();

        public boolean isInvertSignal() {
            return invertSignal;
        }
    }
    public  abstract class MathNode extends ExpressionBase {

        protected static final Set<String> operatorKeys = new HashSet<>(Set.of(
                ADDITION_OPERATOR,
                SUBTRACTION_OPERATOR,
                MULTIPLICATION_OPERATOR,
                DIVISION_OPERATOR
        ));
        
        protected static final Map<String, BiFunction<Double, Double, Double>> operations = new HashMap<>();

        static {
            operations.put(ADDITION_OPERATOR, (x, y) -> x + y);
            operations.put(SUBTRACTION_OPERATOR, (x, y) -> x - y);
            operations.put(MULTIPLICATION_OPERATOR, (x, y) -> x * y);
            operations.put(DIVISION_OPERATOR, (x, y) -> x / y);
            //operations.put("MultiplicationInvertOperator", (x, y) -> x * -y);
        }

        protected ExpressionBase left;
        protected ExpressionBase right;

        public MathNode(String info) {
            super(info);
        }

        public MathNode(MathNode parent, String info) {
            super(parent, info);
        }

        @Override
        public boolean isLeaf() {
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

        public boolean isAddition() {
            return (info.equals(ADDITION_OPERATOR));
        }

        public boolean isMaxPrecedence() {
            int infoPriority = operatorPriority.get(this.getInfo());
            final int  maxPriority;
            if (!this.isOperator())
            {
               maxPriority = operatorPriority.values().stream().mapToInt(Integer::intValue).max().orElse(Integer.MIN_VALUE);
            }
            else
            {

               maxPriority = operatorPriority.entrySet()
                       .stream()
                       .filter(entry -> !entry.getKey().equals(OPEN_PARENTHESIS))
                       .mapToInt(Map.Entry::getValue)
                       .max()
                       .orElse(Integer.MIN_VALUE);
            }
            return infoPriority == maxPriority;
        }

        public boolean isFull() {
            return getRight() != null && getLeft() != null;
        }

        @Override
        public double evaluate() {
            double result = 0.0;

            if (!isLeaf()) {
                if (operatorKeys.contains(info)) {
                    if (left != null && right != null) 
                    {
                        result = operations.get(info).apply(left.evaluate(), right.evaluate());
                    }

                    if (left != null && right == null) {
                        result = left.evaluate();
                    }

                    if (left == null && right != null) {
                        result = right.evaluate();
                    }
                }
                else if (this.info.equals(OPEN_PARENTHESIS))
                {
                    if (left != null && right != null)
                    {
                        boolean areThemSiblings = (left.hasParent() && left.parent.equals(right.parent));
                        if (areThemSiblings)
                        {
                            double rstL = left.evaluate();
                            double rstR = right.evaluate();
                            result = rstL + rstR;
                        }
                        else
                        {
                            double rstL = left.invertSignal ? (left.evaluate() * -1) : left.evaluate();
                            double rstR = right.invertSignal ? (right.evaluate() * -1) : right.evaluate();
                            result = rstL + rstR;
                            if (right.invertSignal && right.parent != null && right.parent.invertSignal)
                                right.parent.invertSignal = false;
                        }
                    }

                    if (left != null && right == null)
                        result = left.evaluate();

                    if (left == null && right != null)
                        result = right.evaluate();
                }

                if ((left != null && right != null) && 
                        (right.info.equals(SUBTRACTION_OPERATOR) ||
                        left.info.equals(SUBTRACTION_OPERATOR)))
                {
                    result = operations.get(ADDITION_OPERATOR).apply(left.evaluate(), right.evaluate());
                }

                return result;
            } 
            else 
            {
                if (isNumber()) 
                {
                    return Double.parseDouble(info);
                } 
                else 
                {
                    return (right != null) ? right.evaluate() : (left != null) ? left.evaluate() : 0;
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

        @Override
        public boolean isOperand(){
            return isNumber();
        }
    }

    public class ParenthesesNode extends MathNode
    {

        protected List<OperatorNode> operatorChildren = new ArrayList<>();
        private ParenthesesNode(String info, boolean pInvertSignal, MathNode parent) {
            super(info);
            this.parent = parent;
            this.invertSignal = pInvertSignal;
        }

        @Override
        public boolean getIsParenthesized() {
            return true;
        }

        @Override
        public double evaluate() {
            double result = 0.0;

            // Assuming operatorPriority is a static Map<String, Integer> that exists in your class
            List<OperatorNode> sortedOperatorChildren = operatorChildren.stream()
                    .sorted(Comparator.comparingInt(o -> operatorPriority.getOrDefault(o.getInfo(), Integer.MAX_VALUE)))
                    .toList();

            for (OperatorNode operator : sortedOperatorChildren) {
                result += operator.evaluate();
            }

            result += super.evaluate();

            return result;
        }

        @Override
        public OperatorNode getLeft() {
            return (OperatorNode) left;
        }

        @Override
        public OperatorNode getRight() {
            return (OperatorNode) right;
        }

        public List<OperatorNode> getOperatorChildren() {
            return operatorChildren;
        }

        public void addOperatorChildren(OperatorNode operatorChildren) {
            this.operatorChildren.add(operatorChildren);
        }
    }
    public  class OperatorNode extends MathNode {

        private final boolean isParenthesized;

        public OperatorNode(String info, boolean pIsParenthesized, boolean pInvertSignal) {
            super(info);
            isParenthesized = pIsParenthesized;
            invertSignal = pInvertSignal;
        }

        private OperatorNode(String info, boolean pIsParenthesized, boolean pInvertSignal, MathNode parent) {
            super(info);
            this.isParenthesized = pIsParenthesized;
            this.parent = parent;
            if (this.parent instanceof ParenthesesNode)
            {
                ((ParenthesesNode)this.parent).addOperatorChildren(this);
            }
            this.invertSignal = pInvertSignal;
        }

        public OperatorNode(MathNode parent, String info)
        {
            super(parent, info);
            this.isParenthesized = parent != null && parent.getIsParenthesized();
            this.invertSignal = false;
        }

        @Override
        public boolean getIsParenthesized() {
            return isParenthesized || (parent != null && parent.getIsParenthesized());
        }

        public boolean hasParentParenthesized()
        {
            return hasParent() && this.parent.getIsParenthesized();
        }

        @Override
        public double evaluate() {
            double rst = super.evaluate();

            return invertSignal ? (rst * -1) : rst;
        }

        @Override
        public boolean isOperand() {
            return false;
        }
    }
    public  class OperandNode extends ExpressionBase {

        public OperandNode(String info) {
            super(info);
        }

        public OperandNode(MathNode parent, String info) {
            super(parent, info);
        }


        public double getValue() {
            try
            {
                return Double.parseDouble(info);
            }
            catch (NumberFormatException e) {
                return 0;
            }
        }

        @Override
        public double evaluate() {
            return getValue();
        }

        public double evaluate(boolean applyInvertionSignal)
        {
            double rst = getValue();
            rst = !applyInvertionSignal ? rst : isInvertSignal() || this.hasParent() && this.getParent().isInvertSignal() ? rst * -1 : rst;
            return rst;
        }
        @Override
        public boolean getIsParenthesized() {
            return this.hasParent() && this.parent.getIsParenthesized();
        }

        @Override
        public boolean isOperand()
        {
            return  (this instanceof OperandNode);
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

    }

}
