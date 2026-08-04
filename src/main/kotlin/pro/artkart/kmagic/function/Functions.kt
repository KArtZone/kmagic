package pro.artkart.kmagic.function

typealias IntToInt = (Int) -> Int

fun sum(a: Int, b: Int): Int = a + b

fun carriedSum(a: Int): (Int) -> Int = { b ->
    a + b
}

val repeatString: (String) -> (Int) -> String = { str -> { n -> str.repeat(n) } }

fun repeatStringPartlySecond(n: Int, f: (String) -> (Int) -> String): (String) -> String = { str ->
    f(str)(n)
}

val repeatStringPartlySecond: (Int, (String) -> (Int) -> String) -> (String) -> String = { n, f ->
    { s -> f(s)(n) }
}

val carriedSumValue: (Int) -> (Int) -> Int = { a -> { b -> a + b } }

fun <T> compose(f1: (T) -> T, f2: (T) -> T): (T) -> T = { x -> f2(f1(x)) }

val composeCarriedValue: ((Int) -> Int) -> ((Int) -> Int) -> (Int) -> Int = { f1 ->
    { f2 ->
        { x -> f2(f1(x)) }
    }
}

val composeCarriedValueAliased: (IntToInt) -> (IntToInt) -> IntToInt = { f1 ->
    { f2 ->
        { x -> f2(f1(x)) }
    }
}

class Typed<T, U, R> {
    val polyComposeCarriedValue: ((T) -> U) -> ((U) -> R) -> (T) -> R = { f1 ->
        { f2 ->
            { x -> f2(f1(x)) }
        }
    }
}

fun <T> varargCompose(vararg f: (T) -> T): (T) -> T = { x ->
    f.reduce { acc, function ->
        { x -> function(acc(x)) }
    }(x)
}

fun <T, U, R> polyCompose(f1: (T) -> U, f2: (U) -> R): (T) -> R = { x ->
    f2(f1(x))
}
