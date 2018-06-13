package test4;

import java.util.HashMap;

class GetAllRefInnerTest2<T> {
    Class clazz;
    T value;
    void foo(T t) { value = t; }
    Object poi(T t) {
        return new Object() {
            public String toString(T t) { return this.getClass().toString(); }
        };
    }
}

public class GetAllRefInnerTest<T> {
    public T bar(T b) {
        Object obj = new GetAllRefInnerTest2<HashMap>() {
            void foo(HashMap a) { value = null; String s = clazz.toString() + a.toString(); }
        };
        return b;
    }
    public Object foo() {
        return new java.util.HashSet<String>() {
            /** default serialVersionUID */
            private static final long serialVersionUID = 1L;

            public String toString() { return this.getClass().toString(); } 
        };
    }
}
