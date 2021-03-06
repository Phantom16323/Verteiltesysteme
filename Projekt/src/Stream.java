import java.util.function.Supplier;

abstract class Stream<A> {
    private static Stream EMPTY = new Empty();

    public abstract A head();

    public abstract Stream<A> tail();

    public abstract Boolean isEmpty();

    public abstract Result<A> headOption();

    /**
     * Returns the first n elements of a stream.
     * Beware, this method must be called on the stream
     * before converting it to a list
     *
     * @param n number of elements
     * @return a stream with the corresponding number of elements
     */
    public abstract Stream<A> take(int n);

    /**
     * Returns the remaining stream after removing the first n elements.
     *
     * @param n number of elements
     * @return a stream withe the remaining elements
     */
    public abstract Stream<A> drop(int n);

    /**
     * Returns a stream containing all starting
     * elements as long as a condition is matched
     *
     * @param p a function returning a bool
     */
    public abstract Stream<A> takeWhile(Function<A, Boolean> p);


    private Stream() {
    }

    private static class Empty<A> extends Stream<A> {
        @Override
        public Stream<A> tail() {
            throw new IllegalStateException("tail called on empty");
        }

        @Override
        public A head() {
            throw new IllegalStateException("head called on empty");
        }

        @Override
        public Boolean isEmpty() {
            return true;
        }

        @Override
        public Result<A> headOption() {
            return Result.empty();
        }

        @Override
        public Stream<A> take(int n) {
            return this;
        }

        @Override
        public Stream<A> drop(int n) {
            return this;
        }

        @Override
        public Stream<A> takeWhile(Function<A, Boolean> p) {
            return this;
        }
    }

    private static class Cons<A> extends Stream<A> {
        private final Supplier<A> head;
        private A h;
        private final Supplier<Stream<A>> tail;
        private Stream<A> t;

        private Cons(Supplier<A> h, Supplier<Stream<A>> t) {
            head = h;
            tail = t;
        }

        @Override
        public A head() {
            if (h == null) {
                h = head.get();
            }
            return h;
        }

        @Override
        public Stream<A> tail() {
            if (t == null) {
                t = tail.get();
            }
            return t;
        }

        @Override
        public Boolean isEmpty() {
            return false;
        }

        @Override
        public Result<A> headOption() {
            return Result.success(head());
        }

        @Override
        public Stream<A> take(int n) {
            return n <= 0
                    ? empty()
                    : cons(head, () -> tail().take(n - 1));

        }

        @Override
        public Stream<A> drop(int n) {
            return drop(this, n).eval();
        }

        @Override
        public Stream<A> takeWhile(Function<A, Boolean> p) {
            return p.apply(head()) ? cons(head, () -> tail().takeWhile(p)) : empty();
        }
    }

    static <A> Stream<A> cons(Supplier<A> hd, Supplier<Stream<A>> tl) {
        return new Cons<>(hd, tl);
    }

    static <A> Stream<A> cons(Supplier<A> hd, Stream<A> tl) {
        return new Cons<>(hd, () -> tl);
    }

    @SuppressWarnings("unchecked")
    public static <A> Stream<A> empty() {
        return EMPTY;
    }

    public static Stream<Integer> from(int i) {
        return iterate(i, x -> x + 1);
    }

    public List<A> toList() {
        return List.reverse(toList(this, List.list()).eval());
    }

    private TailCall<List<A>> toList(Stream<A> s, List<A> acc) {
        return s.isEmpty() ? TailCall.ret(acc) : TailCall.sus(() -> toList(s.tail(), acc.cons(s.head())));
    }

    public TailCall<Stream<A>> drop(Stream<A> acc, int n) {
        return n <= 0
                ? TailCall.ret(acc)
                : TailCall.sus(() -> drop(acc.tail(), n - 1));
    }

    /**
     * Returns a stream with the front elements removed as
     * long as they satisfy a condition
     *
     * @param p a function returning a bool
     */
    public Stream<A> dropWhile(Function<A, Boolean> p) {
        return dropWhile(this, p).eval();
    }

    private TailCall<Stream<A>> dropWhile(Stream<A> acc,
                                          Function<A, Boolean> p) {
        return acc.isEmpty()
                ? TailCall.ret(acc) : p.apply(acc.head()) ? TailCall.sus(() -> dropWhile(acc.tail(), p))
                : TailCall.ret(acc);
    }

    /**
     * This method traverses the stream until an element is found satisfying the predicate p.
     *
     * @param p a function returning a bool
     */
    public boolean exists(Function<A, Boolean> p) {
        return exists(this, p).eval();
    }

    private TailCall<Boolean> exists(Stream<A> s, Function<A, Boolean> p) {
        return s.isEmpty()
                ? TailCall.ret(false) : p.apply(s.head()) ? TailCall.ret(true)
                : TailCall.sus(() -> exists(s.tail(), p));
    }

    /**
     * This method takes a object as its parameter and returns an infinite stream of the same object.
     *
     * @param a the repeated object
     * @return an infinite stream consisting of the object
     */
    public static <A> Stream<A> repeat(A a) {
        return iterate(a, x -> x);
    }

    /**
     * This method takes two parameters
     * the seed, which is used as the first value,
     * and a function which will compute the next one.
     * @param seed the first value
     * @param f the function which generates the next value
     * @return a stream based on the seed and function
     */
    public static <A> Stream<A> iterate(A seed, Function<A, A> f) {
        return cons(() -> seed, () -> iterate(f.apply(seed), f));
    }



}
