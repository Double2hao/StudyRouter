package com.example.studyrouter.processor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.example.studyrouter.annotation.StudyRouter;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * author: xujiajia
 * description:
 * 通过使用注解解释器来生成辅助类，辅助类如：com.example.generated.SubActivity_Router_AutoWised
 */
@AutoService(Processor.class)
//@AutoService(Processor.class)是一个注解处理器，是Google开发的，用来生成META-INF/services/javax.annotation.processing.Processor文件的。
public class StudyRouterProcessor extends AbstractProcessor {

  @Override public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
  }

  //这个方法非常必要，否则将不会执行到process()方法
  @Override public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(StudyRouter.class.getCanonicalName());
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
    System.out.println("TestRouterProcessor process");
    if (annotations == null || annotations.isEmpty()) {
      return false;
    }
    for (Element element : env.getElementsAnnotatedWith(StudyRouter.class)) {
      //获取注解中的内容
      TypeMirror tm = element.asType();
      String className = tm.toString();
      String classSimpleName = element.getSimpleName().toString();
      String key = element.getAnnotation(StudyRouter.class).key();

      //使用javapoet来动态生成代码
      try {
        ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
            ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(String.class));
        ParameterSpec rootParamSpec =
            ParameterSpec.builder(inputMapTypeOfRoot, "routerMap").build();
        MethodSpec main = MethodSpec.methodBuilder(ProcessorConsts.INIT_METHOD)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(void.class)
            .addParameter(rootParamSpec)
            .addStatement("routerMap.put($S,$S)", key, className)
            .build();

        TypeSpec testRouterInit =
            TypeSpec.classBuilder(classSimpleName + ProcessorConsts.INIT_CLASS_SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder(ProcessorConsts.INIT_PACKAGE, testRouterInit)
            .build();

        Filer filer = processingEnv.getFiler();
        javaFile.writeTo(filer);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return true;
  }
}
