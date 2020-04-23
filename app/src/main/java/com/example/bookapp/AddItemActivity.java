package com.example.bookapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import model.Book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class AddItemActivity extends AppCompatActivity {

    private TextInputEditText  bookNameText;
    private ImageView bookImageView;
    private Spinner mBranchSpinner;
    private Spinner mYearSpinner;
    private Spinner mSubjectSpinner;
    private EditText mPublisherText;
    private EditText mComment;
    private EditText mPrice;
    private EditText discount;
    private TextView actualPrice;
    private Book book;
    private String mBranch;
    private String mYear;
    private String mSubject;
    private ProgressBar progressBar;
    private ImageView cameraIcon;

    private String sellerName;
    private String sellerContact;
    private static final String TAG="ADD_ITEM";
    private Button cancelButton;
    private Button addButton;
    private String photoUrl;
    private StorageReference mStrorageStorageReference;
    private FirebaseFirestore db;
    private int branch=0;
    private int year=0;
    private Uri imageUri;
    SharedPreferences sharedPreferences;
    private byte[] compressedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        bookImageView=findViewById(R.id.itemImage);
        bookNameText = findViewById(R.id.itemName);
        mBranchSpinner = findViewById(R.id.spinnerBranchActivity);
        mYearSpinner = findViewById(R.id.spinnerYearActivity);
        mSubjectSpinner = findViewById(R.id.spinnerSubjectActivity);
        mPublisherText = findViewById(R.id.publisherName);
        mComment = findViewById(R.id.comment);
        mPrice = findViewById(R.id.price);
        addButton = findViewById(R.id.buttonAdd);
        progressBar=findViewById(R.id.addItemProgressBar);
        cameraIcon=findViewById(R.id.cameraIcon);
        discount=findViewById(R.id.discount);
        actualPrice=findViewById(R.id.calPrice);

        cancelButton=findViewById(R.id.buttonCancel);
        sharedPreferences=getSharedPreferences(NewProfileActivity.SP,MODE_PRIVATE);
        sellerName = sharedPreferences.getString(NewProfileActivity.USERNAME,"Unknown");
        sellerContact =sharedPreferences.getString(NewProfileActivity.USERCONTACT,"Unknown");
        db=FirebaseFirestore.getInstance();
        mStrorageStorageReference = FirebaseStorage.getInstance().getReference();
        book=new Book();


        mBranchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 mBranch = parent.getItemAtPosition(position).toString();
                branch=position;
                addListToSubject();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                mBranch =parent.getItemAtPosition(0).toString();
            }
        });
        mYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mYear = parent.getItemAtPosition(position).toString();
                year=position;
                addListToSubject();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                mYear=parent.getItemAtPosition(0).toString();
            }
        });
        mSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubject = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                mSubject=parent.getItemAtPosition(0).toString();
            }
        });


        bookImageView.setOnClickListener(v -> {
            Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent,1);
        });


        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float dis;
                if (TextUtils.isEmpty(discount.getText())||TextUtils.isEmpty(mPrice.getText()))
                {
                    dis=0;
                    actualPrice.setText(mPrice.getText());
                }
                else {
                    dis=Float.parseFloat(discount.getText().toString());
                    float price=Float.parseFloat(mPrice.getText().toString());
                    price=(price*(1-dis))/100;
                    actualPrice.setText(String.valueOf(price));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                float dis;
                if (TextUtils.isEmpty(discount.getText())||TextUtils.isEmpty(mPrice.getText()))
                {
                    dis=0;
                    actualPrice.setText(mPrice.getText());
                }
                else {
                    dis=Float.parseFloat(discount.getText().toString());
                    float price=Float.parseFloat(mPrice.getText().toString());
                    price=price*(1-dis/100);
                    actualPrice.setText(String.valueOf(price));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(bookNameText.getText())) {
                    bookNameText.setError("Empty");
                    return;
                } else {
                    book.setBookName(bookNameText.getText().toString().trim());
                }
                if (TextUtils.isEmpty(mPublisherText.getText())) {
                    mPublisherText.setError("Empty");
                    return;
                } else {
                    book.setPublisher(mPublisherText.getText().toString().trim());
                }
                if (TextUtils.isEmpty(mPrice.getText())) {
                    mPrice.setError("Empty");
                    return;
                } else {
                    book.setPrice((int)Float.parseFloat(actualPrice.getText().toString()));
                }

                book.setSold(false);
                book.setBranch(mBranch);
                book.setSubject(mSubject);
                book.setCourseYear(mYear);
                book.setSellerName(sellerName);
                book.setSellerContact(sellerContact);
                book.setComment(mComment.getText().toString().trim());
                book.setActualPrice(Float.parseFloat(mPrice.getText().toString()));
                book.setDiscount((int)Float.parseFloat(discount.getText().toString()));
                book.setUid(getSharedPreferences(NewProfileActivity.SP,MODE_PRIVATE).getString(NewProfileActivity.USERID,"unknown"));

                progressBar.setVisibility(ProgressBar.VISIBLE);

                bookImageView.setDrawingCacheEnabled(true);
                bookImageView.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) bookImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                compressedImage = baos.toByteArray();
                if (compressedImage!=null) {

                    final StorageReference filepath = mStrorageStorageReference.child("images").child("my_image_" + Timestamp.now().getSeconds());

                    filepath.putBytes(compressedImage).addOnSuccessListener(taskSnapshot -> {
                        filepath.getDownloadUrl().addOnSuccessListener(uri -> {

                            photoUrl = uri.toString();
                            book.setPhoto(photoUrl);
                            db.collection("books").add(book).addOnSuccessListener(documentReference -> {
                                progressBar.setVisibility(ProgressBar.GONE);
                                Toast.makeText(AddItemActivity.this.getApplicationContext(), "Item Added!", Toast.LENGTH_SHORT).show();
                                AddItemActivity.this.startActivity(new Intent(AddItemActivity.this, MainActivity.class));
                            });

                        });
                    }).addOnFailureListener(e -> {
                        progressBar.setVisibility(ProgressBar.GONE);
                        Toast.makeText(AddItemActivity.this.getApplicationContext(), "Please try again!", Toast.LENGTH_SHORT).show();

                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressBar.setProgress((int) progress);
                        }
                    });
                }

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); // we have the actual path to the image

                bookImageView.setImageURI(imageUri);//show image
                cameraIcon.setVisibility(ImageView.GONE);
            }
        }
    }
    private void addListToSubject()
    {
        Log.d("FilterDialogFragment",""+whichListToAdd());
        ArrayList<String> subjects = new ArrayList<>(Arrays.asList(getResources().getStringArray(whichListToAdd())));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,subjects);
        mSubjectSpinner.setAdapter(arrayAdapter);

    }

    private int whichListToAdd()
    {

        switch (branch)
        {
            case 1:{
                switch (year) {

                    case 1:return R.array.first_year;
                    case 2:return R.array.sec_comp;
                    case 3:return R.array.third_comp;
                    default:return R.array.forth_year;
                }     }
            case 2:{
                switch (year) {

                    case 1:return R.array.first_year;
                    case 2:return R.array.sec_it;
                    case 3:return R.array.third_it;
                    default:return R.array.forth_year;
                }     }
            case 3:{
                switch (year) {

                    case 1:return R.array.first_year;
                    case 2:return R.array.sec_elec;
                    case 3:return R.array.third_elec;
                    default:return R.array.forth_year;
                }     }
            case 4:{
                switch (year) {

                    case 1:return R.array.first_year;
                    case 2:return R.array.sec_entc;
                    case 3:return R.array.third_entc;
                    default:return R.array.forth_year;
                }     }
            case 5:{
                switch (year) {

                    case 1:return R.array.first_year;
                    case 2:return R.array.sec_mech;
                    case 3:return R.array.third_mech;
                    default:return R.array.forth_year;
                }     }


            default:return R.array.forth_year;

        }
    }


}
