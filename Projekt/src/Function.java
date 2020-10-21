

public interface Function<T, U> {

    U apply(T arg);

    // Exercise 2.1
    static Function<Integer, Integer> compose(Function<Integer, Integer> f1, Function<Integer, Integer> f2) {
        return new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer arg) {
                return f1.apply(f2.apply(arg));
            }
        };
    }

    // Exercise 2.2
    static Function<Integer, Integer> composeLambda(Function<Integer, Integer> f1, Function<Integer, Integer> f2) {
        return arg -> f1.apply(f2.apply(arg));
    }

    // Exercise 2.2a
    public static <T, V, U> Function<V, U> composeGeneric(Function<V, T> f1, Function<T, U> f2) {
        return arg -> f2.apply(f1.apply(arg));
    }

    // Aufgabe not
    public static Function<Boolean, Boolean> not() {
        return x -> !x;
    }
}
