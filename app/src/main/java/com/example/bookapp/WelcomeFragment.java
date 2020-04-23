package com.example.bookapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeFragment.OnWelcomeFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WelcomeFragment extends Fragment {


    private TextView welcomeMessage;
    private TextView welcomeNumber;
    private Button letsGo;
    private OnWelcomeFragmentInteractionListener mListener;

    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_welcome, container, false);
        // Inflate the layout for this fragment

        welcomeMessage=view.findViewById(R.id.welcomeMessage);
        welcomeNumber=view.findViewById(R.id.welcomeNumber);
        letsGo=view.findViewById(R.id.letsGoButton);
        if (getArguments()!=null) {
            welcomeMessage.setText(TextUtils.concat("Welcome ", getArguments().getString("name")));
            welcomeNumber.setText(getArguments().getString("contact"));
        }

        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLetsGoButtonPressed();
            }
        });
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWelcomeFragmentInteractionListener) {
            mListener = (OnWelcomeFragmentInteractionListener) context;
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
    public interface OnWelcomeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLetsGoButtonPressed();
    }
}
