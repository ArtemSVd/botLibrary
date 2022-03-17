package org.example.botLibrary.bot;

import org.example.botLibrary.bot.pojo.BotConfiguration;
import org.example.botLibrary.bot.pojo.Command;
import org.example.botLibrary.bot.pojo.CommandResponse;
import org.example.botLibrary.bot.pojo.CurrentState;
import org.example.botLibrary.bot.pojo.HandleUpdateParams;
import org.example.botLibrary.bot.pojo.UpdateParams;
import org.example.botLibrary.bot.service.DataService;
import org.example.botLibrary.bot.service.MapDataService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.Function;

public abstract class TelegramBot extends LongPoolingBot {
    private BotConfiguration configuration;
    public static DataService dataService;

    public void start() {
        initialize();
        validateConfig();
        dataService = this.getDataService();
    }

    private void validateConfig() {
        if (configuration == null || configuration.getCommands() == null || configuration.getCommands().isEmpty()) {
            return;
        }

        boolean hasNotActionCommand = configuration.getCommands().stream()
                .anyMatch(command -> command.getAction() == null && command.getScript() == null);
        if (hasNotActionCommand) {
            throw new IllegalArgumentException("У одной из команд не задано действие или сценарий");
        }
    }

    public abstract void initialize();
    public abstract Object nonCommandUpdate(UpdateParams updateParams);

    public DataService getDataService() {
        return MapDataService.getInstance();
    }

    public Command addCommand(String name) {
        return addCommand(name, null);
    }

    public Command addCommand(String name, Function<UpdateParams, CommandResponse> defaultAction) {
        Command command = Command.builder()
                .name(name)
                .action(defaultAction)
                .build();
        if (configuration == null) {
            configuration = new BotConfiguration();
        }
        configuration.addCommand(command);
        return command;
    }

    @Override
    public void onUpdateReceived(HandleUpdateParams params) {
        try {
            UpdateParams updateParams = UpdateParams.builder()
                    .arguments(params.getArguments())
                    .chatId(String.valueOf(params.getChatId()))
                    .isCallback(params.isCallback())
                    .messageId(params.getMessageId())
                    .build();

            CurrentState currentState = dataService.getCurrentState(updateParams.getChatId());
            String command = currentState.getLastCommand() != null && currentState.getState() != null
                    ? currentState.getLastCommand() : params.getCommand();
            if (command == null) {
                this.execute(nonCommandUpdate(updateParams));
                dataService.removeCurrentState(updateParams.getChatId());
            } else {
                this.execute(processCommandUpdate(command, updateParams));
            }
        } catch (TelegramApiException ignored) {
            // TODO: продумать логирование ошибок
        }
    }

    private Object processCommandUpdate(String command, UpdateParams updateParams) {
        CommandResponse commandResponse = configuration.applyAction(command, updateParams);

        dataService.updateCurrentState(updateParams.getChatId(), CurrentState.builder()
                .lastCommand(command)
                .state(commandResponse.getNextState())
                .build());

        return commandResponse.getMessage();
    }

    private void execute(Object object) throws TelegramApiException {
        if (object instanceof SendMessage) {
            this.execute((SendMessage) object);
        } else if (object instanceof EditMessageText) {
            this.execute((EditMessageText) object);
        }
    }

}
