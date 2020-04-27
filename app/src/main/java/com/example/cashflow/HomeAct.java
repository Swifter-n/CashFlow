package com.example.cashflow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class HomeAct extends AppCompatActivity {

    String query_cash, queryTotal;
    SqliteHelper sqliteHelper;
    Cursor cursor;
    ListView list_cash;
    TextView pemasukan, pekeluaran, total;
    ArrayList<HashMap<String, String>> flowCash = new ArrayList<>();
    SwipeRefreshLayout swipe_id;

    public static TextView text_filter;
    public static boolean filter;
    public static String nomer_id, edit_dari, edit_sampai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        list_cash = findViewById(R.id.list_cash);
        pemasukan = findViewById(R.id.pemasukan);
        pekeluaran = findViewById(R.id.pekeluaran);
        total = findViewById(R.id.total);
        swipe_id = findViewById(R.id.swipe_id);
        text_filter = findViewById(R.id.text_filter);

        sqliteHelper = new SqliteHelper(this);
        edit_dari = "";
        edit_sampai = "";
        filter = false;



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoAdd = new Intent(HomeAct.this, AddAct.class);
                startActivity(gotoAdd);

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        swipe_id.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query_cash = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC";
                queryTotal = " SELECT SUM(jumlah) AS total, (SELECT SUM(jumlah) FROM transaksi WHERE status = 'Masuk') AS masuk," +
                        "(SELECT SUM(jumlah) FROM transaksi WHERE status = 'Keluar') AS keluar FROM transaksi";
                CashAdapter();
            }
        });
    }
    @Override
    public  void onResume(){
        super.onResume();
        query_cash = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC";
        queryTotal = " SELECT SUM(jumlah) AS total, (SELECT SUM(jumlah) FROM transaksi WHERE status = 'Masuk') AS masuk," +
                "(SELECT SUM(jumlah) FROM transaksi WHERE status = 'Keluar') AS keluar FROM transaksi";


        if(filter){
            query_cash = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi "+
                    "WHERE (tanggal >= '"+edit_dari+"') AND (tanggal <= '"+edit_sampai+"') ORDER BY transaksi_id DESC";
            queryTotal = " SELECT SUM(jumlah) AS total,"+
                    "(SELECT SUM(jumlah) FROM transaksi WHERE status = 'Masuk' AND (tanggal >= '"+edit_dari+"') AND (tanggal <= '"+edit_sampai+"' ))," +
                    "(SELECT SUM(jumlah) FROM transaksi WHERE status = 'Keluar' AND (tanggal >= '"+edit_dari+"') AND (tanggal <= '"+edit_sampai+"')) " +
                    "FROM transaksi WHERE (tanggal >= '"+edit_dari+"') AND (tanggal <= '"+edit_sampai+"')";
        }

        CashAdapter();
    }

    public void CashAdapter(){
        swipe_id.setRefreshing(false);
        flowCash.clear();
        list_cash.setAdapter(null);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(query_cash, null);
        cursor.moveToFirst();
        for(int i = 0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            //Log.d("status", cursor.getString(1));
            HashMap<String, String> map = new HashMap<>();
            map.put("transaksi_id", cursor.getString(0));
            map.put("status", cursor.getString(1));
            map.put("jumlah", cursor.getString(2));
            map.put("keterangan", cursor.getString(3));
            map.put("tanggal", cursor.getString(5));

            flowCash.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, flowCash, R.layout.item_cash, new String[] {"transaksi_id", "status", "jumlah", "keterangan", "tanggal"},
        new int[] {R.id.xnomor, R.id.xstatus, R.id.xjumlah, R.id.xketerangan, R.id.xdate});
        list_cash.setAdapter(simpleAdapter);
        list_cash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView xnomor = view.findViewById(R.id.xnomor);
                nomer_id = xnomor.getText().toString();


                final Dialog dialog = new Dialog(HomeAct.this);
                dialog.setContentView(R.layout.item_menu);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();

              TextView btn_edit = dialog.findViewById(R.id.btn_edit);
              TextView btn_delete = dialog.findViewById(R.id.btn_delete);

              btn_edit.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      dialog.dismiss();
                      Intent gotoEdit = new Intent(HomeAct.this, EditAct.class);
                      startActivity(gotoEdit);
                  }
              });

              btn_delete.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      dialog.dismiss();
                      final AlertDialog.Builder builder = new AlertDialog.Builder(HomeAct.this);
                      builder.setTitle("Confirm");
                      builder.setMessage("Are you sure?");
                      builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              SQLiteDatabase database1 = sqliteHelper.getWritableDatabase();
                              database1.execSQL("DELETE FROM transaksi WHERE transaksi_id='"+ nomer_id +"'");
                              Toast.makeText(getApplicationContext(), "Data Berhasil dihapus", Toast.LENGTH_SHORT).show();
                              CashAdapter();
                          }
                      });
                      builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              dialog.dismiss();
                          }
                      });
                      builder.show();

                  }
              });


            }
        });
        totalCash();
    }

    public void totalCash(){
        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(queryTotal, null);
        cursor.moveToFirst();

        NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);
        pemasukan.setText(rupiah.format(cursor.getDouble(1)));
        pekeluaran.setText(rupiah.format(cursor.getDouble(2)));
        total.setText(rupiah.format(cursor.getDouble(1) - cursor.getDouble(2)));


        if(!filter){text_filter.setText("ALL RECENT");}
        filter = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_filter){
            startActivity(new Intent(HomeAct.this, FilterAct.class));
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
