

import java.util.Set;
import java.util.function.Predicate;

//import Function;
//import legacy.TailCall;

/*import set.Set;
import static set.ListSet.*;*/

public abstract class List<A> {

    public abstract A head();

    public abstract List<A> tail();

    public abstract boolean isEmpty();

    @SuppressWarnings("rawtypes")
    public static final List NIL = new Nil();

    private List() {
    }

    public List<A> cons(A a) {
        return new Cons<>(a, this);
    }

    public abstract List<A> setHead(A h);

    // -----------------------------------------------------------Instanzmethoden
    public abstract int length();

    public abstract boolean elem(A x);

    public abstract boolean any(Function<A, Boolean> p);

    public abstract boolean all(Function<A, Boolean> p);

    public abstract <B> List<B> map(Function<A, B> f);

    public abstract List<A> filter(Function<A, Boolean> f);

    public abstract A finde(Function<A, Boolean> f);

    public abstract List<A> init();

    public abstract A last();

    public abstract List<A> take(int n);

    public abstract List<A> drop(int n);

    public abstract List<A> takeWhile(Function<A, Boolean> p);

    public abstract List<A> dropWhile(Function<A, Boolean> p);

    public abstract List<A> delete(A x);

    public abstract boolean isEqualTo(List<A> xs);

    public abstract boolean equals(Object o);

    public abstract Result<A> find(Function<A, Boolean> p);

    public abstract Result<A> headOption();

    // ----------------------------------------------------------------------Nil
    private static class Nil<A> extends List<A> {
        private Nil() {
        }

        public A head() {
            throw new IllegalStateException("head called en empty list");
        }

        public List<A> tail() {
            throw new IllegalStateException("tail called en empty list");
        }

        public boolean isEmpty() {
            return true;
        }

        public List<A> cons(A a) {
            return new Cons<>(a, this);
        }

        @Override
        public List<A> setHead(A h) {
            throw new IllegalStateException("setHead called on empty list");
        }

        public String toString() {
            return "[NIL]";
        }

        //-----------------------------------------------------------Instanzmethoden
        @Override
        public int length() {
            return 0;
        }

        @Override
        public boolean elem(A x) {
            return false;
        }

        @Override
        public boolean any(Function<A, Boolean> p) {
            return false;
        }

        @Override
        public boolean all(Function<A, Boolean> p) {
            return true;
        }

        @Override
        public <B> List<B> map(Function<A, B> f) {
            return list();
        }

        @Override
        public List<A> filter(Function<A, Boolean> f) {
            return list();
        }

        @Override
        public A finde(Function<A, Boolean> f) {
            return null;
        }

        @Override
        public List<A> init() {
            return list();
        }

        @Override
        public A last() {
            throw new IllegalStateException("last called on empty list");
        }

        @Override
        public List<A> take(int n) {
            return list();
        }

        @Override
        public List<A> drop(int n) {
            return list();
        }

        @Override
        public List<A> takeWhile(Function<A, Boolean> p) {
            return list();
        }

        @Override
        public List<A> dropWhile(Function<A, Boolean> p) {
            return list();
        }

        @Override
        public List<A> delete(A x) {
            return list();
        }

        @Override
        public boolean isEqualTo(List<A> xs) {
            return xs.isEmpty();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Nil<?>;
        }

        @Override
        public Result<A> headOption() {
            return Result.empty();
        }

        @Override
        public Result<A> find(Function<A, Boolean> p) {
            return Result.empty();
        }

        public void forEach(Effect<A> ef) {
        // Do nothing
        }
    }

    // ----------------------------------------------------------------------Cons
    private static class Cons<A> extends List<A> {
        private final A head;
        private final List<A> tail;

        private Cons(A head, List<A> tail) {
            this.head = head;
            this.tail = tail;
        }

        public A head() {
            return head;
        }

        public List<A> tail() {
            return tail;
        }

        public boolean isEmpty() {
            return false;
        }

        public List<A> cons(A a) {
            return new Cons<>(a, this);
        }

        @Override
        public List<A> setHead(A h) {
            return new Cons<>(h, tail());
        }

        public String toString() {
            return String.format("[%sNIL]", toString(new StringBuilder(), this).eval());
        }

        private TailCall<StringBuilder> toString(StringBuilder acc, List<A> list) {
            return list.isEmpty() ? TailCall.ret(acc)
                    : TailCall.sus(() -> toString(acc.append(list.head()).append(", "), list.tail()));
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Cons)) return false;

            List<A> list = (Cons<A>) o;

            return (head().equals(list.head()) && tail.equals(list.tail()));
        }

        // -----------------------------------------------------------Instanzmethoden
        @Override
        public int length() {
            return 1 + tail.length();
        }

        @Override
        public boolean elem(A x) {
            return x == head ? true : tail.elem(x);
        }

        @Override
        public boolean any(Function<A, Boolean> p) {
            return p.apply(head) ? true : tail.any(p);
        }

        @Override
        public boolean all(Function<A, Boolean> p) {
            return p.apply(head) && tail.all(p);
        }

        @Override
        public <B> List<B> map(Function<A, B> f) {
            return append(list(f.apply(head)), tail.map(f));
        }

        @Override
        public List<A> filter(Function<A, Boolean> f) {
            return f.apply(head) ? append(list(head), tail.filter(f)) : tail.filter(f);
        }

        @Override
        public A finde(Function<A, Boolean> f) {
            return f.apply(head) ? head : tail.finde(f);
        }

        @Override
        public List<A> init() {
            return tail.length() == 1 ? list(head) : append(list(head), tail.init());
        }

        @Override
        public A last() {
            return tail.isEmpty() ? head : tail.last();
        }

        @Override
        public List<A> take(int n) {
            return n == 1 ? list(head) : append(list(head), tail.take(n - 1));
        }

        @Override
        public List<A> drop(int n) {
            return n == 1 ? tail : tail.drop(n - 1);
        }

        @Override
        public List<A> takeWhile(Function<A, Boolean> p) {
            return p.apply(head) ? append(list(head), tail.takeWhile(p)) : list();
        }

        @Override
        public List<A> dropWhile(Function<A, Boolean> p) {
            return p.apply(head) ? tail.dropWhile(p) : this;
        }

        @Override
        public List<A> delete(A x) {
            return head == x ? tail : append(list(head), tail.delete(x));
        }

        @Override
        public boolean isEqualTo(List<A> xs) {
            return tail.length() + 1 != xs.length() ? false
                    : head != xs.head() ? false : true && tail.isEqualTo(xs.tail());
        }

        @Override
        public Result<A> headOption() {
            return Result.success(head);
        }

        @Override
        public Result<A> find(Function<A, Boolean> p) {
            return p.apply(this.head) ? this.headOption() : this.tail.find(p);
        }

        public void forEach(Effect<A> ef) {
            forEach(this, ef).eval();
        }

        private static <A> TailCall<List<A>> forEach(List<A> list, Effect<A> ef) {
            return list.isEmpty()
                    ? TailCall.ret(list)
                    : TailCall.sus(() -> {
                ef.apply(list.head());
                return forEach(list.tail(), ef);
            });
        }
    }
    // ---------------------------------------------------------------------List

    @SuppressWarnings("unchecked")
    public static <A> List<A> list() {
        return NIL;
    }

    @SafeVarargs
    public static <A> List<A> list(A... a) {
        List<A> n = list();
        for (int i = a.length - 1; i >= 0; i--) {
            n = new Cons<>(a[i], n);
        }
        return n;
    }

    // ----------------------------------------------------------------------Klassenmethoden

    public static Integer sum(List<Integer> list) {
        return sum(list, 0);
    }

    private static Integer sum(List<Integer> list, int i) {
        return list.isEmpty() ? i : sum(list.tail(), i + list.head());
    }

    public static Double prod(List<Double> list) {
        return prod(list, 0.0);
    }

    private static Double prod(List<Double> list, Double i) {
        return list.isEmpty() ? i : prod(list.tail(), i * list.head());
    }

    public static <A> List<A> append(List<A> list1, List<A> list2) {
        return list1.isEmpty() ? list2 : new Cons<A>(list1.head(), append(list1.tail(), list2));
    }

    public static <A> List<A> concat(List<List<A>> list) {
        return list.isEmpty() ? list() : append(list.head(), concat(list.tail()));
    }

    public static <A> List<A> reverse(List<A> xs) {
        return xs.isEmpty() ? list() : append(reverse(xs.tail()), list(xs.head()));
    }

    public static boolean and(List<Boolean> list) {
        return list.isEmpty() ? true : list.head() && and(list.tail());
    }

    public static boolean or(List<Boolean> list) {
        return list.isEmpty() ? false : list.head() || or(list.tail());
    }

//    public static Integer minimum(List<Integer> list) {
//
//        return list.length() == 1 || list.head() <= minimum(list.tail()) ? list.head() : minimum(list.tail());
//    }

    public static <A extends Comparable<A>> A minimum(List<A> list) {
        return list.tail().isEmpty()
                ? list.head()
                : list.head().compareTo(list.tail().head()) < 0
                ? minimum(list.tail().setHead(list.head()))
                : minimum(list.tail());
    }

//    public static Integer maximum(List<Integer> list) {
//        return list.length() == 1 || list.head() >= maximum(list.tail()) ? list.head() : maximum(list.tail());
//    }

    public static <A extends Comparable<A>> A maximum(List<A> list) {
        return list.tail().isEmpty()
                ? list.head()
                : list.head().compareTo(list.tail().head()) > 0
                ? maximum(list.tail().setHead(list.head()))
                : maximum(list.tail());
    }

    // ------------------------------------------------------foldr

    public static <A, B> B foldr(Function<A, Function<B, B>> f, B s, List<A> xs) {
        return xs.isEmpty() ? s : f.apply(xs.head()).apply(foldr(f, s, xs.tail()));
    }

    public static Integer sum_foldr(List<Integer> list) {
        return foldr(x -> y -> x + y, 0, list);
    }

    public static Integer prod_foldr(List<Integer> list) {
        return foldr(x -> y -> x * y, 1, list);
    }

    public static <A> Integer length_foldr(List<A> list) {
        return foldr(x -> y -> 1 + y, 0, list);
    }

    public static <A> boolean elem_foldr(List<A> list, A a) {
        return foldr(x -> y -> x.equals(a), false, list);
    }

    public static <A> boolean any_foldr(List<A> list, Predicate<A> p) {
        return foldr(x -> y -> p.test(x) || y, false, list);
    }

    public static <A> boolean all_foldr(List<A> list, Predicate<A> p) {
        return foldr(x -> y -> p.test(x) && y, true, list);
    }

    public static boolean and_foldr(List<Boolean> list) {
        return foldr(x -> y -> x && y, true, list);
    }

    public static boolean or_foldr(List<Boolean> list) {
        return foldr(x -> y -> x || y, true, list);
    }

    public static <A> List<A> append_foldr(List<A> list1, List<A> list2) {
        return foldr(x -> y -> new Cons<>(x, y), list2, list1);
    }

    public static <A> List<A> concat_foldr(List<List<A>> list) {
        return foldr(x -> y -> append_foldr(x, y), list(), list);
    }

    public static <A, B> List<B> map_foldr(List<A> list, Function<A, B> f) {
        return foldr(x -> y -> new Cons<B>(f.apply(x), y), list(), list);
    }

    public static <A, B> List<A> filter_foldr(List<A> list, Function<A, Boolean> p) {
        return foldr(x -> y -> p.apply(x) ? new Cons<A>(x, y) : y, list(), list);
    }

    public static <A> List<A> takeWhile_foldr(List<A> list, Predicate<A> p) {
        return foldr(x -> y -> p.test(x) ? new Cons<A>(x, y) : list(), list(), list);
    }

    public static <A> String toString_foldr(List<A> list) {
        return "[ " + foldr(x -> y -> x + "," + y, "", list) + " ]";
    }

    public static <A> List<A> reverse_foldr(List<A> list) {
        return foldr(x -> y -> append_foldr(y, list(x)), list(), list);
    }

    // ---------------------------------------------------------foldl

    public static <A, B> B foldl(Function<B, Function<A, B>> f, B s, List<A> xs) {
        return xs.isEmpty() ? s : foldl(f, f.apply(s).apply(xs.head()), xs.tail());
    }

    public static Integer sum_foldl(List<Integer> list) {
        return foldl(x -> y -> x + y, 0, list);
    }

    public static Integer prod_foldl(List<Integer> list) {
        return foldl(x -> y -> x * y, 1, list);
    }

    public static <A> Integer length_foldl(List<A> list) {
        return foldl(y -> x -> 1 + y, 0, list);
    }

    public static <A> Boolean elem_foldl(List<A> list, A a) {
        return foldl(y -> x -> a == y, false, list);
    }

    public static Boolean and_foldl(List<Boolean> list) {
        return foldl(y -> x -> x && y, true, list);
    }

    public static Boolean or_foldl(List<Boolean> list) {
        return foldl(y -> x -> x || y, true, list);
    }

    public static <A> Boolean any_foldl(List<A> list, Function<A, Boolean> p) {
        return foldl(y -> x -> p.apply(x) || y, false, list);
    }

    public static <A> Boolean all_foldl(List<A> list, Predicate<A> p) {
        return foldl(y -> x -> p.test(x) && y, true, list);
    }

    public static <A> A last_foldl(List<A> list) {
        return foldl(y -> x -> x, list.head(), list);
    }

    public static <A> List<A> reverse_foldl(List<A> list) {
        return foldl(y -> x -> append_foldr(list(x), y), list(), list);
    }

    //-----------------------------------------------------------using any 

    public static <A> Boolean elem_using_any(List<A> list, A a) {
        return any_foldl(list, y -> y == a);
    }

    public static <A> Boolean all_using_any(List<A> list, Function<A, Boolean> p) {
        return !any_foldl(list, p);
    }

    /**
     * Anders als bei der Rechtsfaltung wird die Liste nicht zuerst ganz bis zum Ende durchlaufen,
     * bevor mit der Auswertung der Funktion f begonnen wird.
     * Stattdessen wird die Liste vom Anfang beginnend gefaltet
     * <p>
     * Da f s x anders als bei der Rechtsfaltung in jedem Rekursionsschritt direkt berechnet werden
     * kann, arbeitet die Linksfaltung mit konstantem Speicherbedarf, unabhängig von der Länge der
     * zu verarbeitenden Liste.
     * Es muss also abgewogen werden, ob lieber eine Links- oder Rechtsfaltung verwendet werden soll.
     * Linksfaltungen bieten sich immer dann an, wenn die Funktion f ohnehin keine Teilergebnisse
     * liefern kann, wie das zum Beispiel bei allen arithmetischen Operatoren der Fall ist.
     */

    //-------------------------------------------------------------flatmap
    public static <A, B> List<B> flatMap(Function<A, List<B>> f, List<A> list) {
        return foldr(x -> y -> append(f.apply(x), y), list(), list);
    }
    //--------------------------------------------------------------statische Fabrikmethoden

    public static List<Integer> range(int start, int end) {
        return start > end ? list() : append(list(start), range(start + 1, end));
    }

    public static List<String> words(String s) {
        return s.isEmpty() ? list() : list(s.split("\\s+"));
    }

    //--------------------------------------------------------------Euler 1 & 5

    public static Integer ggT(int x, int y) {
        return y == 0 ? x : ggT(y, x % y);
    }

    public static Integer kgV(int x, int y) {
        return (x * y) / ggT(x, y);
    }

    public static Integer euler5(List<Integer> list) {
        return foldl(y -> x -> kgV(x, y), 1, list);
    }

    public static List<Integer> euler1(List<Integer> list) {
        return list.filter(x -> (x % 3 == 0 || x % 5 == 0) && x != 0);
    }

    //----------------------------------------------------------------------Set
/*    public Set<A> toSet() {
        return fromList(this);
    }

    public List<A> nub(){
        return toSet().toList();
    }*/


}