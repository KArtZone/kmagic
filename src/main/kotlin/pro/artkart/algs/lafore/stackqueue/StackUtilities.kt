package pro.artkart.algs.lafore.stackqueue

import pro.artkart.kmagic.common.Option

fun String.reverse(): String = ArrayStack(length).let { stack ->
    forEach { char -> stack.push(char.code) }
    buildString {
        while (!stack.isEmpty()) {
            append(stack.pop().toChar())
        }
    }
}

fun String.checkBrackets(): String {
    fun checkPair(bracketPair: Pair<Char, Char>): Boolean = when (bracketPair) {
        '{' to '}', '[' to ']', '(' to ')' -> true
        else -> false
    }

    val stack = ArrayStack(length)
    forEachIndexed { index, char ->
        when (char) {
            '{', '[', '(' -> stack.push(char.code)
            '}', ']', ')' -> {
                if (!checkPair(Pair(stack.pop().toChar(), char))) {
                    return "Error: $char at $index"
                }
            }
        }
    }
    return "Ok"
}

fun String.toPostfix(): String = buildString {
    data class Operator(val symbol: Char, val priority: Int)
    Stack<Operator>().let { stack ->
        fun applyOperator(operator: Operator) {
            var shouldContinue = true
            while (!stack.isEmpty() && shouldContinue) {
                stack.pop()
                    .forEach { previousOperator ->
                        if (previousOperator.symbol == '(') {
                            stack.push(previousOperator)
                            shouldContinue = false
                        } else {
                            when {
                                previousOperator.priority >= operator.priority -> append(previousOperator.symbol)
                                else -> {
                                    stack.push(previousOperator)
                                    shouldContinue = false
                                }
                            }
                        }
                    }
            }
            stack.push(operator)
        }

        fun closeParenthesis() {
            var shouldContinue = true
            while (!stack.isEmpty() && shouldContinue) {
                stack.pop()
                    .map { it.symbol }
                    .forEach { symbol ->
                        if (symbol == '(')
                            shouldContinue = false
                        else
                            append(symbol)
                    }
            }
        }
        this@toPostfix.forEach { char ->
            when (char) {
                '+', '-' -> applyOperator(Operator(char, 1))
                '*', '/' -> applyOperator(Operator(char, 2))
                '(' -> stack.push(Operator(char, 3))
                ')' -> closeParenthesis()
                else -> append(char)
            }
        }
        while (!stack.isEmpty())
            stack.pop()
                .map { it.symbol }
                .forEach { append(it) }
    }
}

fun String.evaluate(): Option<Int> {
    val stack = Stack<Int>()
    forEach { char ->
        if (char.isDigit())
            stack.push(char.digitToInt())
        else {
            stack.pop().flatMap { second ->
                stack.pop().map { first ->
                    when (char) {
                        '+' -> stack.push(first + second)
                        '-' -> stack.push(first - second)
                        '/' -> stack.push(first / second)
                        '*' -> stack.push(first * second)
                    }
                }
            }

        }
    }
    return stack.pop()
}
