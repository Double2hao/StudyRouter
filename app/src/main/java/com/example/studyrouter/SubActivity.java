package com.example.studyrouter;

import android.app.Activity;
import android.os.Bundle;

import com.example.studyrouter.annotation.StudyRouter;

import androidx.annotation.Nullable;

/**
 * author: xujiajia
 * description:
 */
@StudyRouter(key = "test/sub")
public class SubActivity extends Activity {
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sub);
  }
}
