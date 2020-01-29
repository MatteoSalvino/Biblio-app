package com.example.biblio.helpers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.biblio.R;

public class XFragment extends Fragment {
    public final String TAG;
    protected final LogHelper logger;

    public XFragment(Class clazz) {
        this.logger = new LogHelper(clazz);
        this.TAG = clazz.getSimpleName();
    }

    protected FragmentManager getXFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }

    protected void popBackStackImmediate() {
        getXFragmentManager().popBackStackImmediate();
    }

    protected void moveTo(Fragment fragment, String tag) {
        startTransaction(fragment, tag, true);
    }

    protected void moveTo(XFragment fragment) {
        startTransaction(fragment, fragment.TAG, true);
    }

    protected void replaceWith(XFragment fragment) {
        startTransaction(fragment, fragment.TAG, false);
    }

    private void startTransaction(Fragment fragment, String tag, boolean addToBackStack) {
        int CONTAINER_ID = R.id.fragment_container;
        FragmentTransaction transaction = getXFragmentManager()
                .beginTransaction()
                .replace(CONTAINER_ID, fragment, tag);
        if (addToBackStack)
            transaction = transaction.addToBackStack(null);
        transaction.commit();
    }

}
