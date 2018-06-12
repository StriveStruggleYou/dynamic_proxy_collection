package org.ssy.learn_demo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/**
 * Created by manager on 2018/6/12.
 * 实现链接如下：https://www.ibm.com/developerworks/cn/java/j-dyn0916/
 *
 */
public class JassistTiming {

  public static void main(String[] argv)
      throws IOException, CannotCompileException, NotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
// start by getting the class file and method
    String clss = "org.ssy.learn_demo.StringBuilder";
    String mname = "buildString";
    CtClass clas = ClassPool.getDefault().get(clss);
// add timing interceptor to the class
    addTiming(clas, mname);
    clas.writeFile();
    Class<?> cc = clas.toClass();
    StringBuilder obj = (StringBuilder) cc.getDeclaredConstructor().newInstance();
    obj.buildString(10);
  }

  private static void addTiming(CtClass clas, String mname)
      throws NotFoundException, CannotCompileException {
    //  get the method information (throws exception if method with
    //  given name is not declared directly by this class, returns
    //  arbitrary choice if more than one with the given name)
    CtMethod mold = clas.getDeclaredMethod(mname);
    //  rename old method to synthetic name, then duplicate the
    //  method with original name for use as interceptor
    String nname = mname + "$impl";
    mold.setName(nname);
    CtMethod mnew = CtNewMethod.copy(mold, mname, clas, null);
    //  start the body text generation by saving the start time
    //  to a local variable, then call the timed method; the
    //  actual code generated needs to depend on whether the
    //  timed method returns a value
    String type = mold.getReturnType().getName();
    StringBuffer body = new StringBuffer();
    body.append("{\nlong start = System.currentTimeMillis();\n");
    if (!"void".equals(type)) {
      body.append(type + " result = ");
    }
    body.append(nname + "($$);\n");
    //  finish body text generation with call to print the timing
    //  information, and return saved value (if not void)
    body.append("System.out.println(\"Call to method " + mname +
        " took \" +\n (System.currentTimeMillis()-start) + " +
        "\" ms.\");\n");
    if (!"void".equals(type)) {
      body.append("return result;\n");
    }
    body.append("}");
//  replace the body of the interceptor method with generated
    //  code block and add it to class
    mnew.setBody(body.toString());
    clas.addMethod(mnew);
    //  print the generated code block just to show what was done
    System.out.println("Interceptor method body:");
    System.out.println(body.toString());
  }
}