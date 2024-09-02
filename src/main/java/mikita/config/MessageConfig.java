package mikita.config;

import mikita.command.CommandOutput;
import mikita.discord.DiscordMessageReceivedOutput;
import mikita.message.GlobalOutput;
import mikita.message.StdMessageable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {

    @Bean
    public StdMessageable stdMessageable() {
        return StdMessageable.getInstance();
    }

    @Bean
    public CommandOutput commandOutput() {
        return new CommandOutput(StdMessageable.getInstance());
    }

    @Bean
    public DiscordMessageReceivedOutput discordMessageReceivedOutput() {
        return new DiscordMessageReceivedOutput(StdMessageable.getInstance());
    }

    @Bean
    public GlobalOutput globalOutput(CommandOutput commandOutput, DiscordMessageReceivedOutput discordMessageReceivedOutput) {
        return new GlobalOutput(commandOutput, discordMessageReceivedOutput);
    }

}
