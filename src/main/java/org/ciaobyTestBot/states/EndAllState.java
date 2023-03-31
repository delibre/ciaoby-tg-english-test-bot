package org.ciaobyTestBot.states;

import org.ciaobyTestBot.bot.ServiceCallback;
import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.ciaobyTestBot.states.statesservice.AbstractState;

public class EndAllState extends AbstractState {
    public EndAllState(ServiceCallback serviceCallback) {
        super(serviceCallback, States.END_ALL);
    }

    @Override
    public void apply(String textMsg, UserInfoDTO user) {
        sendText(user.getChatId(),
                "Извините, не роспознал Вашу команду.\n" +
                        "Если хотите пройти тест заново - нажмите кнопку \"Начать тестирование\"\uD83E\uDD17"
        );
    }
}
