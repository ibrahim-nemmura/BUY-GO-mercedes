package com.example.bookapp;

import adapter.MessageAdapter;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;



import de.hdodenhof.circleimageview.CircleImageView;

import model.ChatMessage;
import model.Profile;

public class MessageActivity extends AppCompatActivity {


    private TextView textView;
    private CircleImageView circleImageView;
    private ImageButton btn_send;
    private EditText text_send;
    private Intent intent;
    private String sellerContact;
    private String uid;
    private Profile profile;
    private Toolbar toolbar;

    private final List<ChatMessage> mchat=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private String TAG="MessageActivity";
    private String userId;
    private String chatId;
    private String senderId;
    private FirebaseFirestore db;
    private DatabaseReference reference;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        userId=getSharedPreferences(NewProfileActivity.SP,MODE_PRIVATE).getString(NewProfileActivity.USERID,"unknown");
        reference=FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        db=FirebaseFirestore.getInstance();
        toolbar=findViewById(R.id.toolbar);
        textView=findViewById(R.id.username);
        circleImageView=findViewById(R.id.profile_image);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.input);
        textView.setText(sellerContact);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        intent=getIntent();
        if(intent!=null) {
            chatId = intent.getStringExtra("chatId");
            senderId=intent.getStringExtra("senderId");
        }

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo open profile
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = text_send.getText().toString().trim();
                if(!TextUtils.isEmpty(msg)){
                    sendMessage(userId,msg, Timestamp.now().toString());

                }else{
                    Toast.makeText(MessageActivity.this,"Empty Message",Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");

            }
        });


        getProfile();

        messageAdapter=new MessageAdapter(mchat,userId);
        recyclerView=findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager =new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);

    }

    private void getProfile()
    {
        DocumentReference docRef = db.collection("profile").document(senderId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        profile=document.toObject(Profile.class);
                        updateUI();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    private void updateUI()
    {
        Glide.with(circleImageView.getContext())
                .load(profile.getProfilePic())
                .into(circleImageView);

        textView.setText(profile.getProfileName());
    }

    private void sendMessage(String userId,String message,String time)  {

        DatabaseReference ref= reference.child("Chats").child(chatId);
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setSender(userId);
        chatMessage.setTimestamp(time);
        ref.push().setValue(chatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG,"successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"unsuccessful");
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.child("Chats").child(chatId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage chat =dataSnapshot.getValue(ChatMessage.class);
                    mchat.add(chat);
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
