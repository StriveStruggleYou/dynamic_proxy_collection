package org.ssy.learn_annotation;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.scopedpool.ScopedClassPoolRepository;
import javassist.scopedpool.ScopedClassPoolRepositoryImpl;
import junit.framework.TestCase;

/**
 * Created by manager on 2018/6/13.
 */
public class TestAnnotations extends TestCase {

  private static final ScopedClassPoolRepository repository = ScopedClassPoolRepositoryImpl
      .getInstance();


  public void testJDKClasses() throws Exception {
    ClassPool poolClass = repository.findClassPool(Class.class.getClassLoader());
    assertNotNull(poolClass);
    ClassPool poolString = repository.findClassPool(String.class.getClassLoader());
    assertNotNull(poolString);
    assertEquals(poolClass, poolString);
  }

  public void testUnscopedAnnotationUsage() throws Exception {
    CtClass clazz = getCtClass(UnscopedAnnotationUsage.class);
    checkTestAnnotation(clazz, "notDefault");
  }

  public void testUnscopedAnnotationDefaultUsage() throws Exception {
    CtClass clazz = getCtClass(UnscopedAnnotationDefaultUsage.class);
    checkTestAnnotation(clazz, "defaultValue");
  }


  protected CtClass getCtClass(Class<?> clazz) throws Exception {
    return getCtClass(clazz.getName(), clazz.getClassLoader());
  }

  protected CtClass getCtClass(String name, ClassLoader cl) throws Exception {
    ClassPool pool = repository.findClassPool(cl);
    assertNotNull(pool);
    CtClass clazz = pool.get(name);
    assertNotNull(clazz);
    return clazz;
  }


  protected void checkTestAnnotation(CtClass ctClass, String value) throws Exception {
    checkTestAnnotation(ctClass.getAnnotations(), value);
    checkTestAnnotation(getFieldAnnotations(ctClass), value);
    checkTestAnnotation(getConstructorAnnotations(ctClass), value);
    checkTestAnnotation(getConstructorParameterAnnotations(ctClass), value);
    checkTestAnnotation(getMethodAnnotations(ctClass), value);
    checkTestAnnotation(getMethodParameterAnnotations(ctClass), value);
  }

  protected void checkTestAnnotation(Object[] annotations, String value) throws Exception {
    assertNotNull(annotations);
    assertEquals(1, annotations.length);
    assertNotNull(annotations[0]);
    assertTrue(annotations[0] instanceof TestAnnotation);
    TestAnnotation annotation = (TestAnnotation) annotations[0];
    assertEquals(value, annotation.something());
  }

  protected void checkScopedAnnotation(ClassLoader cl, CtClass ctClass, String value)
      throws Exception {
    Class<?> annotationClass = cl.loadClass("scoped.jar1.ScopedTestAnnotation");
    checkScopedAnnotation(annotationClass, ctClass.getAnnotations(), value);
    checkScopedAnnotation(annotationClass, getFieldAnnotations(ctClass), value);
    checkScopedAnnotation(annotationClass, getConstructorAnnotations(ctClass), value);
    checkScopedAnnotation(annotationClass, getConstructorParameterAnnotations(ctClass), value);
    checkScopedAnnotation(annotationClass, getMethodAnnotations(ctClass), value);
    checkScopedAnnotation(annotationClass, getMethodParameterAnnotations(ctClass), value);
  }

  protected void checkScopedAnnotation(Class<?> annotationClass, Object[] annotations, String value)
      throws Exception {
    assertNotNull(annotations);
    assertEquals(1, annotations.length);
    assertNotNull(annotations[0]);
    assertTrue(annotationClass.isInstance(annotations[0]));

    Method method = annotationClass.getMethod("something", new Class<?>[0]);
    assertEquals(value, method.invoke(annotations[0], (Object[]) null));
  }

  protected Object[] getFieldAnnotations(CtClass clazz) throws Exception {
    CtField field = clazz.getField("aField");
    assertNotNull(field);
    return field.getAnnotations();
  }

  protected Object[] getMethodAnnotations(CtClass clazz) throws Exception {
    CtMethod method = clazz.getMethod("doSomething", "(I)V");
    assertNotNull(method);
    return method.getAnnotations();
  }

  protected Object[] getMethodParameterAnnotations(CtClass clazz) throws Exception {
    CtMethod method = clazz.getMethod("doSomething", "(I)V");
    assertNotNull(method);
    Object[] paramAnnotations = method.getParameterAnnotations();
    assertNotNull(paramAnnotations);
    assertEquals(1, paramAnnotations.length);
    return (Object[]) paramAnnotations[0];
  }

  protected Object[] getConstructorAnnotations(CtClass clazz) throws Exception {
    CtConstructor constructor = clazz.getConstructor("(I)V");
    assertNotNull(constructor);
    return constructor.getAnnotations();
  }

  protected Object[] getConstructorParameterAnnotations(CtClass clazz) throws Exception {
    CtConstructor constructor = clazz.getConstructor("(I)V");
    assertNotNull(constructor);
    Object[] paramAnnotations = constructor.getParameterAnnotations();
    assertNotNull(paramAnnotations);
    assertEquals(1, paramAnnotations.length);
    return (Object[]) paramAnnotations[0];
  }

  protected ClassLoader getURLClassLoader(String context) throws Exception {
    String output = ".";
    File file = new File(output + File.separator + context);
    URL url = file.toURI().toURL();
    return new URLClassLoader(new URL[]{url});
  }


}
