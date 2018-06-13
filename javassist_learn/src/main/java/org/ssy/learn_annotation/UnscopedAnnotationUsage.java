package org.ssy.learn_annotation;

/**
 * Created by manager on 2018/6/13.
 */
@TestAnnotation(something="notDefault")
public class UnscopedAnnotationUsage
{
  @TestAnnotation(something="notDefault")
  public int aField;

  @TestAnnotation(something="notDefault")
  public UnscopedAnnotationUsage(@TestAnnotation(something="notDefault") int param) {}

  @TestAnnotation(something="notDefault")
  public void doSomething(@TestAnnotation(something="notDefault") int param) {}
}

