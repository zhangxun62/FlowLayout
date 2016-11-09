package com.alvin.flowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FlowLayout mFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlowLayout = (FlowLayout) findViewById(R.id.id_flowLayout);
        List<String> strings = new ArrayList<>();
        strings.add("Android");
        strings.add("Text");
        strings.add("Button");
        strings.add("Hello World!");
        strings.add("Java");
        strings.add("ListView");
        strings.add("RecyclerView");
        strings.add("Weclome");
        strings.add("Hi");


        mFlowLayout.setTextViewFormat(strings, R.color.colorAccent, R.drawable.shape_textview_background);
    }
}
