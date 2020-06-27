package com.example.bookapp;


import adapter.SellBookAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class SellActivity extends AppCompatActivity implements
        SellBookAdapter.OnSellBookSelectedListener{

    private Toolbar mToolbar;
    private Query mQuery;
    private FloatingActionButton mAddItemButton;
    private FirebaseFirestore mFirestore;
    private SharedPreferences sharedPreferences;
    public RecyclerView mBookRecycler;
    public ViewGroup mEmptyView;
    private SellBookAdapter mAdapter;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        switch (item.getItemId()) {
            case R.id.nav_buy:
                startActivity(new Intent(SellActivity.this,MainActivity.class));
                break;
            case R.id.nav_sell:
                //finish();
                //startActivity(getIntent());
                break;
            case R.id.nav_ebook:
                //code to go to eBook sections
                startActivity(new Intent(this,ChatActivity.class));
                break;
        }
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        sharedPreferences =getSharedPreferences(NewProfileActivity.SP,MODE_PRIVATE);
        mToolbar = findViewById(R.id.toolbar_sell);

        BottomNavigationView navigation = findViewById(R.id.navigation_sell);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.nav_sell);
        mAddItemButton=findViewById(R.id.addItemButton);
        mAddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellActivity.this, AddItemActivity.class));
            }
        });


        setSupportActionBar(mToolbar);
        FirebaseFirestore.setLoggingEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();


        mBookRecycler=findViewById(R.id.recyclerBooks);
        mEmptyView=findViewById(R.id.viewEmpty);


        mQuery = mFirestore.collection("products").whereEqualTo("sellerName",sharedPreferences.getString(NewProfileActivity.USERNAME,"unknown"))
                .orderBy("price", Query.Direction.DESCENDING);

        // RecyclerView
        mAdapter = new SellBookAdapter(mQuery,  this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                Log.d("SellActivity",""+getItemCount());
                if (getItemCount() == 0) {
                    mBookRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mBookRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Log.e("SellActivity","below error",e);
            }
        };



        mBookRecycler.setLayoutManager(new LinearLayoutManager(this));
        mBookRecycler.setAdapter(mAdapter);



    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.go_app:
                //write code to move to GO app
                break;
            case R.id.sign_out:
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean(NewProfileActivity.LOGGEDIN,false).commit();
                AuthUI.getInstance().signOut(this);
                startActivity(new Intent(SellActivity.this,LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSellBookSelected(DocumentSnapshot book) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.KEY_BOOK_ID, book.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void onDeletePressed(DocumentSnapshot del) {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("products").document(del.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(findViewById(android.R.id.content),
                                        "Successfully deleted", Snackbar.LENGTH_LONG).show();
                                Log.d("SellBookAdapter", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("SellBookAdapter", "Error deleting document", e);

                            }
                        });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


}
