package com.example.suyash776.autobot;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;



public class ControllerType extends Activity {

    public static String CONTROLLER_TYPE = "controller_type";
    private ArrayAdapter<String> Controller_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_controller_type);

        // Set result CANCELLED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        Button cancelButton = (Button) findViewById(R.id.cancel_button);

        String[] c = new String[] { "Sensor Values", "Open Garage"};

        //Controller_list = new ArrayAdapter<String>(this, R.layout.simplerow);

        ListView controllerListView = (ListView) findViewById(R.id.controller_type);

        ArrayList<String> cList = new ArrayList<String>();
        cList.addAll(Arrays.asList(c));
        Controller_list = new ArrayAdapter<String>(this, R.layout.simplerow, cList);

        controllerListView.setAdapter(Controller_list);
        controllerListView.setOnItemClickListener(controllerlistListener);

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(98);
                finish();
            }
        });
    }

    private OnItemClickListener controllerlistListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            String decision = ((TextView) arg1).getText().toString();

            Intent intent = new Intent();
            intent.putExtra(CONTROLLER_TYPE, decision);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }


    };
}
