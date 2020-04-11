package com.example.studyrouter.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import com.android.build.gradle.BaseExtension;

public class RouterPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    System.out.println("studyPlugin apply()");
    project.getExtensions().findByType(BaseExtension.class)
        .registerTransform(new RouterTransform());
  }
}