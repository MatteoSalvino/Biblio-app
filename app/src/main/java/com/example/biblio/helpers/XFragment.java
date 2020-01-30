package com.example.biblio.helpers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.biblio.R;

import org.jetbrains.annotations.NotNull;

/**
 * A very simple extension of androidx Fragment, providing a default logger and implementing some
 * common patterns (e.g. fragment transactions).
 *
 * @see LogHelper
 */
public class XFragment extends Fragment {
    public final String TAG;
    protected final LogHelper logger;

    public XFragment(Class clazz) {
        this.logger = new LogHelper(clazz);
        this.TAG = clazz.getSimpleName();
    }

    protected void popBackStackImmediate() {
        getXFragmentManager().popBackStackImmediate();
    }

    protected void moveTo(XFragment fragment) {
        startTransaction(fragment, true);
    }

    protected void replaceWith(XFragment fragment) {
        startTransaction(fragment, false);
    }

    private void startTransaction(XFragment fragment, boolean addToBackStack) {
        int CONTAINER_ID = R.id.fragment_container;
        FragmentTransaction transaction = getXFragmentManager()
                .beginTransaction()
                .replace(CONTAINER_ID, fragment, fragment.TAG);
        if (addToBackStack)
            transaction = transaction.addToBackStack(null);
        transaction.commit();
    }

    @NotNull
    private FragmentManager getXFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }
}
