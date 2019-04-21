package io.xhao.javaagent;

import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * Transformer
 */
public class AddMethodTransformer extends AbstractTransformer {

    public AddMethodTransformer(List<String> classNames) {
        super(classNames);
    }

    @Override
    protected void updateClass(CtClass clazz) throws CannotCompileException, NotFoundException {
        CtMethod method = new CtMethod(ClassPool.getDefault().get("void"), "hello", new CtClass[0], clazz);
        method.setModifiers(Modifier.PUBLIC);
        method.setBody("{System.out.println(\"Hello World!\");}");
        clazz.addMethod(method);
    }
}