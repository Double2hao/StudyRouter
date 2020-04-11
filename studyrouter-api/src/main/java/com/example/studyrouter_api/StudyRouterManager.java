package com.example.studyrouter_api;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * author: xujiajia
 * description:
 */
public class StudyRouterManager {
  private static final String TAG = "StudyRouterManager";

  //data
  private static HashMap<String, String> map = new HashMap<>();

  public static void initAutoRegister() {
    Log.d(TAG, "initAutoRegister");
    //这里会通过plugin动态生成代码，如：
    //autoWisedClassInit("com.example.generated.SubActivity_Router_AutoWised");
  }

  private static void autoWisedClassInit(String className) {
    Log.d(TAG, "autoWisedClassInit className:" + className);
    try {
      Class clazz = Class.forName(className);
      Method methodInit = clazz.getMethod("init", Map.class);
      methodInit.invoke(null, map);
    } catch (Exception e) {
      Log.d(TAG, "autoWisedClassInit error:" + e.getMessage());
    }
  }

  public static void register(String key, String className) {
    if (TextUtils.isEmpty(key) || TextUtils.isEmpty(className)) {
      return;
    }
    map.put(key, className);
  }

  public static void gotoPage(Activity activity, String key) {
    Log.d(TAG, "gotoPage map:" + map.toString());
    if (activity == null || TextUtils.isEmpty(key) || TextUtils.isEmpty(map.get(key))) {
      return;
    }
    try {
      Class mClass = Class.forName(map.get(key));
      Intent intent = new Intent(activity, mClass);
      activity.startActivity(intent);
    } catch (Exception e) {
      Log.d(TAG, "gotoPage error:" + e.getMessage());
    }
  }
}
