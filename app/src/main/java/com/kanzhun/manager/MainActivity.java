package com.kanzhun.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kanzhun.manager.util.ImageLoader2;
import com.kanzhun.manager.util.Type;

/**
 * 010-62649180
 *
 * 606103462093
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageConfig config = new ImageConfig.Builder(this)
                .setDefaultResourceId(R.mipmap.ic_launcher)
                .setErrorResourceId(R.mipmap.ic_launcher)
                .setThreadCount(3)
                .setQueueType(Type.FIFO)
                .build();
        ImageLoader2.get().init(config);

        findViewById(R.id.text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(intent);
            }
        });
    }
}