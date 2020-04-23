package com.example.bookapp;

import androidx.annotation.Nullable;
import model.Profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends Activity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView profilePic;
    private EditText profileName, profileAddress, profileCollege;
    private Spinner profileGender;
    private EditText profilePincode, profileAge;
    private Button profileSave;
    private Profile profile;
    private Uri imageUri;
    private ProgressBar progressBar;
    private StorageReference mStorageReference;
    private FirebaseFirestore db;
    private String contact;
    private String uid;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final String SP="USER_LOGIN";
    public static final String LOGGEDIN="LOGGED_IN";
    public static final String USERID="USER_ID";
    public static final String USERCONTACT="USER_CONTACT";
    public static final String USERNAME="USER_NAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent=getIntent();
        contact=intent.getStringExtra(USERCONTACT);
        uid=intent.getStringExtra(USERID);


        profilePic = findViewById(R.id.profilePic);
        profileName = findViewById(R.id.profileName);
        profileAddress = findViewById(R.id.profileAddress);
        profileCollege = findViewById(R.id.profileCollege);
        profileGender = findViewById(R.id.profileGender);
        profilePincode = findViewById(R.id.profilePincode);
        profileAge = findViewById(R.id.profileAge);
        profileSave = findViewById(R.id.profileSave);
        toolbar = findViewById(R.id.profileToolbar);
        progressBar = findViewById(R.id.profileProgress);
        sharedPreferences = getSharedPreferences(SP,MODE_PRIVATE);
        editor =sharedPreferences.edit();
        profile = new Profile();


        profilePic.setOnClickListener(this);
        profileSave.setOnClickListener(this);


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle(contact);
        toolbar.setNavigationOnClickListener(v -> finish());

        mStorageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profilePic:
                getProfilePic();
                break;

            case R.id.profileSave:
                saveProfile();
                break;

        }
    }



    private void getProfilePic() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); // we have the actual path to the image

                profilePic.setImageURI(imageUri);//show image

            }
        }
    }

    private void saveProfile() {

        //age
        if (TextUtils.isEmpty(profileAge.getText())) {
            profileAge.setError("Empty");
            return;
        } else {
            profile.setProfileAge(Integer.parseInt(profileAge.getText().toString()));
        }

        //name
        if (TextUtils.isEmpty(profileName.getText())) {
            profileName.setError("Empty");
            return;
        } else {
            profile.setProfileName((profileName.getText().toString()));
        }

        //pincode
        if (TextUtils.isEmpty(profilePincode.getText())) {
            profilePincode.setError("Empty");
            return;
        } else {
            profile.setProfilePincode(profilePincode.getText().toString());
        }

        //address
        if (TextUtils.isEmpty(profileAddress.getText())) {
            profileAddress.setError("Empty");
            return;
        } else {
            profile.setProfileAddress((profileAddress.getText().toString()));
        }
        //college
        if (TextUtils.isEmpty(profileCollege.getText())) {
            profileCollege.setError("Empty");
            return;
        } else {
            profile.setProfileCollege((profileCollege.getText().toString()));
        }
        //gender
        if (profileGender.getSelectedItemPosition() == 0) {
            profile.setProfileGender("Male");
        } else {
            profile.setProfileGender("Female");
        }
        profile.setProfileContact(contact);


        progressBar.setVisibility(ProgressBar.VISIBLE);

        if (imageUri != null) {
            final StorageReference filepath = mStorageReference.child("profile").child("profile_" + Timestamp.now().getSeconds());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                        profile.setProfilePic(uri.toString());
                        db.collection("profile").document(uid).set(profile).addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(ProgressBar.GONE);
                            Toast.makeText(ProfileActivity.this.getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();

                            editor.putBoolean(LOGGEDIN, true);
                            editor.putString(USERID, uid);
                            editor.putString(USERCONTACT, contact);
                            editor.putString(USERNAME,profile.getProfileName());
                            editor.commit();


                            ProfileActivity.this.startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        });
                    });
                }
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(getApplicationContext(), "Please try again...", Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            });
        }


    }

}
