package com.example.studyrouter.plugin;

import java.io.File;

/**
 * author: xujiajia
 * description:
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
