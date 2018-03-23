package mdsadabwasimcom.criminal;

import android.os.Bundle;

import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


//Creating a abstract class to add fragment in activity ,
// and avoid retyping the whole code.
public abstract class SingleFragmentActivity extends AppCompatActivity {

    //The class which implements this abstract class
    // will call this method to set the required fragment.
    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());


        //we have the fragment manager here (the support fragment manager)
        FragmentManager fm=getSupportFragmentManager();

        /*Fragment is identified by its container(if fragment is created and initialized),
        that's why we give the id of fragment container.
        if fragment is already created then activity will
         use this(in case of screen rotation , its like savedInstance bundle )
         to set the fragment, otherwise it'll create a new one.
         */

        Fragment fragment=fm.findFragmentById(R.id.fragment_container);

        if (fragment == null){
            //if fragment is not null then create a new fragment
            // and begin the transaction through fragment manager.
            fragment =createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
