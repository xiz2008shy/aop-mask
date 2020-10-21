package com.tomqi.demo.test;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import java.io.FileOutputStream;

import static org.objectweb.asm.Opcodes.*;


/**
 * @author TOMQI
 * @Title: MaskCreater
 * @ProjectName: aop_mask
 * @Description :TODO
 * @data 2020/10/1821:18
 **/
public class AmsDemo extends ClassLoader {

    public static void main(String[] args) throws Exception {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_8,ACC_PUBLIC,"Example",null,"java/lang/Object",null);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(ALOAD,0);
        mv.visitMethodInsn(INVOKESPECIAL,"java/lang/Object","<init>","()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(1,1);
        mv.visitEnd();

        MethodVisitor main = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        main.visitFieldInsn(GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;");
        main.visitLdcInsn("Hello World!");
        main.visitMethodInsn(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V");
        main.visitInsn(RETURN);
        main.visitMaxs(2,2);
        main.visitEnd();

        byte[] code = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream("Example.class");
        fos.write(code);
        fos.close();

        AmsDemo loader = new AmsDemo();
        Class<?> example = loader.defineClass("Example",code,0,code.length);
        System.out.println(example);
    }
}
