package com.mathias.apps.tasktracker.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mathias.apps.tasktracker.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditTaskActivityFragment extends Fragment {

    public EditTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_task, container, false);
    }
}
