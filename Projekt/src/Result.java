

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;


public abstract class Result<V> implements Serializable {

    @SuppressWarnings("rawtypes")
    private static Result empty = new Empty();

    private Result() {
    }

    public abstract V getOrElse(final V defaultValue);

    public abstract V getOrElse(final Supplier<V> defaultValue);

    public abstract <U> Result<U> map(Function<V, U> f);

    public abstract boolean equals(Object o);

    public Result<V> orElse(Supplier<Result<V>> defaultValue) {
        return map(x -> this).getOrElse(defaultValue);
    }

    public boolean exists(Function<V, Boolean> p) {
        return map(p).getOrElse(false);
    }

    private static class Empty<V> extends Result<V> {
        public Empty() {
            super();
        }


        public V getOrElse(final V defaultValue) {
            return defaultValue;
        }


        public <U> Result<U> map(Function<V, U> f) {
            return empty();
        }

        public <U> Result<U> flatMap(Function<V, Result<U>> f) {
            return empty();
        }

        @Override
        public String toString() {
            return "Empty()";
        }


        public V getOrElse(Supplier<V> defaultValue) {
            return defaultValue.get();
        }


        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof Empty;
        }

        public Result<String> forEachOrFail(Effect<V> c) {
            return empty();
        }
    }

    private static class Failure<V> extends Result<V> {
        private final RuntimeException exception;

        private Failure(String message) {
            super();
            this.exception = new IllegalStateException(message);
        }

        private Failure(RuntimeException e) {
            super();
            this.exception = e;
        }

        private Failure(Exception e) {
            super();
            this.exception = new IllegalStateException(e.getMessage(), e);
        }

        @Override
        public String toString() {
            return String.format("Failure(%s)", exception.getMessage());
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof Failure;
        }

        @Override
        public V getOrElse(V defaultValue) {
            return defaultValue;
        }

        @Override
        public V getOrElse(Supplier<V> defaultValue) {
            return defaultValue.get();
        }

        @Override
        public <U> Result<U> map(Function<V, U> f) {
            return failure(exception);
        }

        public Result<String> forEachOrFail(Effect<V> c) {
            return success(exception.getMessage());
        }
        public Result<RuntimeException> forEachOrException(Effect<V> c) {
            return success(exception);
        }

    }

    private static class Success<V> extends Result<V> {
        private final V value;

        private Success(V value) {
            super();
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("Success(%s)", value.toString());
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Success) && this.value.equals(((Success<?>) o).value);
        }

        @Override
        public V getOrElse(V defaultValue) {
            return value;
        }

        @Override
        public V getOrElse(Supplier<V> defaultValue) {
            return value;
        }

        @Override
        public <U> Result<U> map(Function<V, U> f) {
            return success(f.apply(value));
        }

        public Result<String> forEachOrFail(Effect<V> e) {
            e.apply(this.value);
            return empty();
        }
    }

    public static <V> Result<V> failure(String message) {
        return new Failure<>(message);
    }

    public static <V> Result<V> failure(Exception e) {
        return new Failure<V>(e);
    }

    public static <V> Result<V> failure(RuntimeException e) {
        return new Failure<V>(e);
    }

    public static <V> Result<V> success(V value) {
        return new Success<>(value);
    }

    @SuppressWarnings("unchecked")
    public static <V> Result<V> empty() {
        return empty;
    }


}
