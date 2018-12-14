package be.imec.apt.bigfix.wear;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, EventBusModule.class})
public interface AppComponent {
    void inject(TaskInstructionActivity activity);

    void inject(MessageReceiverService messageReceiverService);
}