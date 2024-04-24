package com.brainset.ocr;

// Importing necessary Android and Java libraries
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

// Custom ArrayAdapter to display scan items
public class ScanAdapter extends ArrayAdapter<Scans> {

    // Context for inflating the layout and the resource ID of the layout
    private Context mContext;
    private int mResource;

    // Constructor initializing with context, resource layout ID, and data
    public ScanAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Scans> objects){
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    // Method to get custom view for each item in the list
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        // LayoutInflater to inflate the custom layout
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        // Binding UI elements from the layout to variables
        ImageView imageView = convertView.findViewById(R.id.imageScan);
        TextView textTitle = convertView.findViewById(R.id.textScanTitle);
        TextView textSub = convertView.findViewById(R.id.textScanSub);
        Button deleteButton = convertView.findViewById(R.id.delBut);

        // Loading image from file and setting it to the ImageView
        Bitmap bitmap = BitmapFactory.decodeFile(getItem(position).getImage().getAbsolutePath());
        imageView.setImageBitmap(bitmap);

        // Setting text from the Scans object to TextViews
        textTitle.setText(getItem(position).name);
        textSub.setText(getItem(position).name);

        // Setting up the delete button's onClickListener
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logging and performing delete operation
                FbData db = new FbData();
                Scans toRemove = getItem(position);
                Log.e("NAME", toRemove.name);
                Log.e("NAME", toRemove.imageLink);
                remove(toRemove); // Removing from the adapter
                GlobalData.user.scans.remove(toRemove.name); // Removing from global user scans list
                StorageReference file = db.imageFilesRef.child(toRemove.imageLink + ".jpg");
                file.delete(); // Deleting from Firebase storage
                db.addNewUser(GlobalData.user); // Updating user data
                db.setUserScans(GlobalData.user, GlobalData.user.scans); // Updating scans in database
            }
        });

        // Making the delete button invisible for placeholders or empty entries
        if(getItem(position).name.equals("empty")){
            deleteButton.setVisibility(View.INVISIBLE);
        }

        // Returning the view for the current row
        return convertView;
    }
}
