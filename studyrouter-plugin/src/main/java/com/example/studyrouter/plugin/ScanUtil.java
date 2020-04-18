package com.example.studyrouter.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * author: xujiajia
 * description:
 * 用于遍历jar文件和class文件，遍历主要会记录两个内容：
 * 1、记录注解解释器创建的辅助类
 * 2、记录要插入代码的类的位置，即StudyRouterManager的位置
 */
class ScanUtil {

  static void scanFiles(File src) {
    TransformFileUtils.scanFileInDir(src,
        new TransformFileUtils.ScanFileCallback() {
          @Override public void action(File file) {
            System.out.println("scanFiles() path:" + file.getAbsolutePath());
            try {
              if (file.getName().contains(RouterPluginConsts.INIT_CLASS_SUFFIX)) {
                scanClass(new FileInputStream(file));
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
  }

  static void scanJars(File src) {
    try {
      System.out.println("scanJars() path:" + src.getAbsolutePath());
      JarFile jarFile = new JarFile(src);
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        String name = entry.getName();
        if (name.contains(RouterPluginConsts.INIT_CLASS_SUFFIX)) {
          InputStream inputStream = jarFile.getInputStream(entry);
          scanClass(inputStream);
          inputStream.close();
        } else if (name.contains(RouterPluginConsts.ROUTER_MANAGER)) {
          System.out.println("scanJars StudyRouterManager src:" + src.getAbsolutePath());
          RouterTransform.initDestFile = src;
        }
      }
      jarFile.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void scanClass(InputStream inputStream) {
    try {
      ClassReader cr = new ClassReader(inputStream);
      ClassWriter cw = new ClassWriter(cr, 0);
      ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw);
      cr.accept(cv, ClassReader.EXPAND_FRAMES);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static class ScanClassVisitor extends ClassVisitor {

    ScanClassVisitor(int api, ClassVisitor cv) {
      super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
        String superName, String[] interfaces) {
      super.visit(version, access, name, signature, superName, interfaces);
      RouterTransform.listAutoWisedClass.add(name);
    }
  }
}
