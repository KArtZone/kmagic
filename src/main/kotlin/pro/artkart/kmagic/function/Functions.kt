package pro.artkart.kmagic.function

typealias IntToInt = (Int) -> Int

fun sum(a: Int, b: Int): Int = a + b

fun carriedSum(a: Int): (Int) -> Int = { b ->
    a + b
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

fun <T> varargCompose(vararg f: (T) -> T): (T) -> T = {
    f.reduce { acc, function ->
        { x -> function(acc(x)) }
    }(it)
}

fun <T> varargComposeUgly(vararg f: (T) -> T): (T) -> T = { x ->
    var result: T = x
    f.forEach { result = it(result) }
    result
}

fun <T, U, R> polyCompose(f1: (T) -> U, f2: (U) -> R): (T) -> R = { x ->
    f2(f1(x))
}

fun <A, B, C, D> f(a: A, b: B, c: C, d: D): String = "$a $b $c $d"

fun <A, B, C, D> carriedF() = { a: A ->
    { b: B ->
        { c: C ->
            { d: D -> "$a $b $c $d" }
        }
    }
}

class F<A, B, C, D> {
    val carriedFValue: (A) -> (B) -> (C) -> (D) -> String = { a ->
        { b ->
            { c ->
                { d -> "$a $b $c $d" }
            }
        }
    }
}

fun <A, B, C> carriedG(g: (A, B) -> C): (A) -> (B) -> C = { a ->
    { b -> g(a, b) }
}

fun <T, U, R> converted(f: (T) -> (U) -> R): (U) -> (T) -> R = { u ->
    { t ->
        f(t)(u)
    }
}
