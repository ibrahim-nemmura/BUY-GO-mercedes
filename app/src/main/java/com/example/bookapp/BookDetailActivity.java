package com.example.bookapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import model.Book;
import model.Profile;


public class BookDetailActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>, View.OnClickListener {

    private static final String TAG = "BookDetail";

    public static final String KEY_BOOK_ID = "key_book_id";

    private ImageView mImageView;
    private TextView mBookNameView;
    private TextView mBookSubjectView;
    private TextView mYearView;
    private TextView mBranchView;
    private TextView mPriceView;
    private TextView mPublisherName;
    private TextView mSellerName;
    private TextView mSellerContact;
    private TextView mComment;
    private TextView originalPrice;
    private TextView bookDis;
    private ViewGroup commentInfo;
    private Button chatButton;
    private String uid;
    private ImageView soldOut;
    private ImageButton payment;


    private FirebaseFirestore mFirestore;
    private DocumentReference mBookRef;
    private ListenerRegistration mBookRegistration;
    private String bookId;
    private Book newBook;
    final int UPI_PAYMENT = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        chatButton=findViewById(R.id.textCardButton);
        chatButton.setOnClickListener(this);
        mImageView = findViewById(R.id.bookImage);
        mBookNameView = findViewById(R.id.bookName);
        mSellerName = findViewById(R.id.sellerName);
        mSellerContact = findViewById(R.id.contactNumber);
        mYearView = findViewById(R.id.bookYear);
        mBranchView = findViewById(R.id.bookBranch);
        mPriceView = findViewById(R.id.bookPrice);
        mPublisherName = findViewById(R.id.bookPublisherName);
        mBookSubjectView = findViewById(R.id.bookSubject);
        mComment = findViewById(R.id.comment);
        soldOut=findViewById(R.id.soldOut);
        originalPrice = findViewById(R.id.originalPrice);
        bookDis = findViewById(R.id.bookDiscount);
        commentInfo = findViewById(R.id.commentInfo);
        findViewById(R.id.bookButtonBack).setOnClickListener(this);
        findViewById(R.id.callButton).setOnClickListener(this);
        payment=findViewById(R.id.payment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newBook.isSold())
                    Toast.makeText(getApplicationContext(),"Already Sold",Toast.LENGTH_LONG).show();
                else
                startPayment();
            }
        });

        bookId = getIntent().getExtras().getString(KEY_BOOK_ID);
        if (bookId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_BOOK_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mBookRef = mFirestore.collection("products").document(bookId);


    }

    private void startPayment() {


        FirebaseFirestore.getInstance().collection("profile").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Profile profile=documentSnapshot.toObject(Profile.class);
                payUsingUpi(profile.getProfileName(), profile.getProfileUpi(),
                        "Book id "+bookId,String.valueOf(newBook.getPrice()));

            }
        });



    }

    void payUsingUpi(  String name,String upiId, String note, String amount) {
        Log.e("main ", "name " + name + "--up--" + upiId + "--" + note + "--" + amount);

        String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
        int GOOGLE_PAY_REQUEST_CODE = 123;
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
        startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+requestCode );
        Log.e("main1 ", "response "+resultCode );

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult1: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult2: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                updateBookDetail();
                Toast.makeText(this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);

            }
            else {
                Toast.makeText(this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);

            }
        } else {
            Log.e("UPI", "Internet issue: ");

            Toast.makeText(this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBookDetail() {
        newBook.setSold(true);
        soldOut.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("products").document(bookId).set(newBook).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
             Toast.makeText(getApplicationContext(),"Done!",Toast.LENGTH_LONG).show();
            }
        });

    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        mBookRegistration = mBookRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();


        if (mBookRegistration != null) {
            mBookRegistration.remove();
            mBookRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }


    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "book:onEvent", e);
            return;
        }

        onBookLoaded(snapshot.toObject(Book.class));
    }
    private void onBookLoaded(Book book) {
        newBook=book;
        Log.d("BookDetail", "" + book.getBookName());
        mBookSubjectView.setText(book.getSubject());
        mPublisherName.setText(book.getPublisher());
        mYearView.setText(book.getCourseYear());
        mBranchView.setText(book.getBranch());
        mPriceView.setText(String.valueOf(book.getPrice()));
        mPublisherName.setText(book.getPublisher());
        mSellerName.setText(book.getSellerName());
        mSellerContact.setText(book.getSellerContact());
        mBookSubjectView.setText(book.getSubject());
        if (TextUtils.isEmpty(book.getComment()))
            commentInfo.setVisibility(View.GONE);
        else
            mComment.setText(book.getComment());
        mBookNameView.setText(book.getBookName());
        if(book.isSold()){
            soldOut.setVisibility(View.VISIBLE);
        }

        uid=book.getUid();

        String  text="<del>"+book.getActualPrice()+"</del>";
        Spanned html=Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY);
        originalPrice.setText(html);
        bookDis.setText(book.getDiscount()+"%off");

        // Background image
        Glide.with(mImageView.getContext())
                .load(book.getPhoto())
                .into(mImageView);
    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    public void onCallButton(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mSellerContact.getText().toString()));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    10);
            return;
        } else {
            try {
                startActivity(callIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bookButtonBack:
                onBackArrowClicked(v);
                break;
            case R.id.callButton:
                onCallButton(v);
                break;
            case R.id.textCardButton:
                onChatStarted();
                break;
        }
    }

    private void onChatStarted() {
        Intent intent=new Intent(this,ChatActivity.class);
        intent.putExtra("rec",uid);
        startActivity(intent);

    }

}


