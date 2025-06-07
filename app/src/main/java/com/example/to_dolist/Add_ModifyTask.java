package com.example.to_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Add_ModifyTask extends AppCompatActivity {
    private EditText txtTitle;
    private EditText txtDescription;
    private DBManager dbManager;
    private String action, status;
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_modify_task);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        // set Toolbar as ActionBar
        setSupportActionBar(toolbar);
        // provide back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (dbManager == null) {
            dbManager = new DBManager(this);
            dbManager.open();
//            Log.d("debug10", "onCreate: ");
        }

        txtTitle = (EditText) findViewById(R.id.txtTitle2);
        txtDescription = (EditText) findViewById(R.id.txtDescription2);

        Intent intent = getIntent(); // get Intent from previous activity
        if (intent != null) {
            action = intent.getStringExtra("action");
            if ("add".equals(action)) {
                // display empty EditText for user input
                txtTitle.setText("");
                txtDescription.setText("");
                toolbar.setTitle("Add new task");
                status = intent.getStringExtra("status");
            } else if ("modify".equals(action)) {
                //  bring data from previous activity
                id = intent.getIntExtra("id", -1);
                String title = intent.getStringExtra("title");
                String description = intent.getStringExtra("description");
                String d = intent.getStringExtra("date");
                if (getSupportActionBar() != null) {
                    String dateString = null, timeString = null;
                    try {
                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = dateTimeFormat.parse(d);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dateString = dateFormat.format(date);

                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); // 删除 ss
                        timeString = timeFormat.format(date);

                        // 现在，dateString 和 timeString 分别包含日期和时间（不包含秒）
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    getSupportActionBar().setTitle("Task Created Date at: ");
                    getSupportActionBar().setSubtitle("Date: " + dateString + "; Time: " + timeString);
                }
                //fill EditText with data
                txtTitle.setText(title);
                txtDescription.setText(description);
            }
        }
    }

    private void insertOrUpdateDatabase() {
        if ("add".equals(action)) {
            String title = txtTitle.getText().toString();
            String description = txtDescription.getText().toString();
            String status = this.status;

            dbManager.insert(title, description, status);
        } else if ("modify".equals(action)) {

            String title = txtTitle.getText().toString();
            String description = txtDescription.getText().toString();
            dbManager.update(this.id, title, description);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            insertOrUpdateDatabase();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        insertOrUpdateDatabase();
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        // update when user leave hint
        insertOrUpdateDatabase();
        super.onUserLeaveHint();
    }
}