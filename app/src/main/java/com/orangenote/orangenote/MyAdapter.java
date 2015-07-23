package com.orangenote.orangenote;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by on 2015-07-05.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    static int P_count;
    Context context;
    private ItemData[] itemsData;
    DatabaseHelper DBHelper;
    String[] image;
    String[] title;
    String[] body;
    AdapterView.OnItemClickListener mItemClickListener;

    public MyAdapter(ItemData[] itemsData) {
        this.itemsData = itemsData;
    }

    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        this.context = parent.getContext();

        DBHelper = new DatabaseHelper(context);


        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int position){


        /*
        BitmapFactory.Options nBitmapF = new BitmapFactory.Options();
        nBitmapF.inSampleSize = 3;
        Bitmap nBitmap = BitmapFactory.decodeFile(itemsData[position].getImage(), nBitmapF);
        //Bitmap nReSize = Bitmap.createScaledBitmap(nBitmap, 72, 72, true);
        viewHolder.imgViewIcon.setImageBitmap(nBitmap);
        */
        viewHolder.txtViewTitle.setText(itemsData[position].getTitle());
        viewHolder.txtViewBody.setText(itemsData[position].getBody());

        Uri ImageUri = Uri.parse("file:///"+itemsData[position].getImage());
        Picasso.with(context).load(ImageUri).resize(500,500).centerCrop().into(viewHolder.imgViewIcon);
    }

    public int getItemCount()
    {
        return itemsData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imgViewIcon;
        public TextView txtViewTitle;
        public TextView txtViewBody;

        public ViewHolder(final View itemLayoutView){
            super(itemLayoutView);

            itemLayoutView.setOnClickListener(this);

            imgViewIcon = (ImageView)itemLayoutView.findViewById(R.id.item_icon);
            txtViewTitle = (TextView)itemLayoutView.findViewById(R.id.item_title);
            txtViewBody = (TextView)itemLayoutView.findViewById(R.id.item_body);
        }

        @Override
        public void onClick(View v) {
            Intent goMemoView = new Intent(context, MemoView.class);
            goMemoView.putExtra("position", getPosition());
            Log.d("PutPosition", Integer.toString(getPosition()));
            /*
            goMemoView.putExtra("MemoImage", itemsData[getPosition()].getImage());
            goMemoView.putExtra("MemoTitle", itemsData[getPosition()].getTitle());
            goMemoView.putExtra("MemoBody", itemsData[getPosition()].getBody());
            */
            context.startActivity(goMemoView);
        }
    }
}


