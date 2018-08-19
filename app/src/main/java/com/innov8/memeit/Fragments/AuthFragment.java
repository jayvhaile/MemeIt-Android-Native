package com.innov8.memeit.Fragments;


import androidx.fragment.app.Fragment;

import com.innov8.memeit.Activities.AuthActivity;
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton;

public class AuthFragment extends Fragment {

    public AuthFragment() {
    }

    protected CircularProgressButton actionButton;

    protected AuthActivity getAuthActivity(){
        return (AuthActivity) getActivity();
    }

    protected void setLoading(boolean loading){
        if (loading){
            actionButton.startAnimation();
        }
        else {
            actionButton.revertAnimation();
        }

    }
}
