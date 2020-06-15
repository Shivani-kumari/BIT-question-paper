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
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BCA extends AppCompatActivity {

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
        setContentView(R.layout.activity_b_c);

        layoutManager = new LinearLayoutManager(this);
        recyclerView =  findViewById(R.id.bca_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewFirebaseRead();

//        ImageView imageView = findViewById(R.id.imageView);

//        Picasso.get().load("https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg").into(imageView);
    }

    private void recyclerViewFirebaseRead() {
        db.collection("bca")
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

                            mAdapter = new ImageListAdapter(imageTitleList, imageUrlList, imageFileNameList,BCA.this);// here work
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d("msg", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }

    public void upload_bca(View view) {
        Intent intent = new Intent(this, Upload.class);
        intent.putExtra("paperOf","bca");
        startActivity(intent);
    }
}
