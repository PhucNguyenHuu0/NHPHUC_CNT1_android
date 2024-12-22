package com.example.lab08;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    String dbName = "ContactDB.db";
    String dbPath = "/databases/";
    SQLiteDatabase db = null;
    ArrayAdapter<Contact> adapter;
    ListView lvContact;
    EditText edtMa, edtTen, edtDT;
    Button btnThem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xuLyCopy(); // Sao chép cơ sở dữ liệu từ assets
        addView();  // Khởi tạo các thành phần giao diện
        hienthiContact(); // Hiển thị danh bạ
        addEvent(); // Thêm sự kiện
    }

    private void addEvent() {
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int ma = Integer.parseInt(edtMa.getText().toString());
                    String ten = edtTen.getText().toString();
                    String dt = edtDT.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put("Ma", ma);
                    values.put("Ten", ten);
                    values.put("Dienthoai", dt);

                    long result = db.insert("Contact", null, values);
                    if (result > 0) {
                        Toast.makeText(MainActivity.this, "Thêm mới thành công", Toast.LENGTH_SHORT).show();
                        Contact ct = new Contact(ma, ten, dt);
                        adapter.add(ct);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, "Thêm mới thất bại", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Lỗi:", e.toString());
                }
            }
        });
    }

    private void hienthiContact() {
        db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM Contact", null);

        if (cursor.moveToFirst()) {
            do {
                int ma = cursor.getInt(0);
                String ten = cursor.getString(1);
                String dienthoai = cursor.getString(2);

                Contact contact = new Contact(ma, ten, dienthoai);
                adapter.add(contact);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "Không có dữ liệu để hiển thị!", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void addView() {
        lvContact = findViewById(R.id.lvContact);
        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1);
        lvContact.setAdapter(adapter);

        edtMa = findViewById(R.id.edtMa);
        edtTen = findViewById(R.id.edtTen);
        edtDT = findViewById(R.id.edtDT);
        btnThem = findViewById(R.id.btnThem);
    }

    private void xuLyCopy() {
        try {
            File dbFile = getDatabasePath(dbName);
            if (!dbFile.exists()) {
                copyDataFromAsset();
                Toast.makeText(MainActivity.this, "Copy thành công", Toast.LENGTH_LONG).show();
            } else {
                Log.d("Database", "File đã tồn tại trên app.");
            }
        } catch (Exception e) {
            Log.e("Lỗi", e.toString());
        }
    }

    private void copyDataFromAsset() {
        try {
            InputStream myInput = getAssets().open(dbName);
            String outFileName = getApplicationInfo().dataDir + dbPath + dbName;

            File dir = new File(getApplicationInfo().dataDir + dbPath);
            if (!dir.exists()) dir.mkdir();

            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception ex) {
            Log.e("Lỗi", ex.toString());
        }
    }
}
