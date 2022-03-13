package org.example.botLibrary.bot;

import org.example.botLibrary.bot.service.DataService;
import org.example.botLibrary.bot.pojo.BotConfiguration;
import org.example.botLibrary.bot.pojo.Command;
import org.example.botLibrary.bot.pojo.HandleUpdateParams;
import org.example.botLibrary.bot.pojo.UpdateParams;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.Function;

public abstract class TelegramBot extends LongPoolingBot {
    private BotConfiguration configuration;
    private final DataService dataService;

    public TelegramBot() {
        initialize();
        validateConfig();
        this.dataService = this.getDataService();
    }

    public void validateConfig() {
        if (configuration == null || configuration.getCommands() == null || configuration.getCommands().isEmpty()) {
            return;
        }

        boolean hasNotActionCommand = configuration.getCommands().stream()
                .anyMatch(command -> command.getAction() == null || command.getScript() == null);
        if (hasNotActionCommand) {
            throw new IllegalArgumentException("У одной из команд не задано действие или сценарий");
        }
    }

    public abstract void initialize();
    public abstract Object nonCommandUpdate(UpdateParams updateParams);
    public abstract DataService getDataService();

    public Command addCommand(String name, Function<UpdateParams, Object> defaultAction) {
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
                    .chatId(String.valueOf(params.getChatId()))
                    .build();
            String lastCommand = (String) dataService.get(updateParams.getChatId());
            if (params.getCommand() == null && lastCommand == null) {
                this.execute(nonCommandUpdate(updateParams));
            } else {
                String command = lastCommand == null ? params.getCommand() : lastCommand;
                dataService.put(updateParams.getChatId(), command);
                Object result = configuration.getActionByCommandName(command, updateParams)
                        .apply(updateParams);
                this.execute(result);
            }
        } catch (TelegramApiException ignored) {

        }
    }

    private void execute(Object object) throws TelegramApiException {
        if (object instanceof SendMessage) {
            this.execute((SendMessage) object);
        }
    }

}
