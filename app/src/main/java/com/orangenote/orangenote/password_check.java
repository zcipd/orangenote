package com.orangenote.orangenote;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by min on 15. 7. 17.
 */
public class password_check extends ActionBarActivity {


    String main_password;

    String db_password_check;
    EditText password_check;
    String password_delete;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_main);

        SharedPreferences settings_password = getSharedPreferences("main_password",0);
        main_password = settings_password.getString("main_password", ""); //공유 프레퍼런스에서 apssword 가져오기

        SharedPreferences password_delete_settings = getSharedPreferences("password_change", 0);
        password_delete = password_delete_settings.getString("password_change", "");
        // Log.d("Passcheck", db_password_check);


        password_check = (EditText)findViewById(R.id.password_check);
        Button check = (Button)findViewById(R.id.password_check_btn);


        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_delete.toString().equals("3")) {
                    if (!password_check.getText().toString().equals(main_password)) {

                    }else{
                        int Passwordok = 0;
                        SharedPreferences settings = getSharedPreferences("passok", 0); //공유 프레퍼런스 passok 를 0으로
                        SharedPreferences.Editor editor = settings.edit();
                        int passok = Passwordok;
                        editor.putInt("passok", passok);
                        editor.commit();

                        SharedPreferences settings_password = getSharedPreferences("password_change", 0); //공유 프레퍼런스 만들기 password 다음에올때 경로가바뀌도록
                        SharedPreferences.Editor editor_password = settings_password.edit();
                        String password_change = Integer.toString(1);
                        editor_password.putString("password_change", password_change);
                        editor_password.commit();
                        finish();
                    }

                } else {
                    if (!password_check.getText().toString().equals(main_password)) {


                    } else {

                        finish();
                    }

                }
            }
        });






    }

}
