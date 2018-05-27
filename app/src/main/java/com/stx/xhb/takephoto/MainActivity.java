package com.stx.xhb.takephoto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.stx.xhb.library.ChooseImageDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        ChooseImageDialog chooseImageDialog = ChooseImageDialog.newInstance();
        chooseImageDialog.setOperator(new ChooseImageDialog.Operator() {
            @Override
            public void onGetImage(String path) {
                Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
            }
        });
        chooseImageDialog.show(getSupportFragmentManager(), null);
    }
}
