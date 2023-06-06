package edu.poly.alarmapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private EditText editText;
    private Button buttonSave, buttonCancel;

    private Alarm alarm;
    private Boolean needRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        timePicker = findViewById(R.id.timePicker);
        editText = findViewById(R.id.name);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                String name = editText.getText().toString();

                DatabaseHelp db = new DatabaseHelp(getApplicationContext());

                alarm = new Alarm(hour, minute, true, name);
                db.addAlarm(alarm);

                needRefresh = true;

                onBackPressed();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("needRefresh", needRefresh);
        this.setResult(RESULT_OK, data);
        super.finish();
    }
}