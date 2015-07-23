package com.orangenote.orangenote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";
    private static final int CAMERA_CAPTURE = 0;
    String nJClass3FileName;
    DatabaseHelper DBHelper;
    SQLiteDatabase database;
    String TABLE_TITLE = "MEMO";
    RecyclerView recyclerView;
    int ViewChage = 1;
    ItemData ITEMDATA[];
    private DrawerLayout mDrawerLayout;
    private FTPConnect nFTP = null;
    private String ftphost = "mercurys.iptime.org";
    private String ftpusername = "memo3";
    private String ftppassword = "memo";
    int passok;
    int Password_deletecheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 네비게이션 드로우
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        //패스워드 부분
        SharedPreferences setting = getSharedPreferences("passok", 0);
        passok = setting.getInt("passok", 0);

        SharedPreferences settings_notification = getSharedPreferences("notification_position", 0); //공유 프레퍼런스 만들기 password
        SharedPreferences.Editor editor_password = settings_notification.edit();
        String notifi = Integer.toString(0);
        editor_password.putString("notification_position", notifi);
        editor_password.commit();

        SharedPreferences Alarm_Delete = getSharedPreferences("Alarm_Delete", 0);
        SharedPreferences.Editor editor = Alarm_Delete.edit();
        int Alarm_DeleteInt = 0;
        editor.putInt("Alarm_Delete", Alarm_DeleteInt);
        editor.commit();

        try {
            if (passok == 1) {

                startActivity(new Intent(MainActivity.this, password_check.class));
            }
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "인텐트안됨 ", Toast.LENGTH_SHORT).show();
        }

        // + 버튼 부분
        final FloatingActionButton fabsub_1 = (FloatingActionButton) findViewById(R.id.fabsub_1);
        final FloatingActionButton fabsub_2 = (FloatingActionButton) findViewById(R.id.fabsub_2);
        final FloatingActionButton fabsub_3 = (FloatingActionButton) findViewById(R.id.fabsub_3);

        fabsub_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MemoWrite.class));
            }
        });

        fabsub_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MemoDraw.class));
            }
        });

        fabsub_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat nDate = new SimpleDateFormat("yyyymmddhhmmss");
                String nDateFileName = nDate.format(new Date());
                String nCameraFileName = "A" + nDateFileName + ".png";

                String nCameraFilePathName = getExternalFilesDir(null) + File.separator + nCameraFileName;
                Intent nIntentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                nIntentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(nCameraFilePathName)));
                nJClass3FileName = nCameraFilePathName;
                startActivityForResult(nIntentCamera, CAMERA_CAPTURE);
            }
        });

        DBHelper = new DatabaseHelper(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent nIntentview = new Intent(MainActivity.this, MemoWrite.class);
        nIntentview.putExtra("nCameraImageFile", nJClass3FileName);
        startActivity(nIntentview);
    }

    public void onResume() {
        super.onResume();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        final boolean bUseLikeListView = false;
        if (bUseLikeListView) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
        } else {
            StaggeredGridLayoutManager P_Stagger = new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(P_Stagger);

        }

        try {
            database = DBHelper.getWritableDatabase();

            Cursor cursor = database.rawQuery("SELECT _id, KEY_IMAGE, KEY_MEMO_TITLE, KEY_MEMO_BODY FROM " + TABLE_TITLE, null);

            int count = cursor.getCount();

            ITEMDATA = new ItemData[count];

            for (int i = 0; i < count; i++) {
                cursor.moveToNext();
                ITEMDATA[i] = new ItemData(cursor.getString(1), cursor.getString(2), cursor.getString(3));
            }

            MyAdapter mAdapter = new MyAdapter(ITEMDATA);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } catch (Exception e) {
            Toast.makeText(this, "DB가 없네요", Toast.LENGTH_LONG).show();
        }
    }

    public String nDate() {
        SimpleDateFormat nDate = new SimpleDateFormat("yyyymmddhhmmss");
        String nDateFileName = nDate.format(new Date());
        String nCameraFileName = "A" + nDateFileName + ".png";

        String nCameraFilePathName = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Camera" + File.separator + nCameraFileName;

        return nCameraFilePathName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int nItemsu = 2;

        //noinspection SimplifiableIfStatement
        if (id == R.id.ViewChange) {
            MyAdapter mAdapter = new MyAdapter(ITEMDATA);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            if (ViewChage == 0) {
                nItemsu = 2;
                ViewChage = 1;
            } else {
                nItemsu = 1;
                ViewChage = 0;
            }
            StaggeredGridLayoutManager P_Stagger = new StaggeredGridLayoutManager(nItemsu, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(P_Stagger);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        SharedPreferences setting = getSharedPreferences("passok", 0);
        passok = setting.getInt("passok", 0);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_password:
                                if (passok == 0) {
                                    Intent intent = new Intent(MainActivity.this, password.class);
                                    startActivity(intent);
                                } else
                                    Toast.makeText(getApplicationContext(), "비밀번호가 이미 설정 되어 있습니다.", Toast.LENGTH_SHORT).show();

                                return true;
                            case R.id.nav_passwordRelease:
                                if (passok == 1) {
                                    Password_deletecheck = 1;
                                    Intent Delete_Password = new Intent(MainActivity.this, password_check.class);
                                    startActivity(Delete_Password);

                                    SharedPreferences settings_password = getSharedPreferences("password_change", 0); //공유 프레퍼런스 만들기 password check 으로가서 비밀번호 비교전에 경로를 바꾼다.
                                    SharedPreferences.Editor editor_password = settings_password.edit();
                                    String password_change = Integer.toString(3);
                                    editor_password.putString("password_change", password_change);
                                    editor_password.commit();
                                } else
                                    Toast.makeText(getApplicationContext(), "비밀번호 먼저 설정해 주세요.", Toast.LENGTH_SHORT).show();

                                return true;
                            case R.id.nav_backup:
                                Toast.makeText(getApplicationContext(), "백업 하기", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.nav_restore:
                                nFTP = new FTPConnect();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean status = true;
                                        status = nFTP.ftpConnect(ftphost, ftpusername, ftppassword, 21);
                                        if (status == true) {
                                            Log.d("FTPUpdload", "FTP 접속에 성공 했다네요.");
                                        } else {
                                            Log.d("FTPUpdload", "FTP 접속에 실패 했다네요.");
                                        }

                                        try {
                                            FTPFile[] ftpFiles = nFTP.mFTPClient.listFiles("FTP");

                                            if (ftpFiles != null && ftpFiles.length > 0) {
                                                for (FTPFile file : ftpFiles) {
                                                    if (!file.isFile()) {
                                                        continue;
                                                    }
                                                    OutputStream output;
                                                    output = new BufferedOutputStream(new FileOutputStream(getApplicationContext().getExternalFilesDir(null) + File.separator + file.getName()));
                                                    nFTP.mFTPClient.retrieveFile("FTP/"+file.getName(), output);
                                                    output.close();
                                                }
                                            }
                                        } catch (Exception e) {
                                        }

                                        String FTPServerFlile = "/FTP/meme.db";
                                        String DBFilepathName = Environment.getDataDirectory() + "//data//" + getPackageName() + "//databases//" + "memo.db";
                                        try {
                                            FileOutputStream nDownLoadFile = new FileOutputStream(DBFilepathName);
                                            status = nFTP.mFTPClient.retrieveFile("FTP/memo.db", nDownLoadFile);
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        if (status == true)
                                            Log.d("Filedown", "다운로드가 성공이 랍니다.");
                                        else
                                            Log.d("Filedown", "다운로드가 실패 랍니다.");
                                    }
                                }).start();

                                Toast.makeText(getApplicationContext(), "복구가 완료 되었습니다. 어플을 종료 후 다시 실행 해 주세요.", Toast.LENGTH_LONG).show();

                                return true;
                        }
                        return true;
                    }
                });
    }
}
