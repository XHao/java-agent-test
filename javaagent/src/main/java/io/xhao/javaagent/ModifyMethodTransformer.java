package io.xhao.javaagent;

import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * ModifyMethodTransformer
 */
public class ModifyMethodTransformer extends AbstractTransformer {

    public ModifyMethodTransformer(List<String> classNames) {
        super(classNames);
    }

    @Override
    protected void updateClass(CtClass clazz) throws CannotCompileException, NotFoundException {
        try {
            CtMethod method = clazz.getDeclaredMethod("echo");
            method.insertAfter("System.out.println(\"Hello World!\");");
        } catch (NotFoundException e) {
        }
    }
}