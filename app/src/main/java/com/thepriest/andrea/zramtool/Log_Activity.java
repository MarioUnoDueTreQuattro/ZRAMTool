package com.thepriest.andrea.zramtool;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Log_Activity extends ActionBarActivity {
    Button buttonClear,buttonUpdate;
    TextView editTextLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_);
        editTextLog = (TextView) findViewById(R.id.editTextLog);
        editTextLog.setKeyListener(null);
        buttonClear = (Button) findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clearLog();
            }
        });
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateLog();
            }
        });
        editTextLog.setText(ZRAMToolApp.sLogText);
    }

    private void updateLog() {
        editTextLog.setText( ZRAMToolApp.sLogText);
    }

    private void clearLog() {
        editTextLog.setText("");
        ZRAMToolApp.sLogText="";
    }

   public void append(String sText)
   {
       editTextLog.append(sText);
   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
