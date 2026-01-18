package com.example.laundryapp.ui.order;

import android.view.View;
import android.widget.AdapterView;

public class SimpleListener {

    public static AdapterView.OnItemSelectedListener onChange(Runnable r) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                r.run();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }
}
