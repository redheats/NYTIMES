package com.example.redheats.nytimes;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.loopj.android.http.RequestParams;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Settings extends AppCompatActivity {

    Calendar myCalendar;
    EditText date_pickup;
    Spinner order_filter;
    Button save_filter;
    CheckBox arts, fashion, sport;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Setting up date pickup
        myCalendar = Calendar.getInstance();
        date_pickup = findViewById(R.id.date_filter);
        date_pickup.setText(sharedPreferences.getString("date", myCalendar.getTime().toString()));
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                formatLabel(date_pickup);
            }

        };
        date_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Settings.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Setting up the spinner
        order_filter = findViewById(R.id.order_filter);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter
                .createFromResource(getApplicationContext(), R.array.filter_sort_order,
                        android.R.layout.simple_spinner_item);
        order_filter.setAdapter(arrayAdapter);
        order_filter.setSelection(sharedPreferences.getInt("order", 0));

        //Setting up the checkboxes
        arts = findViewById(R.id.check_arts);
        fashion = findViewById(R.id.check_fashion);
        sport = findViewById(R.id.check_sports);

        arts.setChecked(sharedPreferences.getBoolean("arts", false));
        fashion.setChecked(sharedPreferences.getBoolean("fashion", false));
        sport.setChecked(sharedPreferences.getBoolean("sports", false));

        //Setting up the save button
        save_filter = findViewById(R.id.save_filter);
        save_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date;
                String order;
                date = date_pickup.getText().toString().replace("/", "");
                if(order_filter.getSelectedItem().toString().equals("Newest")){
                    order = "newest";
                }
                else {
                    order = "oldest";
                }
                //((SearchActivity)getApplicationContext()).getParams().put("begin_date",date);
                //((SearchActivity)getApplicationContext()).getParams().put("sort",order);
                SearchActivity.params.put("begin_date",date);
                SearchActivity.params.put("sort",order);

                String fq = "";
                if (arts.isChecked()){
                    fq = fq + " \"arts\"";
                }
                else{
                    if (fq.contains(" \"arts\"")){
                        fq = fq.replace(" \"arts\"", "");
                    }
                }
                if (fashion.isChecked()){
                    fq = fq + " \"fashion & style\"";
                }
                else{
                    if (fq.contains(" \"fashion & style\"")){
                        fq = fq.replace(" \"fashion & style\"", "");
                    }
                }
                if (sport.isChecked()){
                    fq = fq + " \"sports\"";
                }
                else{
                    if (fq.contains(" \"sports\"")){
                        fq = fq.replace(" \"sports\"", "");
                    }
                }
                if(!TextUtils.isEmpty(fq)){
                    SearchActivity.params.put("fq", "news_desk:("+fq.trim()+")");
                }

                editor.putBoolean("sports", sport.isChecked());
                editor.putBoolean("fashion", fashion.isChecked());
                editor.putBoolean("arts", arts.isChecked());
                editor.putString("date", date_pickup.getText().toString());
                editor.putInt("order", order_filter.getSelectedItemPosition());
                editor.apply();


                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                finish();
            }
        });


    }

    private void formatLabel(EditText editText) {
        //String myFormat = "MM/dd/yyyy"; //In which you need put here
        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(myCalendar.getTime()));
    }
}
