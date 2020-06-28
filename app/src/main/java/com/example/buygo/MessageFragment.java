//package com.example.ibrahim;
///*
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import de.hdodenhof.circleimageview.CircleImageView;
//import model.Profile;
//
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MessageFragment.OnMessageSentListener} interface
// * to handle interaction events.
// */
//public class MessageFragment extends Fragment {
//
//
//
//    TextView textView;
//    CircleImageView circleImageView;
//    ImageButton btn_send;
//    EditText text_send;
//    Intent intent;
//    String sellerContact;
//    String uid;
//    Profile profile;
//
//    private final List<Chat> mchat=new ArrayList<>();
//    private LinearLayoutManager linearLayoutManager;
//    private MessageAddapter messageAddapter;
//    private RecyclerView recyclerView;
//    private String TAG="MessageActivity";
//    FirebaseUser firebaseUser;
//    String CurrUser;
//    FirebaseFirestore db;
//    DatabaseReference reference;
//    FirebaseDatabase database;
//
//    private OnMessageSentListener mListener;
//
//    public MessageFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view=inflater.inflate(R.layout.activity_message, container, false);
//
//
//
//        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
//        CurrUser=firebaseUser.getUid();
//
//        reference=FirebaseDatabase.getInstance().getReference();
//
//        database = FirebaseDatabase.getInstance();
//
//        intent=getIntent();
//        sellerContact=intent.getStringExtra(BookDetailActivity.SELLER_NAME);
//        uid=intent.getStringExtra(BookDetailActivity.SELLER_UID);
//
//
//        textView=view.findViewById(R.id.username);
//        circleImageView=view.findViewById(R.id.profile_image);
//        btn_send=view.findViewById(R.id.btn_send);
//        text_send=view.findViewById(R.id.input);
//        textView.setText(sellerContact);
//
//
//
//
//
//        btn_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String msg = text_send.getText().toString().trim();
//                if(!TextUtils.isEmpty(msg)){
//                    sendMessage(CurrUser,uid,msg);
//
//                    // readMessages(firebaseUser.getUid(),uid);
//                }else{
//                    Toast.makeText(MessageActivity.this,"You cant send empty message",Toast.LENGTH_SHORT).show();
//                }
//
//                text_send.setText("");
//
//            }
//        });
//        db=FirebaseFirestore.getInstance();
//        getProfile();
//
//        messageAddapter=new MessageAddapter(MessageActivity.this,mchat);
//        recyclerView=findViewById(R.id.recycler);
//        recyclerView.setHasFixedSize(true);
//        linearLayoutManager =new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(messageAddapter);
//        // Inflate the layout for this fragment
//        return view;
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnMessageSentListener) {
//            mListener = (OnMessageSentListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnMessageSentListener {
//        // TODO: Update argument type and name
//        void onMessageSentButtonPressed(String message);
//    }
//
//
//
//    public class MessageActivity extends AppCompatActivity {
//
//
//        TextView textView;
//        CircleImageView circleImageView;
//        ImageButton btn_send;
//        EditText text_send;
//        Intent intent;
//        String sellerContact;
//        String uid;
//        Profile profile;
//
//        private final List<Chat> mchat=new ArrayList<>();
//        private LinearLayoutManager linearLayoutManager;
//        private MessageAddapter messageAddapter;
//        private RecyclerView recyclerView;
//        private String TAG="MessageActivity";
//        FirebaseUser firebaseUser;
//        String CurrUser;
//        FirebaseFirestore db;
//        DatabaseReference reference;
//        FirebaseDatabase database;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_message);
//
//
//
//        }
//
//        private void getProfile()
//        {
//            DocumentReference docRef = db.collection("profile").document(uid);
//            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            profile=document.toObject(Profile.class);
//                            updateUI();
//
//                        } else {
//                            Log.d(TAG, "No such document");
//                        }
//                    } else {
//                        Log.d(TAG, "get failed with ", task.getException());
//                    }
//                }
//            });
//        }
//        private void updateUI()
//        {
//            Glide.with(circleImageView.getContext())
//                    .load(profile.getProfilePic())
//                    .into(circleImageView);
//
//        }
//
//        private void sendMessage(String sender,String receiver,String message)  {
//
//            DatabaseReference ref= reference.child("Chats");
//            Map<String, Object> hashMap= new HashMap<>();
//            hashMap.put("sender",sender);
//            hashMap.put("receiver",receiver);
//            hashMap.put("message",message);
//            ref.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    Log.d(TAG,"successful");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d(TAG,"unsuccessful");
//                }
//            });
//
//
//        }
//
//        @Override
//        protected void onStart() {
//            super.onStart();
//
//            reference.child("Chats").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    Chat chat =dataSnapshot.getValue(Chat.class);
//                    if (chat.getReceiver().equals(CurrUser) && chat.getSender().equals(uid) ||
//                            chat.getReceiver().equals(uid) && chat.getSender().equals(CurrUser)) {
//                        mchat.add(chat);
//                    }
//
//                    messageAddapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }/
//
//}
