package com.example.yacinehc.mplrss.utils;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yacinehc.mplrss.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SimpleDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimpleDialogFragment extends DialogFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TITLE = "title";
    private static final String QUESTION = "question";

    private String title;
    private String question;

    private TextView titleTextView;
    private TextView questionTextView;
    private EditText responseEditText;
    private Button addButton;
    private Button cancelButton;

    private OnDialogFragmentInteractionListener onDialogFragmentInteractionListener;


    public SimpleDialogFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @return A new instance of fragment SimpleDialogFragment.
     */
    public static SimpleDialogFragment newInstance(String title, String question) {
        SimpleDialogFragment fragment = new SimpleDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.title = getArguments().getString(TITLE);
            this.question = getArguments().getString(QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_dialog, container, false);
        titleTextView = view.findViewById(R.id.dialogTitle);
        questionTextView = view.findViewById(R.id.questionDialog);
        responseEditText = view.findViewById(R.id.dialogEditText);
        addButton = view.findViewById(R.id.addDialogButton);
        cancelButton = view.findViewById(R.id.cancelDialogButton);

        titleTextView.setText(this.title);
        questionTextView.setText(this.question);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogFragmentInteractionListener != null) {
                    String adresse = responseEditText.getText().toString();
                    if (!adresse.equals("")) {
                        Uri uri = Uri.parse(adresse);
                        onDialogFragmentInteractionListener.onDialogFragmentAdd(uri);
                        dismiss();
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDialogFragmentInteractionListener) {
            onDialogFragmentInteractionListener = (OnDialogFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDialogFragmentInteractionListener");
        }
    }

    public interface OnDialogFragmentInteractionListener{
        void onDialogFragmentAdd(Uri uri);
    }
}
