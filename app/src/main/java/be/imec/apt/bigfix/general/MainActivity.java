package be.imec.apt.bigfix.general;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.calendar.CalendarFragment;
import be.imec.apt.bigfix.database.entities.Event;
import be.imec.apt.bigfix.gallery.GalleryFragment;
import be.imec.apt.bigfix.info.InfoFragment;
import be.imec.apt.bigfix.info.LocalizerDialogFragment;
import be.imec.apt.bigfix.tasks.detail.TaskDetailFragment;
import be.imec.apt.bigfix.tasks.overview.TasksFragment;
import be.imec.apt.bigfix.tasks.running.TaskRunningActivity;
import be.imec.apt.bigfix.utils.CustomTypefaceSpan;
import be.imec.apt.bigfix.utils.TimeTickBroadcastReceiver;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements MainPresenter.MainView {

    private static final int MAX_NOTIFICATION_SOUNDS = 10; // ~ 2 sec sound for 10 secs

    @BindView(R.id.textview_user_cta)
    TextView textViewUserCTA;

    @BindView(R.id.imageview_user_image)
    ImageView imageViewUser;

    @Inject
    MainPresenter presenter;

    private BroadcastReceiver timeTickBroadcastReceiver;

    private Unbinder butterKnife;

    private int notificationSoundCounter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigFixApplication.appComponent.inject(this);

        setContentView(R.layout.activity_main);

        this.butterKnife = ButterKnife.bind(this);

        setupFragments();
        setupPresenter();
    }

    private void setupPresenter() {
        presenter.attachView(this);

        presenter.start();
    }

    private void setupFragments() {
        showFragment(R.id.layout_calendar, CalendarFragment.class);
        showFragment(R.id.layout_tasks, TasksFragment.class);
        showFragment(R.id.layout_info, InfoFragment.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        timeTickBroadcastReceiver = new TimeTickBroadcastReceiver();
        registerReceiver(timeTickBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(timeTickBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.onScreenVisible();
    }

    @Override
    protected void onPause() {
        super.onPause();

        presenter.onScreenInvisible();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.stop();

        butterKnife.unbind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TaskRunningActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            showGallery();
        }
    }

    private void showFragment(@IdRes int layoutId, Class<? extends Fragment> clazz, final Bundle bundle) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(layoutId);
        if (fragment == null || fragment.getClass() != clazz) {
            try {
                fragment = clazz.newInstance();
                if (bundle != null) {
                    fragment.setArguments(bundle);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                // Ignore
                return;
            }
        } else {
            if (fragment.getArguments() != null && bundle != null) {
                fragment.getArguments().putAll(bundle);
            } else {
                fragment.setArguments(bundle);
            }
        }

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            fragmentTransaction.replace(layoutId, fragment);
        }
        fragmentTransaction.commit();
    }

    private void showFragment(@IdRes int layoutId, Class<? extends Fragment> clazz) {
        showFragment(layoutId, clazz, null);
    }

    @OnClick(R.id.imageview_icon)
    public void onImecLogoClicked(final View view) {
        presenter.onImecLogoClicked();
    }

    @OnClick(R.id.layout_user_cta)
    public void onUserCTAClicked() {
        presenter.onUserCTAClicked();
    }

    //region MainPresenter.MainView

    @Override
    public void playNotificationSound() {
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.notification_sound);
        notificationSoundCounter = 0;
        mediaPlayer.setOnCompletionListener(mp -> {
            if (notificationSoundCounter < MAX_NOTIFICATION_SOUNDS) {
                mediaPlayer.start();
                notificationSoundCounter++;
            }
        });
        mediaPlayer.start();
    }

    @Override
    public void showTaskDetail(Event event) {
        final Bundle bundle = TaskDetailFragment.getBundle(event);
        showFragment(R.id.layout_detail, TaskDetailFragment.class, bundle);
    }

    @Override
    public void showGallery() {
        showFragment(R.id.layout_detail, GalleryFragment.class);
    }

    @Override
    public void updateUser(User user) {
        final String ctaString = getString(R.string.info_user_cta).replace("{{name}}", user.getName());

        final String click = getString(R.string.click_here);
        final String result = ctaString.replace("{{click}}", click);
        final SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(result);
        final Typeface typeface = ResourcesCompat.getFont(this, R.font.raleway_bold);
        spannableStringBuilder.setSpan(new CustomTypefaceSpan(null, typeface), 0, click.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        textViewUserCTA.setText(spannableStringBuilder);

        Glide.with(this)
                .load(user.getAvatar())
                .into(imageViewUser);
    }

    @Override
    public void openLocalizer() {
        final DialogFragment dialogFragment = new LocalizerDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), LocalizerDialogFragment.class.getSimpleName());
    }


    //endregion
}
