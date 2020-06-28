package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.buygo.R;



import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



import model.Book;

/**
 * RecyclerView adapter for a list of book items.
 */
public class SellBookAdapter extends FirestoreAdapter<SellBookAdapter.ViewHolder> {

    public interface OnSellBookSelectedListener {

        void onSellBookSelected(DocumentSnapshot book);
        void onDeletePressed(DocumentSnapshot del);
    }

    private OnSellBookSelectedListener mListener;

    public SellBookAdapter(Query query, OnSellBookSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.sell_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView bookNameView;
        TextView bookYearView;
        TextView priceView;
        TextView bookBranchView;
        TextView publisherView;
        Button editButton;
        Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bookImageSell);
            bookNameView = itemView.findViewById(R.id.bookNameItemSell);
            bookYearView = itemView.findViewById(R.id.bookYearSell);
            priceView = itemView.findViewById(R.id.bookPriceSell);
            bookBranchView = itemView.findViewById(R.id.bookBranchSell);
            publisherView = itemView.findViewById(R.id.bookPublisherSell);
            editButton=itemView.findViewById(R.id.editButtonSell);
            deleteButton=itemView.findViewById(R.id.deleteButtonSell);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnSellBookSelectedListener listener) {

            Book book = snapshot.toObject(Book.class);


            // Load image
            Glide.with(imageView.getContext())
                    .load(book.getPhoto())
                    .into(imageView);
            bookNameView.setText(book.getBookName());
            bookYearView.setText(book.getCourseYear());
            priceView.setText(""+(book.getPrice()));
            bookBranchView.setText(book.getBranch());
            publisherView.setText(book.getPublisher());



            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onSellBookSelected(snapshot);
                    }
                }
            });
            //delete button for item
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore db=FirebaseFirestore.getInstance();

                    if (listener != null) {
                        listener.onDeletePressed(snapshot);
                    }

                }
            });
            //edit button for item
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

    }
}
