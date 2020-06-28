package com.example.buygo;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class PaymentFragment extends Fragment {

    private EditText upi;
    private Button saveUpi;
    private String upiId;

    private OnSaveUpiButtonPressedListener mListener;

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        upi = view.findViewById(R.id.upiId);
        saveUpi = view.findViewById(R.id.saveUPI);
        saveUpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upiId=upi.getText().toString().trim();
                String reg="^[\\w\\.\\-_]{3,}@[a-zA-Z]{3,}";
                if(!TextUtils.isEmpty(upiId)){
                    if (upiId.matches(reg)){
                        upi.setEnabled(false);
                        mListener.onSaveUpiButtonPressed(upiId);
                    }else
                    {
                        upi.setError("Invalid UPI");
                    }
                }
                else
                    upi.setError("Empty");
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSaveUpiButtonPressedListener) {
            mListener = (OnSaveUpiButtonPressedListener) context;
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
    public interface OnSaveUpiButtonPressedListener {
        // TODO: Update argument type and name
        void onSaveUpiButtonPressed(String upiId);
    }
}
