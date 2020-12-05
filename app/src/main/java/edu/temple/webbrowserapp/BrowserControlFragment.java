package edu.temple.webbrowserapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.Serializable;

public class BrowserControlFragment extends Fragment implements Serializable{

    private BrowserControlInterface browserActivity;

    interface BrowserControlInterface
    {
        void newPage();
        void savePage();
        void launchBookmark();
    }

    public BrowserControlFragment()
    {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof BrowserControlInterface) {
            browserActivity = (BrowserControlInterface) context;
        } else {
            throw new RuntimeException("Need to implement methods");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browser_control, container, false);
        ImageButton newPageButton = view.findViewById(R.id.newPageButton);
        ImageButton savePageButton = view.findViewById(R.id.savePageButton);
        ImageButton bookmarkButton = view.findViewById(R.id.bookmarkButton);

        newPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserActivity.newPage();
            }
        });

        savePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserActivity.savePage();
            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserActivity.launchBookmark();
            }
        });

        return view;
    }
}