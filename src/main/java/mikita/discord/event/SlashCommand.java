package mikita.discord.event;

import mikita.discord.command.slash.MikitaDiscordSlash;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SlashCommand extends ListenerAdapter {

    private Map<String, MikitaDiscordSlash> commands;

    public SlashCommand(List<MikitaDiscordSlash> commands) {
        this.commands = commands.stream().collect(Collectors
                .toMap(MikitaDiscordSlash::commandName, slash -> slash));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String name = event.getName();
        if (commands.containsKey(name)) {
            event.deferReply().queue();
            commands.get(name).onCommand(event, event.getHook());
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        String name = event.getName();
        if (commands.containsKey(name)) {
            commands.get(name).onAutoComplete(event);
        }
    }

}
