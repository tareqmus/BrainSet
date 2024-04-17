package com.brainset.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brainset.ocr.dao.Scans;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ScanAdapter extends ArrayAdapter<Scans> {

    private Context mContext;
    private int mResource;

    public ScanAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Scans> objects){
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);


        ImageView imageView = convertView.findViewById(R.id.imageScan);
        TextView textTitle = convertView.findViewById(R.id.textScanTitle);
        TextView textSub = convertView.findViewById(R.id.textScanSub);
        Button deleteButton = convertView.findViewById(R.id.delBut);


        Bitmap bitmap = BitmapFactory.decodeFile(getItem(position).getImage().getAbsolutePath());
        imageView.setImageBitmap(bitmap);

        textTitle.setText(getItem(position).name);
        textSub.setText(getItem(position).name);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FbData db = new FbData();
                Scans toRemove = getItem(position);
                Log.e("NAME", toRemove.name);
                Log.e("NAME", toRemove.imageLink);
                remove(toRemove);
                GlobalData.user.scans.remove(toRemove.name);
                StorageReference file = db.imageFilesRef.child(toRemove.imageLink + ".jpg");
                file.delete();
                db.addNewUser(GlobalData.user);
                db.setUserScans(GlobalData.user, GlobalData.user.scans);
            }
        });

        if(getItem(position).name.equals("empty")){
            deleteButton.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }


}
