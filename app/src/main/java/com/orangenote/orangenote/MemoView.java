package com.orangenote.orangenote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoView extends ActionBarActivity {
    View captureView;
    EditText nJMemoViewTitle;
    EditText nJMemoViewBody;
    ImageView nJMemoViewImage;
    String MemoImageStr;
    DatabaseHelper DBHelper = new DatabaseHelper(this);
    String TABLE_TITLE = "MEMO";
    SQLiteDatabase database;
    int Get_Position;
    private static final int REQUEST_CODE = 1;
    String MemoWriteImagePath_Name_DB;
    String KEY_ROWID = "_id";
    String KEY_IMAGE = "KEY_IMAGE";
    String KEY_TITLE = "KEY_MEMO_TITLE";
    String KEY_BODY = "KEY_MEMO_BODY";
    int nRowID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        captureView = getWindow().getDecorView();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_view);

        Intent MemoViewIntent = getIntent();
        Get_Position = MemoViewIntent.getIntExtra("position", -1);

        database = DBHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT _id, KEY_IMAGE, KEY_MEMO_TITLE, KEY_MEMO_BODY FROM " + TABLE_TITLE, null);
        cursor.moveToPosition(Get_Position);
        nRowID = (int)cursor.getLong(cursor.getColumnIndex("_id"));


        nJMemoViewTitle = (EditText)findViewById(R.id.MemoViewTitle);
        nJMemoViewBody = (EditText)findViewById(R.id.MemoViewBody);
        nJMemoViewImage = (ImageView)findViewById(R.id.MemoViewImage);

        nJMemoViewTitle.setText(cursor.getString(2));
        nJMemoViewBody.setText(cursor.getString(3));

        MemoImageStr = cursor.getString(1);

        if(TextUtils.isEmpty(MemoImageStr))
            nJMemoViewImage.setImageResource(R.drawable.draw);
        else
        {
            Log.d("ImagePath", "이미지는 null 은 아닌가 보네요.");
            BitmapFactory.Options nBitmapF = new BitmapFactory.Options();
            nBitmapF.inSampleSize = 1;
            Bitmap nBitmap = BitmapFactory.decodeFile(MemoImageStr, nBitmapF);
            Bitmap nReSize = Bitmap.createScaledBitmap(nBitmap, ((int) convertDpToPixel(250, this)), ((int) convertDpToPixel(250, this)), true);
            nJMemoViewImage.setImageBitmap(nReSize);

            nJMemoViewTitle.setFocusableInTouchMode(false);
            nJMemoViewBody.setFocusableInTouchMode(false);
        }
    }

    public float convertDpToPixel(float dp, Activity context)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memo_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.MemoEdit) {
            nJMemoViewTitle.setFocusableInTouchMode(true);
            nJMemoViewBody.setFocusableInTouchMode(true);

            Toast.makeText(this, "지금 부터 메모 수정이 가능 합니다.", Toast.LENGTH_LONG).show();

            return true;
        }
        else if(id == R.id.MemoViewAlarm)
        {
            Intent Alarm_intent = new Intent(this, notification.class);
            startActivity(Alarm_intent);
        }
        else if(id == R.id.MemoViewImage)
        {
            Intent nInsertImageIntent = new Intent();
            nInsertImageIntent.setType("image/*");
            nInsertImageIntent.setAction(Intent.ACTION_GET_CONTENT);
            nInsertImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(nInsertImageIntent, REQUEST_CODE);
        }
        else if(id == R.id.MemoViewSave)
        {
            String KEY_IMAGE_Str;

            if(TextUtils.isEmpty(MemoWriteImagePath_Name_DB))
            {
                KEY_IMAGE_Str = MemoImageStr;
            }
            else
            {
                KEY_IMAGE_Str = MemoWriteImagePath_Name_DB;
            }

            String KEY_MEMO_TITLE_Str = nJMemoViewTitle.getText().toString();
            String KEY_MEMO_BODY_Str = nJMemoViewBody.getText().toString();
            ContentValues args = new ContentValues();
            args.put(KEY_IMAGE, KEY_IMAGE_Str);
            args.put(KEY_TITLE, KEY_MEMO_TITLE_Str);
            args.put(KEY_BODY, KEY_MEMO_BODY_Str);

            try
            {
                database.update(TABLE_TITLE, args, KEY_ROWID + "=" + nRowID , null);
                Toast.makeText(this, "메모 저장 성공", Toast.LENGTH_LONG).show();
                finish();
            }
            catch (Exception e)
            {
                Toast.makeText(this, "메모 저장 실패", Toast.LENGTH_LONG).show();
            }

            return true;
        }
        else if(id == R.id.MemoViewDelete)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("질문");
            alertDialogBuilder.setMessage("삭제 할까요?");
            alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    database.delete(TABLE_TITLE, KEY_ROWID + "=" + nRowID, null);
                    finish();
                }
            });
            alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
            return true;
        }
        else if(id == R.id.MemoShare)
        {
            Bitmap bitmap = takeScreenshot();

            File imagePath = new File(Environment.getExternalStorageDirectory() + "/screentshot.png");
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }catch (FileNotFoundException e) {
                Log.e("GREC", e.getMessage(), e);
            }catch (IOException e){
                Log.e("GREC", e.getMessage(),e);
            }


           // String KEY_IMAGE_Str = MemoImageStr;
           // String KEY_MEMO_TITLE_Str = nJMemoViewTitle.getText().toString();
           // String KEY_MEMO_BODY_Str = nJMemoViewBody.getText().toString();

            Intent nShareIntent = new Intent(Intent.ACTION_SEND);
            nShareIntent.setType("image/*");
            Uri imageUri = Uri.fromFile(imagePath);

            //nShareIntent.putExtra(Intent.EXTRA_TITLE, KEY_MEMO_TITLE_Str);
            nShareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            //nShareIntent.putExtra(Intent.EXTRA_TEXT, KEY_MEMO_BODY_Str);

            startActivity(Intent.createChooser(nShareIntent, "Share"));
        }

        return super.onOptionsItemSelected(item);
    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                SimpleDateFormat nDate = new SimpleDateFormat("yyyymmddhhmmss");
                String nDateFileName = nDate.format(new Date());
                String MemoWriteImageFileName = "A" + nDateFileName + ".png";
                File MemoWriteImage_Name_Final = new File(this.getExternalFilesDir(null), MemoWriteImageFileName);
                InputStream InStream = getContentResolver().openInputStream(data.getData());
                OutputStream OutStream = new FileOutputStream(MemoWriteImage_Name_Final);
                byte[] buf = new byte[1024];
                int len;

                while((len = InStream.read(buf)) > 0)
                    OutStream.write(buf, 0, len);

                OutStream.close();
                InStream.close();

                BitmapFactory.Options nBitmapF = new BitmapFactory.Options();
                nBitmapF.inSampleSize = 1;
                Bitmap nBitmap = BitmapFactory.decodeFile(MemoWriteImage_Name_Final.toString(), nBitmapF);
                Bitmap nReSize = Bitmap.createScaledBitmap(nBitmap, ((int)convertDpToPixel(250, this)), ((int)convertDpToPixel(250, this)), true);
                nJMemoViewImage.setImageBitmap(nReSize);

                MemoWriteImagePath_Name_DB = MemoWriteImage_Name_Final.toString();
            }
            catch (Exception e)
            {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
