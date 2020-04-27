package com.example.cashflow;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.andexert.library.RippleView;
import com.google.android.material.snackbar.Snackbar;

public class AddAct extends AppCompatActivity {
    EditText edit_jumlah, edit_keterangan;
    Button btn_save;
    RadioGroup radio_status;
    SqliteHelper sqliteHelper;

    String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        edit_jumlah = findViewById(R.id.edit_jumlah);
        edit_keterangan = findViewById(R.id.edit_keterangan);
        btn_save = findViewById(R.id.btn_save);
        radio_status = findViewById(R.id.radio_status);

        sqliteHelper = new SqliteHelper(this);
        status = "";

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
                Log.d(status, "onCheckedChanged: ");
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jumlah = edit_jumlah.getText().toString();
                String keterangan = edit_keterangan.getText().toString();

                if(status.equals("") || jumlah.equals("") || keterangan.equals("")){
                    Snackbar.make(v, "Data Kosong, Silakan Lengkapi Dahulu", Snackbar.LENGTH_LONG).show();
                }else {
                    SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                    database.execSQL(
                            "INSERT INTO transaksi (status, jumlah, keterangan) VALUES('"+status+"', '"+jumlah+"', '"+keterangan+"')"
                    );
                    Snackbar.make(v, "Data Berhasil Disimpan", Snackbar.LENGTH_LONG).show();
                    finish();
                }



            }
        });


        getSupportActionBar().setTitle("Add Transaction");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
