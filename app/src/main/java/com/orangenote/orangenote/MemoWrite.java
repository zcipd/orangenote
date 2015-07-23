package com.orangenote.orangenote;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoWrite extends ActionBarActivity {

    DatabaseHelper DBHelper = new DatabaseHelper(this);
    String DB_Name = "";
    EditText nJMemoTitle;
    EditText nJMemoBody;
    private static final int REQUEST_CODE = 1;
    ImageView nJMemoImage;
    String MemoWriteImagePath_Name_DB = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_write);
        nJMemoTitle = (EditText)findViewById(R.id.MemoWriteTitle);
        nJMemoBody = (EditText)findViewById(R.id.MemoWriteBody);
        nJMemoImage = (ImageView)findViewById(R.id.MemoWriteImage);

        Intent CameraGetIntent = getIntent();
        MemoWriteImagePath_Name_DB = CameraGetIntent.getStringExtra("nCameraImageFile");

        if(!TextUtils.isEmpty(MemoWriteImagePath_Name_DB))
        {
            File file = new File(MemoWriteImagePath_Name_DB);

            if(file.isFile())
            {
                BitmapFactory.Options nBitmapF = new BitmapFactory.Options();
                nBitmapF.inSampleSize = 1;
                Bitmap nBitmap = BitmapFactory.decodeFile(MemoWriteImagePath_Name_DB, nBitmapF);
                Bitmap nReSize = Bitmap.createScaledBitmap(nBitmap, convertDpToPixel(250, this), convertDpToPixel(250, this), true);
                nJMemoImage.setImageBitmap(nReSize);
            }
            else
            {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memo_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.MemoDelete) {
            finish();
            return true;
        }
        else if(id == R.id.MemoSave)
        {
            String TABLE_TITLE = "MEMO";
            String KEY_IMAGE_Str = MemoWriteImagePath_Name_DB;
            String KEY_MEMO_TITLE_Str = nJMemoTitle.getText().toString();
            String KEY_MEMO_BODY_Str = nJMemoBody.getText().toString();

            if(KEY_IMAGE_Str == null)
            {
                KEY_IMAGE_Str = "drawable://" + R.drawable.draw;
                nJMemoImage.setImageResource(R.drawable.draw);
            }

            try
            {
                DBHelper.insert("INSERT INTO " + TABLE_TITLE + "(KEY_IMAGE, KEY_MEMO_TITLE, KEY_MEMO_BODY) VALUES " + "('"
                        + KEY_IMAGE_Str + "', '"
                        + KEY_MEMO_TITLE_Str + "', '"
                        + KEY_MEMO_BODY_Str + "')");
                Toast.makeText(this, "메모 저장 성공", Toast.LENGTH_LONG).show();
                finish();
            }
            catch (Exception e)
            {
                Toast.makeText(this, "메모 저장 실패", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if(id == R.id.MemoImage)
        {
            Intent nInsertImageIntent = new Intent();
            nInsertImageIntent.setType("image/*");
            nInsertImageIntent.setAction(Intent.ACTION_GET_CONTENT);
            nInsertImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(nInsertImageIntent, REQUEST_CODE);
            return true;
        }

        else if(id == R.id.MemoAlarm)
        {
            Intent Alarm_intent = new Intent(MemoWrite.this, notification.class);
            startActivity(Alarm_intent);
            //Notification notification = new Notification();

            return true;
        }


        return super.onOptionsItemSelected(item);
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
                nJMemoImage.setImageBitmap(nReSize);

                MemoWriteImagePath_Name_DB = MemoWriteImage_Name_Final.toString();
            }
            catch (Exception e)
            {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public int convertDpToPixel(float dp, Activity context)
    {
        int intpx;

        if (dp != 0.0)
        {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float px = dp * (metrics.densityDpi / 160f);
            intpx = (int)px;
        }
        else
        {
            intpx = 0;
        }

        return intpx;
    }
}