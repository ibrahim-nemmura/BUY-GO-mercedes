package com.example.buygo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText nameText,surnameText,emailText,passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        nameText = findViewById(R.id.nameText);
        surnameText = findViewById(R.id.surnameText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

    }

    public void signupClicked(View view){

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();


        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Toast.makeText(Register.this,"Aramıza Hoşgeldin!", Toast.LENGTH_LONG).show();


                    EditText emailText = findViewById(R.id.emailText);
                    EditText passworText = findViewById(R.id.passwordText);

                    String emailUser = emailText.getText().toString();
                    String password = passworText.getText().toString();
                    //defining the method of (Gir)
                    mAuth.signInWithEmailAndPassword(emailUser,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(Register.this, NewProfileActivity.class);
                            intent.putExtra("USER_ID",mAuth.getUid());
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this,e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Register.this,e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();

            }
        });




    }
}
