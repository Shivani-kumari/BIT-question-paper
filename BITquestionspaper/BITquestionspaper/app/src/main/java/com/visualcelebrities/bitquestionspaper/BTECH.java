package com.visualcelebrities.bitquestionspaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BTECH extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public List<String> imageTitleList = new ArrayList<>();
    public List<String> imageUrlList = new ArrayList<>();
    public List<String> imageFileNameList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_t_e_c_h);

        layoutManager = new LinearLayoutManager(this);
        recyclerView =  findViewById(R.id.btech_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewFirebaseRead();
    }

    public void upload_btech(View view) {
        Intent intent = new Intent(this, Upload.class);
        intent.putExtra("paperOf","btech");
        startActivity(intent);
    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }

    private void recyclerViewFirebaseRead() {
        db.collection("btech")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String imageTitle = (String) document.getData().get("title");
                                String imageUrl = (String) document.getData().get("url");
                                String fileName = (String) document.getData().get("fileName");
                                imageTitleList.add(imageTitle);
                                imageUrlList.add(imageUrl);
                                imageFileNameList.add(fileName);
//                                Log.d("msg", document.getId() + " => " + document.getData());
                            }

                            mAdapter = new ImageListAdapter(imageTitleList, imageUrlList, imageFileNameList,BTECH.this);// here work
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d("msg", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
