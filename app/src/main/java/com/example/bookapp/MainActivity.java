package com.example.bookapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import adapter.BookAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import model.Book;

import viewmodel.MainActivityViewModel;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements FilterDialogFragment.FilterListener,
        BookAdapter.OnBookSelectedListener, View.OnClickListener {


    private static final String TAG ="MainActivity";


    private Toolbar mToolbar;
    private Query mQuery;
    private TextView mCurrentSearchView;
    private TextView mCurrentSortByView;
    private FilterDialogFragment mFilterDialog;
    private FloatingActionButton mAddItemButton;
    private MainActivityViewModel mViewModel;
    private FirebaseFirestore mFirestore;
    private SharedPreferences sharedPreferences;
    public RecyclerView mBookRecycler;
    public ViewGroup mEmptyView;
    private BookAdapter mAdapter;




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

                switch (item.getItemId()) {
                    case R.id.nav_buy:
                        //code for refresh only if tapped twice
                        //finish();
                        //startActivity(getIntent());
                        break;
                    case R.id.nav_sell:
                        //code to go to sell_activity
                        startActivity(new Intent(MainActivity.this,SellActivity.class));
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
        setContentView(R.layout.activity_main);
        sharedPreferences =getSharedPreferences(NewProfileActivity.SP,MODE_PRIVATE);

        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mCurrentSearchView = findViewById(R.id.textCurrentSearch);
        mCurrentSortByView = findViewById(R.id.textCurrentSortBy);
        mToolbar = findViewById(R.id.toolbar);
        //adding listener for bottom navigation bar

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.nav_buy);
        mAddItemButton=findViewById(R.id.addItemButton);
        mAddItemButton.setOnClickListener(this);
        findViewById(R.id.filterBar).setOnClickListener(this);
        findViewById(R.id.buttonClearFilter).setOnClickListener(this);

        setSupportActionBar(mToolbar);
        FirebaseFirestore.setLoggingEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();


        mBookRecycler=findViewById(R.id.recyclerBooks);
        mEmptyView=findViewById(R.id.viewEmpty);


        mQuery = mFirestore.collection("books")
                .orderBy("price", Query.Direction.DESCENDING);

        // RecyclerView
        mAdapter = new BookAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
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
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }
        };


        mBookRecycler.setLayoutManager(new GridLayoutManager(this,2));
        mBookRecycler.setAdapter(mAdapter);
        mFilterDialog = new FilterDialogFragment();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filterBar:
                onFilterClicked();
                break;
            case R.id.buttonClearFilter:
                onClearFilterClicked();
                break;
            case R.id.addItemButton:
                startActivity(new Intent(MainActivity.this, AddItemActivity.class));
                break;
        }

    }

    public void onFilter(Filters filters) {
        // Construct query basic query
        Query query = mFirestore.collection("books");

        // Branch (equality filter)
        if (filters.hasBranch()) {
            query = query.whereEqualTo(Book.FIELD_BRANCH, filters.getBranch());
        }

        // Year (equality filter)
        if (filters.hasYear()) {
            query = query.whereEqualTo(Book.FIELD_YEAR, filters.getYear());
        }

        // Price (equality filter)
        if (filters.hasPrice()) {
            switch (filters.getPrice())
            {
                case 1:query = query.whereLessThanOrEqualTo(Book.FIELD_PRICE, 200);break;
                case 2:query = query.whereGreaterThan(Book.FIELD_PRICE,200).whereLessThanOrEqualTo(Book.FIELD_PRICE, 500);break;
                case 3:query = query.whereGreaterThan(Book.FIELD_PRICE,500).whereLessThanOrEqualTo(Book.FIELD_PRICE, 1000);break;
                case 4:query = query.whereGreaterThan(Book.FIELD_PRICE, 1000);break;
            }

        }

        if (filters.hasSubject()) {
            query = query.whereEqualTo(Book.FIELD_SUBJECT, filters.getSubject());
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }
        // Limit items
        mQuery = query;

        mAdapter.setQuery(query);

        // Set header
        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this),Html.FROM_HTML_MODE_LEGACY));
        mCurrentSortByView.setText(filters.getOrderDescription(this));

        // Save filters
        mViewModel.setFilters(filters);


    }


    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(Filters.getDefault());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!shouldStartSignIn())
        {
            startActivity(new Intent(this,LoginActivity.class));
        }

        onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
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
            case R.id.sendFeedback:
                //write code to get user feedback
                //startActivity(new Intent(this,NewProfileActivity.class));
                break;
            case R.id.sign_out:
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean(NewProfileActivity.LOGGEDIN,false);
                editor.commit();
                AuthUI.getInstance().signOut(this);
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean shouldStartSignIn() {
        return (sharedPreferences.getBoolean(NewProfileActivity.LOGGEDIN,false));

    }



    public void onBookSelected(DocumentSnapshot book) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.KEY_BOOK_ID, book.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }


}
