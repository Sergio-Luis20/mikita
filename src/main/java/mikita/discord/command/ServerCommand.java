package mikita.discord.command;

import mikita.command.AbstractCommand;
import mikita.command.CommandEntry;
import mikita.command.CommandOutput;
import mikita.discord.DiscordPosition;
import mikita.discord.DiscordService;
import mikita.exception.DiscordEntityNotFoundException;
import mikita.message.Printer;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ServerCommand extends AbstractCommand {

    private DiscordService service;
    private DiscordPosition position;

    public ServerCommand(CommandOutput output, DiscordService service) {
        super(output);
        this.service = service;
        this.position = service.getPosition();
    }

    @Override
    public Map<String, String> helpMap() {
        Map<String, String> helpMap = new HashMap<>();
        helpMap.put("enter <id>", "Entra no servidor do id inserido");
        helpMap.put("leave", "Sai do servidor atual");
        helpMap.put("list", "Lista a primeira página dos servidores");
        helpMap.put("lista <página>", "Lista a página n dos servidores");
        return helpMap;
    }

    @CommandEntry("enter")
    public void enter(long id) {
        try {
            position.enterGuild(id);
        } catch (DiscordEntityNotFoundException e) {
            output.sendErrorMessage("Servidor não encontrado para o id " + id, e);
            return;
        }
        Guild guild = position.getCurrentGuild();
        output.sendMessage("Entrou no servidor " + guild.getName() + " (" + guild.getIdLong() + ")");
    }

    @CommandEntry("leave")
    public void leave() {
        position.leaveGuild();
        output.sendMessage("Você saiu do servidor");
    }

    @CommandEntry("list")
    public void list() {
        list(1);
    }

    @CommandEntry("list")
    public void list(int page) {
        Printer.listPageable(output, page, service.getGuilds(),
                "servidores do discord", guild -> "Servidor: " + guild.getName()
                        + ". Id: " + guild.getIdLong());
    }

    @Override
    public String getName() {
        return "server";
    }

}
