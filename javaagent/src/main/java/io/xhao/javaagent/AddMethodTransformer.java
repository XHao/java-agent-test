package io.xhao.javaagent;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
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
    List<String> classNames = new ArrayList<>();

    public AddMethodTransformer(List<String> classNames) {
        for (String var : classNames) {
            this.classNames.add(var.replace(".", "/"));
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null || !classNames.contains(className)) {
            return classfileBuffer;
        }

        try {
            CtClass clazz = getCtClass(classfileBuffer, loader);
            updateClass(clazz);
            return clazz.toBytecode();
        } catch (IOException | RuntimeException | CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }

        return EMPTY_BYTE_ARRAY;
    }

    private void updateClass(CtClass clazz) throws CannotCompileException, NotFoundException {
        CtMethod method = new CtMethod(ClassPool.getDefault().get("void"), "hello", new CtClass[0], clazz);
        method.setModifiers(Modifier.PUBLIC);
        StringBuilder methodBody = new StringBuilder();
        methodBody.append("{System.out.println(\"Hello World!\");}");
        method.setBody(methodBody.toString());
        clazz.addMethod(method);
    }
}