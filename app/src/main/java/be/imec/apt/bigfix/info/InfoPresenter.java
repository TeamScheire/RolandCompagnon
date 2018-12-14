package be.imec.apt.bigfix.info;

import javax.inject.Inject;

import be.imec.apt.bigfix.general.User;
import be.imec.apt.bigfix.di.TimeTickBus;
import bigfix.apt.imec.be.shared.BasePresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class InfoPresenter extends BasePresenter<InfoPresenter.InfoView> {

    private final TimeTickBus timeTickBus;

    interface InfoView {
        void updateTime(long time);
    }

    @Inject
    public InfoPresenter(TimeTickBus timeTickBus) {
        this.timeTickBus = timeTickBus;
    }

    public void start() {
        updateTime(System.currentTimeMillis());

        listenForTimeTicks();
    }

    private void listenForTimeTicks() {
        final Disposable disposable = timeTickBus.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateTime);

        addDisposable(disposable);
    }

    private void updateTime(long time) {
        view.updateTime(time);
    }

}
