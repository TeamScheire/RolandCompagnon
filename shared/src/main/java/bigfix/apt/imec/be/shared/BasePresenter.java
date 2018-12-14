package bigfix.apt.imec.be.shared;

import android.support.annotation.CallSuper;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BasePresenter<T> {

    protected T view;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public final void attachView(T view){
        this.view = view;
    }

    @CallSuper
    public void stop() {
        disposables.dispose();
    }

    protected void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }
}
