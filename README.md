# Infix-Calculator
Algorithm exercise: Module that evaluates a string that contains an arithmetic expression

```markdown
# Infix Calculator

Welcome to the Infix Calculator project! This Java program allows you to evaluate mathematical expressions written in infix notation. It can handle basic arithmetic operations, parentheses, and negative numbers.

## Table of Contents
- [Introduction](#introduction)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [Examples](#examples)
- [Main Methods](#main-methods)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Introduction

In mathematics, infix notation is a common way of representing mathematical expressions, where operators are placed between operands. The Infix Calculator project takes infix expressions and evaluates them, providing the result.

## Getting Started

### Prerequisites

Make sure you have the following prerequisites installed on your system:

- Java (JDK 8 or higher)

### Installation

1. Clone the repository to your local machine:

   ```bash
   git clone https://github.com/yourusername/infix-calculator.git
   ```

2. Navigate to the project directory:

   ```bash
   cd infix-calculator
   ```

3. Compile the Java code:

   ```bash
   javac EvaluateMathematicalExpression.java
   ```

## Usage

To use the Infix Calculator, you can create an instance of the `EvaluateMathematicalExpression` class and call the `evaluate` method with your mathematical expression as a string. Here's a basic example:

```java
EvaluateMathematicalExpression evaluator = new EvaluateMathematicalExpression();
double result = evaluator.evaluate("5 + 3 * (10 - 2) / 4");
System.out.println("Result: " + result);
```

## Examples

Here are some example expressions you can try:

- `5 + 3 * (10 - 2) / 4`
- `-(4 + 2) * -3`
- `12 * 123 / (-5 + 2)`

## Main Methods

### `isOperand(String value)`

- Determines if a given string is a numeric operand.
- Returns `true` if the input is a valid numeric value, `false` otherwise.

### `isOperator(String value)`

- Checks if a given string is a valid operator.
- Returns `true` if the input is one of the supported operators (`+`, `-`, `*`, `/`), `false` otherwise.

### `normalizeElementsTokenizing(String expression)`

- Tokenizes and normalizes a mathematical expression.
- Returns a list of strings representing the individual elements of the expression with spaces removed and additional spaces added around parentheses and operators.

### `evaluate(String expression)`

- Evaluates a given infix mathematical expression and returns the result as a `double`.
- Takes an infix expression as input and returns the calculated result.

### `popConnectPush(Stack<ExpressionOperatorNode> opStack, Stack<ExpressionBase> nodeStack, boolean isClosing)`

- Performs the core logic for connecting and pushing operators and nodes.
- Handles operator precedence, balancing, and parenthesized expressions.
- Returns `true` if the operation succeeded, `false` otherwise.

### `infixExpressionToTree(String exp)`

- Converts an infix expression into a syntax tree for evaluation.
- Uses stacks to manage operators and nodes during the conversion.
- Returns the root of the syntax tree.

## Testing

The project includes JUnit tests to ensure the correctness of the calculator. You can run the tests using your preferred IDE or build tool.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.


🚀 Happy calculating! 🧮
```

This README file now includes explanations for the `popConnectPush` and `infixExpressionToTree` methods, providing a complete overview of the project's main methods and their functionalities.
