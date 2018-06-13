package org.ssy.learn_proxy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import junit.framework.TestCase;

/**
 * Created by manager on 2018/6/13.
 */
public class Learn_proxy extends TestCase {

  String testResult;

  public void testProxyFactory() throws Exception {
    ProxyFactory f = new ProxyFactory();
    f.writeDirectory = "./proxy";
    f.setSuperclass(Foo.class);
    f.setFilter(new MethodFilter() {
      public boolean isHandled(Method m) {
        return m.getName().startsWith("f");
      }
    });
    Class c = f.createClass();
    MethodHandler mi = new MethodHandler() {
      public Object invoke(Object self, Method m, Method proceed,
          Object[] args) throws Throwable {
        testResult += args[0].toString();
        return proceed.invoke(self, args);  // execute the original method.
      }
    };
    Foo foo = (Foo) c.getConstructor().newInstance();
    ((Proxy) foo).setHandler(mi);
    testResult = "";
    foo.foo(1);
    foo.foo2(2);
    foo.bar(3);
    assertEquals("12", testResult);
  }

  public static class Foo {

    public int foo(int i) {
      return i + 1;
    }

    public int foo2(int i) {
      return i + 2;
    }

    public int bar(int i) {
      return i + 1;
    }
  }


  public void testReadWrite() throws Exception {
    final String fileName = "read-write.bin";
    ProxyFactory.ClassLoaderProvider cp = ProxyFactory.classLoaderProvider;
    try {
      ProxyFactory.classLoaderProvider = new ProxyFactory.ClassLoaderProvider() {
        public ClassLoader get(ProxyFactory pf) {
                    /* If javassist.Loader is returned, the super type of ReadWriteData class,
                     * which is Serializable, is loaded by javassist.Loader as well as ReadWriteData.
                     * This breaks the implementation of the object serializer.
                     */
          // return new javassist.Loader();
          return Thread.currentThread().getContextClassLoader();
        }
      };
      ProxyFactory pf = new ProxyFactory();
      pf.setSuperclass(ReadWriteData.class);
      Object data = pf.createClass().getConstructor().newInstance();
      // Object data = new ReadWriteData();
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
      oos.writeObject(data);
      oos.close();
    } finally {
      ProxyFactory.classLoaderProvider = cp;
    }

    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
    Object data2 = ois.readObject();
    ois.close();
    int i = ((ReadWriteData) data2).foo();
    assertEquals(4, i);
  }


  public static class ReadWriteData implements Serializable {

    /**
     * default serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public int foo() {
      return 4;
    }
  }


  public void testWriteReplace() throws Exception {
    ProxyFactory pf = new ProxyFactory();
    pf.setSuperclass(WriteReplace.class);
    Object data = pf.createClass().getConstructor().newInstance();
    assertEquals(data, ((WriteReplace) data).writeReplace());

    ProxyFactory pf2 = new ProxyFactory();
    pf2.setSuperclass(WriteReplace2.class);
    Object data2 = pf2.createClass().getConstructor().newInstance();
    Method meth = data2.getClass().getDeclaredMethod("writeReplace", new Class[0]);
    assertEquals("javassist.util.proxy.SerializedProxy",
        meth.invoke(data2, new Object[0]).getClass().getName());
  }


  public static class WriteReplace implements Serializable {

    /**
     * default serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public Object writeReplace() {
      return this;
    }
  }

  public static class WriteReplace2 implements Serializable {

    /**
     * default serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public Object writeReplace(int i) {
      return Integer.valueOf(i);
    }
  }

  public static interface Default1 {

    default int foo() {
      return 0;
    }

    default int baz() {
      return 2;
    }
  }

  public static interface Default2 extends Default1 {

    default int bar() {
      return 1;
    }
  }

  public static class Default3 implements Default2 {

    public int foo() {
      return Default2.super.foo();
    }
  }


  String valueDefaultMethods = "";


  public void testDefaultMethods() throws Exception {
    valueDefaultMethods = "";
    ProxyFactory f = new ProxyFactory();
    f.writeDirectory = "./proxy";
    f.setSuperclass(Default3.class);
    Class c = f.createClass();
    MethodHandler mi = new MethodHandler() {
      public Object invoke(Object self, Method m, Method proceed,
          Object[] args) throws Throwable {
        valueDefaultMethods += "1";
        return proceed.invoke(self, args);  // execute the original method.
      }
    };
    Default3 foo = (Default3) c.getConstructor().newInstance();
    ((Proxy) foo).setHandler(mi);
    foo.foo();
    foo.bar();
    assertEquals("11", valueDefaultMethods);
  }


  public void testPublicProxy() throws Exception {
    ProxyFactory f = new ProxyFactory();
    f.writeDirectory = "./proxy";
    f.setSuperclass(PubProxy.class);
    Class c = f.createClass();
    MethodHandler mi = new MethodHandler() {
      public Object invoke(Object self, Method m, Method proceed,
          Object[] args) throws Throwable {
        PubProxy.result += args[0].toString();
        return proceed.invoke(self, args);
      }
    };
    PubProxy.result = "";
    PubProxy foo = (PubProxy) c.getConstructor().newInstance();
    ((Proxy) foo).setHandler(mi);
    foo.foo(1);
    foo.bar(2);
    foo.baz(3);
    assertEquals("c1p2q3r", PubProxy.result);
  }

  public static class PubProxy {

    public static String result;

    public PubProxy() {
      result += "c";
    }

    PubProxy(int i) {
      result += "d";
    }

    void foo(int i) {
      result += "p";
    }

    protected void bar(int i) {
      result += "q";
    }

    public void baz(int i) {
      result += "r";
    }
  }


}
