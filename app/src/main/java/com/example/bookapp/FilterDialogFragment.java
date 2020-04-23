package com.example.bookapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bookapp.R;
import model.Book;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Dialog Fragment containing filter form.
 */
public class FilterDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "FilterDialog";

    interface FilterListener {

        void onFilter(Filters filters);

    }

    private View mRootView;
    private Spinner mBranchSpinner;
    private Spinner mYearSpinner;
    private Spinner mSortSpinner;
    private Spinner mPriceSpinner;
    private Spinner mSubjectSpinner;
    private int branch=0;
    private int year=0;

    private FilterListener mFilterListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.filter_layout, container, false);

        mBranchSpinner = mRootView.findViewById(R.id.spinnerBranch);
        mYearSpinner = mRootView.findViewById(R.id.spinnerYear);
        mSortSpinner = mRootView.findViewById(R.id.spinnerSort);
        mPriceSpinner = mRootView.findViewById(R.id.spinnerPrice);
        mSubjectSpinner = mRootView.findViewById(R.id.spinnerSubject);

        mBranchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    branch = mBranchSpinner.getSelectedItemPosition();
                    addListToSubject();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    year = mYearSpinner.getSelectedItemPosition();
                    addListToSubject();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mRootView.findViewById(R.id.buttonSearch).setOnClickListener(this);
        mRootView.findViewById(R.id.buttonCancel).setOnClickListener(this);

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getSelectedBranch() {
        String selected = (String) mBranchSpinner.getSelectedItem();
        if (getString(R.string.value_any_branch).equals(selected)) {
            return null;
        } else {

            return selected;
        }
    }

    @Nullable
    private String getSelectedYear() {
        String selected = (String) mYearSpinner.getSelectedItem();
        if (getString(R.string.value_any_year).equals(selected)) {
            return null;
        } else {

            return selected;
        }
    }

    @Nullable
    private String getSelectedSubject() {
        String selected = (String) mSubjectSpinner.getSelectedItem();
        if (getString(R.string.all).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    private int getSelectedPrice() {
        String selected = (String) mPriceSpinner.getSelectedItem();
        if (selected.equals(getString(R.string.price_1))) {
            return 1;
        } else if (selected.equals(getString(R.string.price_2))) {
            return 2;
        } else if (selected.equals(getString(R.string.price_3))) {
            return 3;
        } else if (selected.equals(getString(R.string.price_4))) {
            return 4;
        } else {
            return -1;
        }
    }

    @Nullable
    private String getSelectedSortBy() {
        return Book.FIELD_PRICE;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_price_htl).equals(selected)) {
            return Query.Direction.DESCENDING;
        } if (getString(R.string.sort_by_price_lth).equals(selected)) {
            return Query.Direction.ASCENDING;
        }

        return null;
    }

    public void resetFilters() {
        if (mRootView != null) {
            mBranchSpinner.setSelection(0);
            mYearSpinner.setSelection(0);
            mPriceSpinner.setSelection(0);
            mSortSpinner.setSelection(0);
            mSubjectSpinner.setSelection(0);
            branch=0;
            year=0;
        }
    }

    public Filters getFilters() {
        Filters filters = new Filters();
        if (mRootView != null) {
            filters.setBranch(getSelectedBranch());
            filters.setYear(getSelectedYear());
            filters.setPrice(getSelectedPrice());
            filters.setSubject(getSelectedSubject());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }
        branch=0;
        year=0;
        return filters;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSearch:
                onSearchClicked();
                break;
            case R.id.buttonCancel:
                onCancelClicked();
                break;
        }
    }
    private void addListToSubject()
    {
        ArrayList<String> subjects = new ArrayList<>(Arrays.asList(getResources().getStringArray(whichListToAdd())));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,subjects);
        mSubjectSpinner.setAdapter(arrayAdapter);

    }

    private int whichListToAdd()
    {
        switch (branch)
        {
            case 1:{
                switch (year) {

                    case 1:return R.array.first_year;
                    case 2:return R.array.sec_comp;
                    case 3:return R.array.third_comp;
                    default:return R.array.forth_year;
                }     }
            case 2:{
                switch (year) {

                    case 1:return R.array.first_year;
                    case 2:return R.array.sec_it;
                    case 3:return R.array.third_it;
                    default:return R.array.forth_year;
                }     }
            case 3:{
                switch (year) {

                    case 1:return R.array.first_year;
                    case 2:return R.array.sec_elec;
                    case 3:return R.array.third_elec;
                    default:return R.array.forth_year;
                }     }
            case 4:{
                switch (year) {

                    case 1:return R.array.first_year;
                    case 2:return R.array.sec_entc;
                    case 3:return R.array.third_entc;
                    default:return R.array.forth_year;
                }     }
            case 5:{
                switch (year) {

                    case 1:return R.array.first_year;
                    case 2:return R.array.sec_mech;
                    case 3:return R.array.third_mech;
                    default:return R.array.forth_year;
                }     }
                default:return R.array.forth_year;

        }
    }
}

