package com.example.buygo;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment {


    public CommentFragment() {
        // Required empty public constructor
    }

    interface CommentFragmentListener{
        void savedButtonPressed(String com1,String com2,String com3,int price);
    }
    private CommentFragmentListener listener;
    private EditText com1;
    private EditText com2;
    private EditText com3;
    private EditText priceText;

    private Button saveButton;
    private Button backButton;



    private String comment1,comment2,comment3;
    private int price;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_comment, container, false);
        // Inflate the layout for this fragment
        com1=view.findViewById(R.id.commentItem);
        com2=view.findViewById(R.id.howOldComment);
        com3=view.findViewById(R.id.otherComments);
        saveButton=view.findViewById(R.id.saveButton);
        backButton=view.findViewById(R.id.cancelButton);
        //priceText=view.findViewById(R.id.priceItem);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment1=com1.getText().toString();
                comment2=com2.getText().toString();
                comment3=com3.getText().toString();

                if(TextUtils.isEmpty(priceText.getText().toString().trim()))
                {
                    priceText.setError("Cannot be empty!");
                }
                else {
                    price=Integer.parseInt(priceText.getText().toString());
                    listener.savedButtonPressed(comment1,comment2,comment3,price);
                }}
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction t = getFragmentManager().beginTransaction();
                Fragment mFrag = new CategoryFragment();
                t.replace(R.id.fragment_layout, mFrag);
                t.commit();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CommentFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CommentFragmentListener");
        }
    }

}
