package mikita.config;

import jakarta.annotation.PostConstruct;
import mikita.discord.DiscordService;
import mikita.discord.command.slash.MikitaDiscordSlash;
import mikita.exception.JDAInitializationException;
import mikita.message.StdMessageable;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DiscordConfig {

    @Value("${discord.token}")
    private String token;

    @Value("${discord.main-server-id}")
    private long mainServerId;

    @Bean
    public DiscordService discordService(StdMessageable log) throws JDAInitializationException {
        return new DiscordService(token, mainServerId, log);
    }

    @Configuration
    public static class DiscordServiceConfiguration {

        @Value("${discord.force-slash-commands-update}")
        private boolean forceSlashCommandsUpdate;

        @Autowired
        private DiscordService discordService;

        @Autowired
        private EventListener[] listeners;

        @Autowired
        private MikitaDiscordSlash[] slashCommands;

        @PostConstruct
        public void configureDiscordService() {
            discordService.registerEvents(listeners);
            discordService.updateSlashCommands(forceSlashCommandsUpdate, Arrays.stream(slashCommands)
                    .map(MikitaDiscordSlash::toSlashData).toArray(SlashCommandData[]::new));
        }

    }

}
