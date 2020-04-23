package com.example.bookapp;

import adapter.ChatAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import model.ChatDoc;
import model.ChatMessage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity implements ChatAdapter.OnChatSelectedListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private Intent intent;
    private String recUid;
    private FirebaseDatabase database;
    private DatabaseReference profileRef;
    private String uid;
    private ChatAdapter adapter;
    private final List<ChatDoc> chats = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chatToolbar);
        recyclerView = findViewById(R.id.allChatRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance();

        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreferences = getSharedPreferences(NewProfileActivity.SP, MODE_PRIVATE);
        uid = sharedPreferences.getString(NewProfileActivity.USERID, "Unknown");
        intent = getIntent();
        adapter = new ChatAdapter(chats, this);
        recyclerView.setAdapter(adapter);
        if (intent != null && intent.getStringExtra("rec") != null) {
            recUid = intent.getStringExtra("rec");
            checkExistingChat();
        } else
            showAllChat();



    }


    private void checkExistingChat() {


        profileRef = database.getReference("profile").child(uid);
        DatabaseReference recProfileRef = database.getReference("profile").child(recUid);
        DatabaseReference chatRef = database.getReference("chat");

        //if found get chat id
        profileRef.child(recUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String chatId = dataSnapshot.getValue(String.class);
                if (chatId != null) {
                    //send data to message
                    Intent intent=new Intent(ChatActivity.this,MessageActivity.class);
                    intent.putExtra("chatId",chatId);
                    intent.putExtra("senderId",recUid);
                    startActivity(intent);

                } else {
                    //call to create message and add to both profiles
                    DatabaseReference chatIdRef = chatRef.push();
                    String newChatId = chatIdRef.getKey();
                    profileRef.child(recUid).setValue(newChatId);
                    recProfileRef.child(uid).setValue(newChatId);

                    Intent intent=new Intent(ChatActivity.this,MessageActivity.class);
                    intent.putExtra("chatId",newChatId);
                    intent.putExtra("senderId",recUid);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ChatActivity", "The read failed: " + databaseError.getCode());
            }
        });
    }

    private void showAllChat() {
        //List<ChatDoc> chats=new ArrayList<>();


        profileRef = database.getReference("profile").child(uid);

        profileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ChatDoc doc= new ChatDoc();
                doc.setSender(dataSnapshot.getKey());
                doc.setChatid(dataSnapshot.getValue(String.class));

                chats.add(doc);
                adapter.notifyDataSetChanged();



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                ChatDoc doc=new ChatDoc();
                  doc.setSender(dataSnapshot.getKey());
                    doc.setChatid(dataSnapshot.getValue().toString());
                    chats.remove(doc);
                    adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onChatSelected(ChatDoc chatDoc) {
        Intent intent=new Intent(ChatActivity.this,MessageActivity.class);
        intent.putExtra("chatId",chatDoc.getChatid());
        intent.putExtra("senderId",chatDoc.getSender());
        startActivity(intent);
    }


}

