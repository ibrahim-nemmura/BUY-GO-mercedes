package adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buygo.MessageActivity;
import com.example.buygo.R;

import java.util.List;

import model.Profile;

public class ChatuserAdapter extends RecyclerView.Adapter<ChatuserAdapter.ViewHolder> {

    private Context mContext;
    private List<Profile> mUsers;
    SharedPreferences sharedPreferences;

    public ChatuserAdapter(Context mContext, List<Profile> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
        return new ChatuserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile user = mUsers.get(position);
        holder.username.setText(user.getProfileName());
        if (user.getProfilePic().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);

        } else {

            Glide.with(mContext).load(user.getProfilePic()).into(holder.profile_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MessageActivity.class);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;

        public ViewHolder(View itemView) {

            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
