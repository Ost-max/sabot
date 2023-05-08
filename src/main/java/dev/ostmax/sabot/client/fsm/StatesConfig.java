package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.BotCommand;
import dev.ostmax.sabot.client.fsm.states.BotState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Configuration
public class StatesConfig {

    private final List<BotCommand> botCommands;
    private final List<BotState> botStates;


    public StatesConfig(List<BotCommand> botCommands, List<BotState> botStates) {
        this.botCommands = botCommands;
        this.botStates = botStates;
    }

    @Bean
    public Map<String, BotState> statesByCommandName() {
        Map<String, BotState> result = new HashMap<>();
        for(var command: botCommands) {
            result.put(command.getCommandName(), command.getState());
        }
        return result;
    }

    @Bean
    public Map<String, BotState> statesByStateId() {
        Map<String, BotState> result = new HashMap<>();
        for(var state: botStates) {
            if(state.getStateId() != null) {
                result.put(state.getStateId(), state);
            }
        }
        return result;
    }

}
