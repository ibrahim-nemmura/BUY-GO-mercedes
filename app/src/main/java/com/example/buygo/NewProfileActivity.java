package com.example.buygo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import model.Profile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class NewProfileActivity extends AppCompatActivity implements WelcomeFragment.OnWelcomeFragmentInteractionListener, ProfileFragmentName.OnFragmentInteractionListener, PaymentFragment.OnSaveUpiButtonPressedListener {

    private static final String TAG = "NewProfileActivity";
    public static final String USERUPI = "USER_UPI";
    public static final String SP = "USER_LOGIN";
    public static final String LOGGEDIN = "LOGGED_IN";
    public static final String USERID = "USER_ID";
    public static final String USERCONTACT = "USER_CONTACT";
    public static final String USERNAME = "USER_NAME";

    private ProgressBar progressBar;
    private Profile profile;
    private FragmentManager manager;
    private ImageView profilePic;
    private Uri imageUri;
    private StorageReference mStorageReference;
    private FirebaseFirestore db;
    private String contact;
    private String uid;
    private byte[] compressedImage;
    private SharedPreferences sharedPreferences;
    private Bitmap selectedImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
        manager = getSupportFragmentManager();

        progressBar=findViewById(R.id.progressbarProfile);
        progressBar.setVisibility(View.GONE);
        profilePic = findViewById(R.id.newProfilePic);
        profile = new Profile();
        Intent intent = getIntent();
        contact = intent.getStringExtra(USERCONTACT);
        uid = intent.getStringExtra(USERID);

        sharedPreferences = getSharedPreferences(SP, MODE_PRIVATE);



        mStorageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        checkIfProfileExists();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfilePic();
            }
        });
    }


    private void getProfilePic() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent intentToGalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGalery, 2);
        }

        /* Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intentToGalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGalery, 2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();



            try {
                if (Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageUri);
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    profilePic.setImageBitmap(selectedImage);
                }else{
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    profilePic.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePic()
    {
       profilePic.setDrawingCacheEnabled(true);
        profilePic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        compressedImage = baos.toByteArray();
        if (compressedImage!=null) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            final StorageReference filepath = mStorageReference.child("profile").child("profile_" + Timestamp.now().getSeconds());
            UploadTask uploadTask = filepath.putBytes(compressedImage);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profile.setProfilePic(uri.toString());
                            db.collection("profile").document(uid).set(profile).addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(ProgressBar.GONE);
                                SharedPreferences.Editor editor;
                                editor = sharedPreferences.edit();
                                editor.putBoolean(LOGGEDIN, true);
                                editor.putString(USERID, uid);
                                editor.putString(USERCONTACT, contact);
                                editor.putString(USERNAME, profile.getProfileName());
                                editor.putString(USERUPI, profile.getProfileUpi());
                                editor.apply();
                                Bundle bundle = new Bundle();
                                bundle.putString("name", profile.getProfileName());
                                bundle.putString("contact", profile.getProfileContact());
                                WelcomeFragment fragment = new WelcomeFragment();
                                fragment.setArguments(bundle);
                                manager.beginTransaction().replace(R.id.profileContainer, fragment).commit();
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(),"Please add pic",Toast.LENGTH_LONG);
        }
    }



    @Override
    public void onSignUpPressed(String name, String email, String college, String address, String pincode, int age, String gender) {
        profile.setProfileName(name);
        profile.setProfileEmail(email);
        profile.setProfileCollege(college);
        profile.setProfileAddress(address);
        profile.setProfilePincode(pincode);
        profile.setProfileAge(age);
        profile.setProfileContact(contact);
        profile.setProfileGender(gender);
        manager.beginTransaction().replace(R.id.profileContainer, new PaymentFragment()).commit();
    }

    private void checkIfProfileExists() {
        DocumentReference docRef = db.collection("profile").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (Objects.requireNonNull(document).exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        profile = document.toObject(Profile.class);
                        Log.d("NewProfileActivity","profile number"+ Objects.requireNonNull(profile).getProfileContact());
                        Bundle bundle = new Bundle();
                        bundle.putString("name", profile.getProfileName());
                        bundle.putString("contact", profile.getProfileContact());
                        WelcomeFragment fragment = new WelcomeFragment();
                        fragment.setArguments(bundle);
                        updateUI();
                        manager.beginTransaction().replace(R.id.profileContainer, fragment).commit();
                    } else {
                        Log.d(TAG, "No such document");
                        manager.beginTransaction().add(R.id.profileContainer, new ProfileFragmentName()).commit();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI() {
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(LOGGEDIN, true);
        editor.putString(USERID, uid);
        editor.putString(USERCONTACT, contact);
        editor.putString(USERNAME, profile.getProfileName());
        editor.putString(USERUPI, profile.getProfileUpi());
        editor.apply();
        profilePic.setEnabled(false);
        Glide.with(profilePic.getContext())
                .load(profile.getProfilePic())
                .into(profilePic);
    }

    @Override
    public void onSaveUpiButtonPressed(String upiId) {
        profile.setProfileUpi(upiId);
        uploadProfilePic();
        profilePic.setEnabled(false);
    }

    @Override
    public void onLetsGoButtonPressed() {

        startActivity(new Intent(NewProfileActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));

    }
}