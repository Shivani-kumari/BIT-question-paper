package com.visualcelebrities.bitquestionspaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Upload extends AppCompatActivity {

    private static final int INTERNET_PERMISSION = 10;
    private static final int LOAD_IMAGE_INT = 1;
    String paperOf = "";

    Uri sourceUri;
    String sourceUriText;
    String extension = ".jpg";

    TextView upload_text_view;
    EditText upload_text;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        upload_text_view = findViewById(R.id.upload_text_view);
        upload_text = findViewById(R.id.upload_text);

        Intent intent = getIntent();
        paperOf =intent.getStringExtra("paperOf");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED){
            requestStoragePermission();
        }
    }

    public void choose_paper(View view) {
        upload_text_view.setText("Wait...");
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, LOAD_IMAGE_INT);
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Upload.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, INTERNET_PERMISSION);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, INTERNET_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOAD_IMAGE_INT && resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    // uploading to firebase
                    sourceUri = data.getData();
                    sourceUriText = sourceUri.toString();

                    upload_text_view.setText("Wait....");

                    String filePath ="";
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(sourceUri, filePathColumn, null, null, null);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                    }
                    cursor.close();

//                    Toast.makeText(Upload.this, "String test " + filePath.substring(filePath.lastIndexOf("/") + 1), Toast.LENGTH_LONG).show();
                    extension = "." + filePath.substring(filePath.lastIndexOf(".") + 1);
//                    extensionView.setText(extension + "");
                    upload_text_view.setText("Wait.....");
                    StorageReference storageRef = storage.getReference();
                    final Uri file = Uri.fromFile(new File(filePath));
                    final StorageReference riversRef = storageRef.child(paperOf+"/"+filePath.substring(filePath.lastIndexOf("/") + 1));
                    final String finalFilePath = filePath;
                    riversRef.putFile(file).

                    // Register observers to listen for when the download is done or if it fails
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
//                            Uri downloadUrl = taskSnapshot.getUploadSessionUri();
//                            ?alt=media&token=

                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String urlget = uri.toString();

                                    Map<String, Object> docData = new HashMap<>();
                                    docData.put("fileName",finalFilePath.substring(finalFilePath.lastIndexOf("/") + 1));
                                    docData.put("title",upload_text.getText().toString());
                                    docData.put("url",urlget);
                                    db.collection(paperOf).add(docData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
//                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                    upload_text_view.setText("Ok uploaded and added!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error adding document", e);
                                                }
                                            });

                                    Toast.makeText(Upload.this, "success "+ file.getLastPathSegment(), Toast.LENGTH_SHORT).show();

                                }
                            });

                            upload_text_view.setText("Ok uploaded!");

//                            final DocumentReference sfDocRef = db.collection("bac");

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    // ...
                                    Toast.makeText(Upload.this, "failed "+ finalFilePath, Toast.LENGTH_SHORT).show();

                                }
                            });
                    break;
            }
        }
//        else if(requestCode == LOAD_FRAME_INT && resultCode == RESULT_OK){
//
//            Toast.makeText(MainActivity.this,
//                    "Fetching the frame " + data.getStringExtra("description"), Toast.LENGTH_SHORT).show();
//
//            MyAsyncTask task = new MyAsyncTask(this);
//            task.execute(data.getStringExtra("description")+"");
//        }
    }

}
