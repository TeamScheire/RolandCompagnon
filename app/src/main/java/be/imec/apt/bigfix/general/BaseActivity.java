package be.imec.apt.bigfix.general;

import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder butterKnife;

    protected void useButterKnife() {
        this.butterKnife = ButterKnife.bind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (butterKnife != null) {
            butterKnife.unbind();
        }
    }
}
