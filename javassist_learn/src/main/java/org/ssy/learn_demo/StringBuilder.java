package org.ssy.learn_demo;

/**
 * Created by manager on 2018/6/12.
 */
public class StringBuilder {

  public String buildString(int length) {
    String result = "";
    for (int i = 0; i < length; i++) {
      result += (char) (i % 26 + 'a');
    }
    return result;
  }

  // jassist 目标实现的代码
//  private String buildString$impl(int length) {
//    String result = "";
//    for (int i = 0; i < length; i++) {
//      result += (char) (i % 26 + 'a');
//    }
//    return result;
//  }
//
//  private String buildString(int length) {
//    long start = System.currentTimeMillis();
//    String result = buildString$impl(length);
//    System.out.println("Call to buildString took " +
//        (System.currentTimeMillis() - start) + " ms.");
//    return result;
//  }

}
