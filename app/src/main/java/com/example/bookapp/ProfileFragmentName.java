package com.example.bookapp;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragmentName.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ProfileFragmentName extends Fragment {
    private EditText nameText;
    private EditText emailText;
    private EditText collegeText;
    private EditText addressText;
    private Button signUpButton;
    private Spinner profileGender;
    private EditText profilePincode, profileAge;

    private OnFragmentInteractionListener mListener;

    public ProfileFragmentName() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_fragment_name, container, false);
        // Inflate the layout for this fragment
        nameText = view.findViewById(R.id.newProfileName);
        emailText = view.findViewById(R.id.newProfileEmail);
        collegeText = view.findViewById(R.id.newProfileCollege);
        addressText = view.findViewById(R.id.newProfileAddress);
        signUpButton = view.findViewById(R.id.signupButton);
        profileGender = view.findViewById(R.id.profileGender);
        profilePincode = view.findViewById(R.id.profilePincode);
        profileAge = view.findViewById(R.id.profileAge);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        return view;
    }

    private void getData() {
        String name, email, college, address,pincode,gender;
        int age;

        if (TextUtils.isEmpty(nameText.getText().toString())) {
            nameText.setError("Empty");
            return;
        }else
            name = nameText.getText().toString();
        if (TextUtils.isEmpty(emailText.getText().toString())) {
            emailText.setError("Empty");
            return;
        }else
            email = emailText.getText().toString();
        if (TextUtils.isEmpty(collegeText.getText().toString())) {
            collegeText.setError("Empty");
            return;
        }else
            college = collegeText.getText().toString();
        if (TextUtils.isEmpty(addressText.getText().toString())) {
            addressText.setError("Empty");
            return;
        }else
            address = addressText.getText().toString();

        if (TextUtils.isEmpty(profilePincode.getText())) {
            profilePincode.setError("Empty");
            return;
        } else {
            pincode= profilePincode.getText().toString();
        }

        if (profileGender.getSelectedItemPosition() == 0) {
            gender="Male";
        } else {
            gender="Female";
        }

        if (TextUtils.isEmpty(profileAge.getText())) {
            profileAge.setError("Empty");
            return;
        } else {
            age=Integer.parseInt(profileAge.getText().toString());
        }

        if(mListener!=null){

            mListener.onSignUpPressed(name,email,college,address,pincode,age,gender);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSignUpPressed(String name, String email, String college, String address,String pincode,int age,String gender);
    }
}
