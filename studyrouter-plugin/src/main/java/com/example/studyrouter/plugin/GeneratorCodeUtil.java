package com.example.studyrouter.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * author: xujiajia
 * description:
 * 用于在RouterManager中插入代码
 * 这里只需要处理jar的情况就可以，因为RouterManager写在一个单独的module中，其他模块会以jar的方式引用
 */
public class GeneratorCodeUtil {

  public static void insertInitCodeIntoJarFile(File jarFile) {
    if (jarFile == null) {
      return;
    }
    try {
      File optJar = new File(jarFile.getParent(), jarFile.getName() + ".opt");
      if (optJar.exists()) {
        optJar.delete();
      }
      JarFile file = new JarFile(jarFile);
      Enumeration enumeration = file.entries();
      JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar));

      while (enumeration.hasMoreElements()) {
        JarEntry jarEntry = (JarEntry) enumeration.nextElement();
        String entryName = jarEntry.getName();
        ZipEntry zipEntry = new ZipEntry(entryName);
        InputStream inputStream = file.getInputStream(jarEntry);
        jarOutputStream.putNextEntry(zipEntry);
        if (entryName.contains(RouterPluginConsts.ROUTER_MANAGER)) {
          byte[] bytes = referHackWhenInit(inputStream);
          jarOutputStream.write(bytes);
        } else {
          jarOutputStream.write(IOUtils.toByteArray(inputStream));
        }
        inputStream.close();
        jarOutputStream.closeEntry();
      }
      jarOutputStream.close();
      file.close();

      if (jarFile.exists()) {
        jarFile.delete();
      }
      optJar.renameTo(jarFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static byte[] referHackWhenInit(InputStream inputStream) {
    System.out.println("referHackWhenInit");
    try {
      //读取class文件并且插入代码
      ClassReader cr = new ClassReader(inputStream);
      ClassWriter cw = new ClassWriter(cr, 0);
      ClassVisitor cv = new MyClassVisitor(Opcodes.ASM5, cw);
      cr.accept(cv, ClassReader.EXPAND_FRAMES);
      return cw.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static class MyClassVisitor extends ClassVisitor {

    MyClassVisitor(int api, ClassVisitor cv) {
      super(api, cv);
    }

    public void visit(int version, int access, String name, String signature,
        String superName, String[] interfaces) {
      super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
        String signature, String[] exceptions) {
      MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
      if (name.equals(RouterPluginConsts.ROUTER_MANAGER_INIT_METHOD)) {
        mv = new MyMethodVisitor(Opcodes.ASM5, mv);
      }
      return mv;
    }
  }

  private static class MyMethodVisitor extends MethodVisitor {

    MyMethodVisitor(int api, MethodVisitor mv) {
      super(api, mv);
    }

    @Override
    public void visitInsn(int opcode) {

      if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
        System.out.println("transform autowised:" + RouterTransform.listAutoWisedClass.toString());
        for (String className : RouterTransform.listAutoWisedClass) {
          String name = className.replaceAll("/", ".");
          mv.visitLdcInsn(name);
          mv.visitMethodInsn(Opcodes.INVOKESTATIC,
              RouterPluginConsts.ROUTER_MANAGER,
              RouterPluginConsts.AUTO_WISED_CLASS_INIT_METHOD,
              "(Ljava/lang/String;)V",
              false);
        }
      }
      super.visitInsn(opcode);
    }

    @Override public void visitMaxs(int maxStack, int maxLocals) {
      super.visitMaxs(maxStack + 1, maxLocals);
    }
  }
}
