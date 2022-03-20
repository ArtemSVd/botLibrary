package org.example.botLibrary.bot;

import lombok.SneakyThrows;
import org.example.botLibrary.bot.pojo.BotConfiguration;
import org.example.botLibrary.bot.pojo.Command;
import org.example.botLibrary.bot.pojo.CommandResponse;
import org.example.botLibrary.bot.pojo.CurrentState;
import org.example.botLibrary.bot.pojo.UpdateParams;
import org.example.botLibrary.bot.service.DataService;
import org.example.botLibrary.bot.service.MapDataService;
import org.example.botLibrary.bot.util.TelegramUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.Function;

public abstract class TelegramBot extends LongPoolingBot {
    private BotConfiguration configuration;
    private DataService dataService;

    public void start() {
        if (configuration == null) {
            configuration = new BotConfiguration();
        }
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
        configuration.addCommand(command);
        return command;
    }

    public void addNonCommandScript(ScriptBuilder<?> scriptBuilder) {
        this.configuration.addNonCommandScript(scriptBuilder);
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(UpdateParams updateParams) {
        try {
            CurrentState currentState = dataService.getCurrentState(updateParams.getChatId());
            String command = currentState.getLastCommand() != null && currentState.getState() != null
                    ? currentState.getLastCommand() : updateParams.getCommand();
            if (command == null) {
                this.execute(processNonCommandUpdate(updateParams));
            } else {
                this.execute(processCommandUpdate(command, updateParams));
            }
        } catch (Exception exception) {
            this.execute(TelegramUtil.sendMessage(updateParams.getChatId(),
                    "Возникла непредвиденная ошибка."));
            // TODO: продумать логирование ошибок
        }
    }

    private Object processCommandUpdate(String command, UpdateParams updateParams) {
        String dataKey = updateParams.getCommand() + "_" + updateParams.getChatId();
        updateParams.setEntity(dataService.get(dataKey));
        updateParams.setCurrentState(dataService.getCurrentState(updateParams.getChatId()));
        CommandResponse commandResponse = configuration.applyAction(command, updateParams);
        this.saveData(updateParams, commandResponse, command, dataKey);
        return commandResponse.getMessage();
    }

    private Object processNonCommandUpdate(UpdateParams updateParams) {
        if (configuration.getNonCommandScript() == null) {
            return nonCommandUpdate(updateParams);
        }
        String dataKey = "nonCommand" + "_" + updateParams.getChatId();
        updateParams.setEntity(dataService.get(dataKey));
        updateParams.setCurrentState(dataService.getCurrentState(updateParams.getChatId()));
        CommandResponse commandResponse = configuration.applyNonCommandAction(updateParams);
        this.saveData(updateParams, commandResponse, null, dataKey);
        return commandResponse.getMessage();
    }

    private void saveData(UpdateParams updateParams, CommandResponse commandResponse,
                          String command, String dataKey) {
        dataService.updateCurrentState(updateParams.getChatId(), CurrentState.builder()
                .lastCommand(command)
                .state(commandResponse.getNextState())
                .build());

        if (commandResponse.getEntityForSave() != null) {
            dataService.put(dataKey,
                    commandResponse.getEntityForSave());
        } else {
            dataService.remove(dataKey);
        }
    }

    private void execute(Object object) throws TelegramApiException {
        if (object instanceof SendMessage) {
            this.execute((SendMessage) object);
        } else if (object instanceof EditMessageText) {
            this.execute((EditMessageText) object);
        } else if (object instanceof SendDocument) {
            this.execute((SendDocument) object);
        }
    }

}
