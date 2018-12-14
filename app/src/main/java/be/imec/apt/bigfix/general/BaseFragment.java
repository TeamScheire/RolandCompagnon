package be.imec.apt.bigfix.general;

import android.support.v4.app.DialogFragment;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseFragment extends DialogFragment {
    private Unbinder butterKnife;

    protected void useButterKnife(final View view) {
        this.butterKnife = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (butterKnife != null) {
            butterKnife.unbind();
        }
    }
}
