package adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import model.ChatDoc;
import model.Profile;
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    public interface OnChatSelectedListener{
        void onChatSelected(ChatDoc chatDoc);
    }

    private OnChatSelectedListener listener;


    private List<ChatDoc> chats;

    public ChatAdapter(List<ChatDoc> chats, OnChatSelectedListener listener)
    {
        this.chats=chats;
        this.listener=listener;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ChatViewHolder(inflater.inflate(R.layout.chat_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("profile").document(chats.get(position).getSender()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Profile profile;
                profile = documentSnapshot.toObject(Profile.class);
                holder.bind(chats.get(position),profile,listener);
            }
        });

        Log.d("ChatAdapter","pos: "+position+" chats"+chats.get(position).getChatid());
        Log.d("ChatAdapter",chats.toString());

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }



     static class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;
        TextView name;
        TextView lastChat;
        TextView lastSeen;





        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            pic=itemView.findViewById(R.id.chatProfilePic);
            name=itemView.findViewById(R.id.chatName);
            lastChat=itemView.findViewById(R.id.chatLast);
            lastSeen=itemView.findViewById(R.id.lastSeen);
        }

        public void bind(ChatDoc chatDoc,Profile profile,OnChatSelectedListener listener) {

            Glide.with(pic.getContext())
                    .load(profile.getProfilePic())
                    .into(pic);
            name.setText(profile.getProfileName());
            if(chatDoc.getLastMessage()!=null)
                lastChat.setText(chatDoc.getLastMessage());
            if(chatDoc.getTimestamp()!=null)
                lastSeen.setText(chatDoc.getTimestamp());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onChatSelected(chatDoc);
                }
            });

        }


    }
}