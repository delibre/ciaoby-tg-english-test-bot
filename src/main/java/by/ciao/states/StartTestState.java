package by.ciao.states;

import by.ciao.bot.ServiceCallback;
import by.ciao.enums.StateEnum;
import by.ciao.states.statesservice.AbstractState;
import by.ciao.states.statesservice.UserHandlerState;
import by.ciao.states.statesservice.UserMessageHandlerState;
import by.ciao.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalTime;
import java.util.Objects;
import java.util.TimerTask;

public class StartTestState extends AbstractState implements UserMessageHandlerState {

    @Value("test_duration")
    private String testDuration;
    private static final Logger log = LoggerFactory.getLogger(StartTestState.class);

    public StartTestState(ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final User user) throws Exception {
        if (Objects.equals(textMsg, getBotResponses().startTestButton())) {
            user.setState(StateEnum.SEND_QUESTION);
            user.clearTest();
            setTimer(user);
            user.getTestState().setStartTime(LocalTime.now());

            UserHandlerState state = new SendQuestionState(getServiceCallback());
            state.apply(user);
        }
        else {
            sendStartButton(user);
        }
    }

    private void setTimer(User user) {
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    sendText(user.getChatId(), "Время вышло. Результаты вашего теста ниже");
                    changeStateToTestFinished(user);
                } catch (Exception e) {
                    log.error(getLoggerMessages().setTimerException(), e);
                }
                user.getTestState().getTimer().cancel();
            }
        };
        user.getTestState().getTimer().schedule(task, Long.parseLong(testDuration) * 60 * 1000);
    }

}
