package com.example.studyrouter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.studyrouter.annotation.StudyRouter;
import com.example.studyrouter_api.StudyRouterManager;

@StudyRouter(key = "test/main")
public class MainActivity extends Activity {

  //ui
  private Button btnInit;
  private Button btnGotoPage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initViews();
  }

  private void initViews() {
    btnInit = findViewById(R.id.btn_init_main);
    btnGotoPage = findViewById(R.id.btn_goto_page_main);

    btnInit.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        StudyRouterManager.initAutoRegister();
        Toast.makeText(MainActivity.this, R.string.main_toast_init, Toast.LENGTH_LONG).show();
      }
    });

    btnGotoPage.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        StudyRouterManager.gotoPage(MainActivity.this, "test/sub");
        Toast.makeText(MainActivity.this, R.string.main_toast_goto_page, Toast.LENGTH_LONG).show();
      }
    });
  }
}
