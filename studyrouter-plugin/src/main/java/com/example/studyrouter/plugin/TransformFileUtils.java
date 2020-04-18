package com.example.studyrouter.plugin;

import java.io.File;

/**
 * author: xujiajia
 * description:
 * 辅助类，用于遍历文件夹下的所有文件
 */
class TransformFileUtils {
  static void scanFileInDir(File rootFile, ScanFileCallback callback) {
    if (rootFile == null) {
      return;
    }
    if (rootFile.isDirectory()) {
      if (rootFile.listFiles() == null) {
        return;
      }
      for (File file : rootFile.listFiles()) {
        scanFileInDir(file, callback);
      }
    } else {
      callback.action(rootFile);
    }
  }

  public interface ScanFileCallback {
    void action(File file);
  }
}
