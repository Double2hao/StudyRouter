package com.example.studyrouter.plugin;

import java.io.File;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;

/**
 * author: xujiajia
 * description:
 * 先遍历jar文件和class文件，然后再根据遍历的内容插入初始化的代码。
 * 遍历主要会记录两个内容：
 * 1、记录注解解释器创建的辅助类
 * 2、记录要插入代码的类的位置，即StudyRouterManager的位置
 */
public class RouterTransform extends Transform {

  public static File initDestFile;
  public static LinkedList<String> listAutoWisedClass = new LinkedList<String>();

  RouterTransform() {
    initDestFile = null;
    listAutoWisedClass.clear();
  }

  //用于指明本Transform的名字，也是代表该Transform的task的名字
  @Override public String getName() {
    return "RouterTransform";
  }

  //用于指明Transform的输入类型，可以作为输入过滤的手段。
  @Override public Set<QualifiedContent.ContentType> getInputTypes() {
    return TransformManager.CONTENT_CLASS;
  }

  //用于指明Transform的作用域
  @Override public Set<? super QualifiedContent.Scope> getScopes() {
    return TransformManager.SCOPE_FULL_PROJECT;
  }

  //是否增量编译
  @Override public boolean isIncremental() {
    return false;
  }

  @Override public void transform(TransformInvocation invocation) {
    System.out.println("RouterTransform transform start");

    for (TransformInput input : invocation.getInputs()) {
      //遍历jar文件
      input.getJarInputs().parallelStream().forEach(jarInput -> {
        File src = jarInput.getFile();
        File dst = invocation.getOutputProvider().getContentLocation(
            jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(),
            Format.JAR);
        try {
          FileUtils.copyFile(src, dst);
          ScanUtil.scanJars(dst);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
      //遍历class文件
      input.getDirectoryInputs().parallelStream().forEach(directoryInput -> {
        File src = directoryInput.getFile();
        System.out.println("input.getDirectoryInputs fielName:" + src.getName());
        File dst = invocation.getOutputProvider().getContentLocation(
            directoryInput.getName(), directoryInput.getContentTypes(),
            directoryInput.getScopes(), Format.DIRECTORY);
        try {
          FileUtils.copyDirectory(src, dst);
          ScanUtil.scanFiles(dst);
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      });
    }
    System.out.println("listAutoWisedClass:" + listAutoWisedClass.toString());
    System.out.println("initDestFile:" + initDestFile);
    if (listAutoWisedClass.size() > 0 && initDestFile != null) {
      GeneratorCodeUtil.insertInitCodeIntoJarFile(initDestFile);
    }

    System.out.println("RouterTransform transform finish");
  }
}
