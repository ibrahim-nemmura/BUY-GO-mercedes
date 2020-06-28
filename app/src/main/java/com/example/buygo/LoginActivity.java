package com.example.buygo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private Button register;
    private EditText mail_login, pass_login;
    private Button   facebook;




    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            Intent intent = new Intent(getApplicationContext(),NewProfileActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mail_login = findViewById(R.id.e_mail);
        pass_login = findViewById(R.id.password);
        login = findViewById(R.id.login_btn);
        register = findViewById(R.id.register);
        facebook = findViewById(R.id.facebook);

        mAuth = FirebaseAuth.getInstance();

        createRequest();



        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null){

            Intent intent = new Intent(LoginActivity.this, NewProfileActivity.class);
            startActivity(intent);
            finish();
        }

        //declaring the button of (Google ile giriş yap)
        findViewById(R.id.google_sign_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    //defining the method of (Google ile giriş yap)
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount acct = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(acct);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, "Failed.. please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //declaring the button of (Kayit ol)
    public void registerClicked (View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
        finish();
    }

    //declaring the button of (Gir)
    public void signinClicked (View view) {
        EditText emailText = findViewById(R.id.e_mail);
        EditText passworText = findViewById(R.id.password);

        String emailUser = emailText.getText().toString();
        String password = passworText.getText().toString();
        //defining the method of (Gir)
        mAuth.signInWithEmailAndPassword(emailUser,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent = new Intent(LoginActivity.this, NewProfileActivity.class);
                intent.putExtra("USER_ID",mAuth.getUid());
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //Authenticating the method of (Google ile giriş yap)
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(getApplicationContext(), NewProfileActivity.class);
                            intent.putExtra("USER_ID",user.getUid());
                            intent.putExtra("USER_CONTACT",user.getPhoneNumber());
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Sorry", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
