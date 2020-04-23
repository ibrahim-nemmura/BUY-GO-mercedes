package adapter;


import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookapp.R;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;



import model.Book;

/**
 * RecyclerView adapter for a list of book items.
 */
public class BookAdapter extends FirestoreAdapter<BookAdapter.ViewHolder> {

    public interface OnBookSelectedListener {

        void onBookSelected(DocumentSnapshot book);

    }

    private OnBookSelectedListener mListener;

    public BookAdapter(Query query, OnBookSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.itemview, parent, false));
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
        TextView realPrice;
        TextView discountPer;
        ImageView soldOut;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bookImage);
            bookNameView = itemView.findViewById(R.id.bookNameItem);
            bookYearView = itemView.findViewById(R.id.bookYear);
            priceView = itemView.findViewById(R.id.bookPrice);
            bookBranchView = itemView.findViewById(R.id.bookBranch);
            publisherView = itemView.findViewById(R.id.bookPublisher);
            realPrice=itemView.findViewById(R.id.realPrice);
            discountPer=itemView.findViewById(R.id.discountPer);
            soldOut=itemView.findViewById(R.id.sold_out);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnBookSelectedListener listener) {

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
            discountPer.setText(book.getDiscount()+"");
            String  text="<del>"+book.getActualPrice()+"</del>";
            Spanned html= Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY);
            realPrice.setText(html);
            if (book.isSold()) {
                soldOut.setVisibility(View.VISIBLE);
            }else
                soldOut.setVisibility(View.GONE);


            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onBookSelected(snapshot);
                    }
                }
            });
        }

    }
}
