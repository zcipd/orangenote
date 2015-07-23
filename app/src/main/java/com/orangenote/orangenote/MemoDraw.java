package com.orangenote.orangenote;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoDraw extends ActionBarActivity implements ColorPickerDialog.OnColorChangedListener, View.OnClickListener {

    public static final boolean D = true;
    private static final String TAG = "PaintActivity";
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int BLUR_MENU_ID = Menu.FIRST + 2;
    private static final int ERASE_MENU_ID = Menu.FIRST + 3;
    private static final int BOLD_MENU_ID = Menu.FIRST + 4;
    private static final int PRIMARYCOLOR_MENU_ID = Menu.FIRST + 5;
    private static final int CLEAR_MENU_ID = Menu.FIRST + 6;

    private signView sV;
    private Button btn_save;

    public int bold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handwriting_making_activity);

        sV = (signView)findViewById(R.id.signView);
        btn_save = (Button)findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

    }



    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_save :
                if(sV != null)
                    saveView(sV);
            default:
        }
    }

    //파일 저장하는 소스
    private void saveView(View view) {
        String ex_storage = this.getExternalFilesDir(null).getAbsolutePath();

        Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.draw(c);
        FileOutputStream fos = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String name = "A" + dateFormat.format(date) + ".png";   //여기까지가 이름 변수.

        String string_path = ex_storage;


        File imgFile;
        try {
            imgFile = new File(string_path);
            fos = new FileOutputStream(name);
            if(fos != null) {
                if(!imgFile.exists()){
                    imgFile.mkdirs();
                }
                b.compress(Bitmap.CompressFormat.PNG,100,fos);
                fos.close();
            }
        }catch(Exception e) {
            if(D)
                Log.e(getClass().getSimpleName(), "Exception: " + e.toString());
            Toast.makeText(this, "파일 생성 오류" + name, Toast.LENGTH_LONG);
            //예외처리
        }

        Toast.makeText(this, "저장에 성공했습니다." + name, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0, COLOR_MENU_ID, 0, "색상").setShortcut('3', 'c');
        menu.add(0, BLUR_MENU_ID, 0, "흐림").setShortcut('5', 'z');
        menu.add(0, ERASE_MENU_ID, 0, "지우개").setShortcut('5', 'z');
        menu.add(0, BOLD_MENU_ID, 0, "굵기").setShortcut('6', 'z');
        menu.add(0, PRIMARYCOLOR_MENU_ID, 0, "기본색(검은색)").setShortcut('7', 'z');
        menu.add(0, CLEAR_MENU_ID, 0, "전부 지우기").setShortcut('8', 'z');

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        sV.mPaint.setXfermode(null);
        sV.mPaint.setAlpha(0xFF);


        switch(item.getItemId()) {
            case COLOR_MENU_ID :
                new ColorPickerDialog(this, this, sV.mPaint.getColor()).show();
                return true;
            case BLUR_MENU_ID :
                if(sV.mPaint.getMaskFilter() != sV.mBlur) {
                    sV.mPaint.setMaskFilter(sV.mBlur);
                }else {
                    sV.mPaint.setMaskFilter(null);
                }
                return true;
            case ERASE_MENU_ID :
                sV.mPaint.setStrokeWidth(50);
                sV.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                sV.mEraserMode = true;
                return true;
            case BOLD_MENU_ID :
                ShowDialog();
                return true;
            case PRIMARYCOLOR_MENU_ID :
                sV.mPaint.setColor(Color.BLACK);
                return true;
            case CLEAR_MENU_ID :
                sV.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                sV.invalidate();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void ShowDialog()
    {


        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.activity_dialog,
                (ViewGroup) findViewById(R.id.layout_dialog));
        final TextView item1 = (TextView)Viewlayout.findViewById(R.id.txtItem1);

        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("굵기 설정");
        popDialog.setView(Viewlayout);

        //시크바
        SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.seekBar1);
        seek1.setMax(50);
        item1.setText("굵기(px) : 0");
        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Do something here with new value
                sV.mPaint.setStrokeWidth(progress);
                item1.setText("굵기(px) : " + progress);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        // Button OK
        popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        popDialog.create();
        popDialog.show();
    }


    public void colorChanged(int color) {
        sV.colorChanged(color);
    }
}
