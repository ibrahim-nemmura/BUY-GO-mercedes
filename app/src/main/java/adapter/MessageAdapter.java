package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import model.ChatMessage;

import com.example.bookapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private Context mContext;
    private List<ChatMessage> mChat;
    private DatabaseReference userRef;
    private String userId;

    public MessageAdapter( List<ChatMessage> mChat,String userId) {

        this.mChat = mChat;
        this.userId=userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.chat_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chat = mChat.get(position);
        String FromMessageType = chat.getMessage();

        if (!FromMessageType.equals("")) {
            holder.receiverMessage.setVisibility(View.INVISIBLE);

            if (!chat.getSender().equals(userId)) {
                holder.senderMessage.setVisibility(View.VISIBLE);
                holder.senderMessage.setBackgroundResource(R.drawable.background_left);
                holder.senderMessage.setText(chat.getMessage());
            } else {
                holder.senderMessage.setVisibility(View.INVISIBLE);
                holder.receiverMessage.setVisibility(View.VISIBLE);
                holder.receiverMessage.setBackgroundResource(R.drawable.background_right);
                holder.receiverMessage.setText(chat.getMessage());
            }
        }
    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessage, receiverMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.receiver_message);
            receiverMessage = itemView.findViewById(R.id.sender_message);

        }
    }
}

