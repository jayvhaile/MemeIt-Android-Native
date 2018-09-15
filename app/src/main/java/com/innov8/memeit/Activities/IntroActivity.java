package com.innov8.memeit.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.paolorotolo.appintro.AppIntro;
import com.innov8.memeit.R;

public class IntroActivity extends AppIntro {
    public static final String NUMBER = "number";


    Bundle[] bundles = new Bundle[]{
    createBundle(1),
    createBundle(2),
    createBundle(3),
    createBundle(4),
    createBundle(5)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for(Bundle bundle : bundles){
            Fragment fragment = new IntroFragment();
            fragment.setArguments(bundle);
            addSlide(fragment);
        }

    }
    public static class IntroFragment extends Fragment{

        public IntroFragment(){}


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view;
            switch (getArguments().getInt(NUMBER)){
                case 1: view = inflater.inflate(R.layout.slide_1, container, false);
                    YoYo.with(Techniques.FadeInDown)
                            .duration(700)
                            .delay(500)
                            .playOn(view.findViewById(R.id.memeitlogo));
                break;
                case 2: view = inflater.inflate(R.layout.slide_2, container, false);

                break;
                case 3: view = inflater.inflate(R.layout.slide_3, container, false);

                break;
                case 4: view = inflater.inflate(R.layout.slide_4, container, false);

                break;
                case 5: view = inflater.inflate(R.layout.slide_5, container, false);

                break;
                default:view = inflater.inflate(R.layout.slide_1, container, false);
            }
            return view;
        }
    }

    private Bundle createBundle(int i){
        Bundle bundle = new Bundle();
        bundle.putInt(NUMBER,i);
        return bundle;
    }
}
