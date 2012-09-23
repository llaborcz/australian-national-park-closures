package com.bluebottlesoftware.nationalparkclosures;

import com.bluebottlesoftware.nswnpclosures.R;
import com.bluebottlesoftware.nswnpclosures.R.layout;
import com.bluebottlesoftware.nswnpclosures.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
