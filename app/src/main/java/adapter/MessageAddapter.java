package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import model.Chat;

public class MessageAddapter extends RecyclerView.Adapter<MessageAddapter.ViewHolder> {

    FirebaseUser firebaseUser;
    private Context mContext;
    private List<Chat> mChat;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;

    public MessageAddapter(Context mContext, List<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public MessageAddapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);

        firebaseAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String senderId = firebaseAuth.getCurrentUser().getUid();
        Chat chat = mChat.get(position);
        String fromUserId = chat.getReceiver();
        String FromMessageType = chat.getMessage();

        if (!FromMessageType.equals("")) {
            holder.receiverMessage.setVisibility(View.INVISIBLE);

            if (fromUserId.equals(senderId)) {
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

