package com.orangenote.orangenote;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class password extends ActionBarActivity {

    int passok;
    String main_password;

    DatabaseHelper DBHelper = new DatabaseHelper(this);
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);



        database = DBHelper.getWritableDatabase();
        final EditText password = (EditText)findViewById(R.id.passwordText);
        Button pass_ok = (Button)findViewById(R.id.password_ok);
        Button pass_cancle = (Button)findViewById(R.id.password_cancle);

        pass_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String PasswordStr_Str = password.getText().toString();
                int statPassword_int = 1;

                try {
                    SharedPreferences settings = getSharedPreferences("passok",0); //공유 프레퍼런스 만들기 passok
                    SharedPreferences.Editor editor = settings.edit();
                    passok = statPassword_int;
                    editor.putInt("passok",passok);
                    editor.commit();

                    SharedPreferences settings_password = getSharedPreferences("main_password", 0); //공유 프레퍼런스 만들기 password
                    SharedPreferences.Editor editor_password = settings_password.edit();
                    main_password = PasswordStr_Str;
                    editor_password.putString("main_password",main_password);
                    editor_password.commit();

                    finish();
                }
                catch (Exception e) {
                    Toast.makeText(password.this, "메모 저장 실패", Toast.LENGTH_LONG).show();
                }
            }
        });
        pass_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password, menu);
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
