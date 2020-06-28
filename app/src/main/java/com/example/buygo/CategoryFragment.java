package com.example.buygo;


import android.content.Context;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;




/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    interface CategotyFragmentListener{
        void saveCategoryButtonPressed(String branch,String year,String subject);
    }
    private CategotyFragmentListener listener;


    public CategoryFragment() {
        // Required empty public constructor
    }

    private Spinner mBranchSpinner;
    private Spinner mYearSpinner;
    private Spinner mSubjectSpinner;
    private String mBranch;
    private String mYear;
    private String mSubject;
    private int branch=0;
    private int year=0;
    private Button backButton;
    private Button nextButton;

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            view=inflater.inflate(R.layout.fragment_category, container, false);
        mBranchSpinner = view.findViewById(R.id.spinnerBranchCategory);
        mYearSpinner = view.findViewById(R.id.spinnerYearCategory);
        mSubjectSpinner = view.findViewById(R.id.spinnerSubjectCategory);
        backButton=view.findViewById(R.id.backNameAndPic);
        nextButton=view.findViewById(R.id.nextComment);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to go back to name and photo
                FragmentTransaction t = getFragmentManager().beginTransaction();
                Fragment mFrag = new NameAndPic();
                t.replace(R.id.fragment_layout, mFrag);
                t.commit();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to go next to comment
                listener.saveCategoryButtonPressed(mBranch,mYear,mSubject);
                FragmentTransaction t = getFragmentManager().beginTransaction();
                Fragment mFrag = new CommentFragment();
                t.replace(R.id.fragment_layout, mFrag);
                t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                t.addToBackStack(null);
                t.commit();
            }
        });

        mBranchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBranch = parent.getItemAtPosition(position).toString();
                branch=position;
                addListToSubject();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                mBranch =parent.getItemAtPosition(0).toString();
            }
        });
        mYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mYear = parent.getItemAtPosition(position).toString();
                year=position;
                addListToSubject();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                mYear=parent.getItemAtPosition(0).toString();
            }
        });
        mSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubject = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                mSubject=parent.getItemAtPosition(0).toString();
            }
        });




        // Inflate the layout for this fragment
        return view;

    }

    private void addListToSubject()
    {
        Log.d("FilterDialogFragment",""+whichListToAdd());
        ArrayList<String> subjects = new ArrayList<>(Arrays.asList(getResources().getStringArray(whichListToAdd())));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,subjects);
        mSubjectSpinner.setAdapter(arrayAdapter);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CategotyFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CommentFragmentListener");
        }
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
            case 6:
            case 7:
            case 8:{
                switch (year) {
                    case 1:
                    case 2:
                    case 3:
                    default:return R.array.forth_year;
                }     }
            default:return R.array.forth_year;

        }
    }

}
