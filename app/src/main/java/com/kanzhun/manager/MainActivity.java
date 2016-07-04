package com.kanzhun.manager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.kanzhun.manager.util.ImageLoader;

/**
 * 010-62649180
 * <p/>
 * 606103462093
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageConfig config = new ImageConfig.Builder()
                .setDefaultResourceId(R.mipmap.bg_default)
                .setErrorResourceId(R.mipmap.bg_default)
                .setThreadCount(3)
                .setImageConfig(Bitmap.Config.ARGB_8888)
                .build();
        ImageLoader.get().init(config);

        findViewById(R.id.text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(intent);
            }
        });
    }
}
