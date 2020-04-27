package com.example.cashflow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Year;
import java.util.Calendar;

public class EditAct extends AppCompatActivity {
    HomeAct M = new HomeAct();
    EditText edit_jumlah, edit_keterangan, edit_tanggal;
    Button btn_save;
    RadioGroup radio_status;
    RadioButton radio_masuk, radio_keluar;
    SqliteHelper sqliteHelper;
    DatePickerDialog datePickerDialog;
    Cursor cursor;
    String status, tanggal, query_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        edit_jumlah = findViewById(R.id.edit_jumlah);
        edit_keterangan = findViewById(R.id.edit_keterangan);
        btn_save = findViewById(R.id.btn_save);
        radio_status = findViewById(R.id.radio_status);
        edit_tanggal = findViewById(R.id.edit_tanggal);
        radio_masuk = findViewById(R.id.radio_masuk);
        radio_keluar = findViewById(R.id.radio_keluar);


        sqliteHelper = new SqliteHelper(this);
        status = "";
        tanggal = "";

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        final SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery("SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi WHERE transaksi_id='"+M.nomer_id+"'", null);
        cursor.moveToFirst();

        status = cursor.getString(1);
        switch (status){
            case "Masuk": radio_masuk.setChecked(true);
            break;
            case "Keluar": radio_keluar.setChecked(true);
        }
        radio_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_masuk:
                        status = "Masuk";
                        break;
                    case R.id.radio_keluar:
                        status = "Keluar";
                        break;
                }
            }
        });

        edit_jumlah.setText(cursor.getString(2));
        edit_keterangan.setText(cursor.getString(3));
        tanggal = cursor.getString(4);
        edit_tanggal.setText(cursor.getString(5));



        edit_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(EditAct.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");
                        tanggal = numberFormat.format(year)+"-"+numberFormat.format((month + 1))+"-"+ numberFormat.format(dayOfMonth);

                        edit_tanggal.setText(numberFormat.format(dayOfMonth)+"/"+numberFormat.format((month + 1))+
                                "/"+numberFormat.format(year));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("") || edit_jumlah.equals("") || edit_keterangan.equals("") || tanggal.equals("")){
                    Toast.makeText(getApplicationContext(), "Tolong lengkapi semua data", Toast.LENGTH_SHORT).show();
                }else {
                    SQLiteDatabase database1 = sqliteHelper.getWritableDatabase();
                    database1.execSQL(
                            "UPDATE transaksi SET status='" + status + "', jumlah='" + edit_jumlah.getText().toString() + "', keterangan='"+edit_keterangan.getText().toString()+"', tanggal='"+tanggal+"' WHERE transaksi_id='"+M.nomer_id+"'"
                    );
                    Toast.makeText(getApplicationContext(), "Data Berhasil di ubah", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
