package org.ssy.learn_annotation;

/**
 * Created by manager on 2018/6/13.
 */
@TestAnnotation
public class UnscopedAnnotationDefaultUsage {

  @TestAnnotation
  public int aField;

  @TestAnnotation
  public UnscopedAnnotationDefaultUsage(@TestAnnotation int param) {
  }

  @TestAnnotation
  public void doSomething(@TestAnnotation int param) {
  }
}

