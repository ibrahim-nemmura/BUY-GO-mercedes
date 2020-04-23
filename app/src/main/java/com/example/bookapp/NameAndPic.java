package com.example.bookapp;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class NameAndPic extends Fragment {

    interface NameFragmentListener{
        void OnNameNextButtonPressed(String name,Uri imageUri);
        void OnNameCancelButtonPressed();
    }
    private NameFragmentListener listener;
    public NameAndPic() {
        // Required empty public constructor
    }

    private ImageView pic;
    private EditText nameText;
    private Button cancelButton;
    private Button nextButton;
    private Uri imageUri;

    String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_name_and_pic, container, false);
        pic=view.findViewById(R.id.pic);
        nameText=view.findViewById(R.id.userName);
        cancelButton=view.findViewById(R.id.cancelName);
        nextButton=view.findViewById(R.id.saveName);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,1);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to cancel and go back
                listener.OnNameCancelButtonPressed();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to go to next fragment
                name=nameText.getText().toString();
                listener.OnNameNextButtonPressed(name,imageUri);
                FragmentTransaction t = getFragmentManager().beginTransaction();
                Fragment mFrag = new CategoryFragment();
                t.replace(R.id.fragment_layout, mFrag);
                t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                t.addToBackStack(null);
                t.commit();

            }
        });
        
        // Inflate the layout for this fragment
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); // we have the actual path to the image
                pic.setImageURI(imageUri);//show image

            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NameFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CommentFragmentListener");
        }
    }


}
