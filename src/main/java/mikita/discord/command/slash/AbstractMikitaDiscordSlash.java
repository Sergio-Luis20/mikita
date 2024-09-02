package mikita.discord.command.slash;

import mikita.discord.DiscordService;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractMikitaDiscordSlash implements MikitaDiscordSlash {

    protected final String name, description;
    protected final DiscordService discordService;

    public AbstractMikitaDiscordSlash(DiscordService discordService, String name, String description) {
        this.discordService = Objects.requireNonNull(discordService);
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
        if (!DiscordService.NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name. Must be between 1 and 32 alphanumeric " +
                    "lower case characters plus dash. Provided: " + name + " (" + name.length() + " characters)");
        }
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Empty description. Must be between 1 and 100 characters.");
        }
        if (description.length() > 100) {
            throw new IllegalArgumentException("Too long description. Must be between 1 and 100 characters. " +
                    "Length: " + description.length() + " characters.");
        }
    }

    @Override
    public String commandName() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public SlashCommandData toSlashData() {
        SlashCommandData slashData = Commands.slash(name, description);
        List<Option> options = options();
        if (!options.isEmpty()) {
            slashData.addOptions(options.stream().map(Option::toOptionData).toArray(OptionData[]::new));
        }
        return slashData;
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        Map<String, Option> options = options().stream().collect(Collectors.toMap(Option::name, option -> option));
        AutoCompleteQuery focusedOption = event.getFocusedOption();
        String focusedOptionName = focusedOption.getName();
        if (options.containsKey(focusedOptionName)) {
            Option option = options.get(focusedOptionName);
            Choice[] choices = option.choices();
            if (choices != null) {
                String focusedOptionValue = focusedOption.getValue();
                List<Choice> choicesList = Arrays.stream(choices)
                        .filter(choice -> choice.getName().startsWith(focusedOptionValue))
                        .toList();
                event.replyChoices(choicesList).queue(null, throwable -> discordService.getLog()
                        .sendErrorMessage("Não foi possível enviar escolhas para o autocomplete do comando \"" +
                                event.getName() + "\" na opção \"" + focusedOptionName + "\".", throwable));
            }
        }
    }
}
