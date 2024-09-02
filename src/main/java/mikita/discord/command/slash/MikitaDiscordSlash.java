package mikita.discord.command.slash;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;

public interface MikitaDiscordSlash {

    SlashCommandData toSlashData();

    String commandName();

    String description();

    void onCommand(SlashCommandInteractionEvent event, InteractionHook hook);

    default void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
    }

    default List<Option> options() {
        return new ArrayList<>();
    }

}
