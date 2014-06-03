package es.molestudio.temazos.swipelistview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView mListView = (ListView) findViewById(R.id.myListView);
        ADPSwipeListView mADPSwipeListView = new ADPSwipeListView(MainActivity.this);
        mListView.setAdapter(mADPSwipeListView);
        
    }



}
