package mikita.discord.command;

import mikita.command.AbstractCommand;
import mikita.command.CommandEntry;
import mikita.command.CommandOutput;
import mikita.discord.DiscordPosition;
import mikita.discord.DiscordService;
import mikita.message.Printer;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemberCommand extends AbstractCommand {

    private DiscordService service;
    private DiscordPosition position;

    public MemberCommand(CommandOutput output, DiscordService service) {
        super(output);
        this.service = service;
        this.position = service.getPosition();
    }

    @Override
    public Map<String, String> helpMap() {
        Map<String, String> helpMap = new HashMap<>();
        helpMap.put("list", "Lista os membros online do servidor atual");
        helpMap.put("list <online apenas>", "Lista a primeira página dos membros do servidor atual");
        helpMap.put("list <página>", "Lista a página n dos membros online do servidor atual");
        helpMap.put("list <página> <online apenas>", "Lista a página n dos membros do servidor atual");
        return helpMap;
    }

    @CommandEntry("list")
    public void list() {
        list(1, true);
    }

    @CommandEntry("list")
    public void list(boolean onlineOnly) {
        list(1, onlineOnly);
    }

    @CommandEntry("list")
    public void list(int page) {
        list(page, true);
    }

    @CommandEntry("list")
    public void list(int page, boolean onlineOnly) {
        List<Member> members = position.listMembers(onlineOnly);
        if(members == null) {
            output.sendMessage("Você não está numa guilda");
            return;
        }
        Printer.listPageable(output, page, members, "membros "
                + (onlineOnly ? "online " : "") + "do servidor atual", member -> {
            long id = member.getIdLong();
            String name = DiscordService.memberName(member);
            return name + " (" + id + ")";
        });
    }

    @Override
    public String getName() {
        return "member";
    }

}
