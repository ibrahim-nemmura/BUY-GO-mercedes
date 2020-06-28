package com.example.buygo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import model.Book;
import viewmodel.AddActivityViewModel;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.ExecutionException;

public class EbookActivity extends AppCompatActivity implements NameAndPic.NameFragmentListener, CategoryFragment.CategotyFragmentListener, CommentFragment.CommentFragmentListener {


    private static LinearLayout background;

    public static final int Pic_0=0;
    public static final int Pic_1=1;
    public static final int Pic_2=2;
    private Book book;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private PhotoUploader photoUploader;
    protected StorageReference file;
    protected String picUrl;
    protected UploadTask uploadTask;
    private AddActivityViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook);
        photoUploader=new PhotoUploader();
        background=findViewById(R.id.sellBackground);
        progressBar=findViewById(R.id.progressbar);
        background.setBackgroundResource(R.drawable.pic1);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mViewModel = ViewModelProviders.of(this).get(AddActivityViewModel.class);
        NameAndPic fragment = new NameAndPic();
        fragmentTransaction.add(R.id.fragment_layout, fragment);
        fragmentTransaction.commit();
        book=new Book();
        db=FirebaseFirestore.getInstance();
    }

    @Override
    public void saveCategoryButtonPressed(String branch, String year, String subject) {
        book.setBranch(branch);
        book.setCourseYear(year);
        book.setSubject(subject);
        background.setBackgroundResource(R.drawable.pic3);
    }

    @Override
    public void savedButtonPressed(String com1, String com2, String com3, int price) {
        book.setComment(com1);
        book.setPrice(price);
        try {
            //book.setPhoto(photoUploader.get());
            book.setPhoto(picUrl);
            Log.d("EbookActivity","get(): "+photoUploader.get());
        }catch (ExecutionException | InterruptedException e1)
        {
            Log.d("EbookActivity",""+e1);
        }
        upload();
    }

    @Override
    public void OnNameNextButtonPressed(String name, Uri uri) {
        book.setBookName(name);
        background.setBackgroundResource(R.drawable.pic2);
        photoUploader.execute(uri);

    }

    @Override
    public void OnNameCancelButtonPressed() {
        if(file!=null)
        {
            file.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
               Log.d("EbookActivity","Deleted Successfull");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("EbookActivity","Deleted failed");
                }
            });
        }
        finish();
    }

    private void upload()
    {
        db.collection("products").add(book).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(),"Item Added!",Toast.LENGTH_SHORT);
                startActivity(new Intent(EbookActivity.this,SellActivity.class));
            }
        });
    }

    public class PhotoUploader extends AsyncTask<Uri, Integer, String> {

        private StorageReference mStrorageStorageReference;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mStrorageStorageReference = FirebaseStorage.getInstance().getReference();
        }

        @Override
        protected String doInBackground(Uri... uris) {

            if (uris[0] != null) {
                final StorageReference filepath = mStrorageStorageReference.child("images").child("my_image_" + Timestamp.now().getSeconds());
                uploadTask= filepath.putFile(uris[0]);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            picUrl = uri.toString();
                            Log.d("EbookActivity", picUrl);
                            file = filepath;
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO inform user that upload has failed
                }
            });
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    publishProgress((int) progress);
                }
            });
        }
            return picUrl;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

    }

}
