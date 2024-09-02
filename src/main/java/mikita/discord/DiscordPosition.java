package mikita.discord;

import mikita.BatchList;
import mikita.exception.DiscordEntityNotFoundException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiscordPosition {

    public static final int PAGE_SIZE = 5;

    private DiscordService service;
    private Guild currentGuild;
    private TextChannel currentTextChannel;
    private VoiceChannel currentVoiceChannel;

    private boolean listeningMessages;

    public DiscordPosition(DiscordService service) {
        this.service = service;
        listeningMessages = true;
    }

    public synchronized DiscordService getService() {
        return service;
    }

    public synchronized Guild getCurrentGuild() {
        return currentGuild;
    }

    public synchronized TextChannel getCurrentTextChannel() {
        return currentTextChannel;
    }

    public synchronized VoiceChannel getCurrentVoiceChannel() {
        return currentVoiceChannel;
    }

    public synchronized boolean isListeningMessages() {
        return listeningMessages;
    }

    public synchronized void setListeningMessages(boolean listeningMessages) {
        this.listeningMessages = listeningMessages;
    }

    public synchronized List<Member> getMembers() {
        return getMembers(false);
    }

    public synchronized List<Member> getMembers(boolean onlineOnly) {
        if (!isInGuild()) {
            return null;
        }
        List<Member> members = currentGuild.getMembers();
        if (!onlineOnly) {
            return members;
        }
        return members.stream().filter(service::isOnline).collect(Collectors.toList());
    }

    public synchronized BatchList<Member> listMembers() {
        return listMembers(false);
    }

    public synchronized BatchList<Member> listMembers(boolean onlineOnly) {
        return new BatchList<>(PAGE_SIZE, getMembers(onlineOnly));
    }

    public synchronized List<TextChannel> getTextChannels() {
        return getElements(Guild::getTextChannels);
    }

    public synchronized BatchList<TextChannel> listTextChannels() {
        return listElements(Guild::getTextChannels);
    }

    public synchronized List<VoiceChannel> getVoiceChannels() {
        return getElements(Guild::getVoiceChannels);
    }

    public synchronized BatchList<VoiceChannel> listVoiceChannels() {
        return listElements(Guild::getVoiceChannels);
    }

    public synchronized void enterGuild(long id) throws DiscordEntityNotFoundException {
        enterGuild(service.getGuild(id));
    }

    public synchronized void enterGuild(Guild guild) throws DiscordEntityNotFoundException {
        if (guild == null) {
            throw new DiscordEntityNotFoundException("Null guild");
        }
        if (!service.getGuilds().contains(guild)) {
            throw new DiscordEntityNotFoundException("Guild not found");
        }
        if (!guild.equals(currentGuild)) {
            leaveTextChannel();
            leaveVoiceChannel();
        }
        currentGuild = guild;
    }

    public synchronized boolean enterTextChannel(long id) throws DiscordEntityNotFoundException {
        if (!isInGuild()) {
            return false;
        }
        return enterTextChannel(currentGuild.getTextChannelById(id));
    }

    public synchronized boolean enterTextChannel(TextChannel channel) throws DiscordEntityNotFoundException {
        if (!isInGuild()) {
            return false;
        }
        if (!currentGuild.getTextChannels().contains(channel)) {
            throw new DiscordEntityNotFoundException("TextChannel not found");
        }
        currentTextChannel = channel;
        return true;
    }

    public synchronized boolean enterVoiceChannel(long id) throws DiscordEntityNotFoundException {
        if (!isInGuild()) {
            return false;
        }
        return enterVoiceChannel(currentGuild.getVoiceChannelById(id));
    }

    public synchronized boolean enterVoiceChannel(VoiceChannel channel) throws DiscordEntityNotFoundException {
        if (!isInGuild()) {
            return false;
        }
        if (!currentGuild.getVoiceChannels().contains(channel)) {
            throw new DiscordEntityNotFoundException("VoiceChannel not found");
        }
        currentVoiceChannel = channel;
        return true;
    }

    public synchronized boolean isInGuild(){
        return currentGuild != null;
    }

    public synchronized boolean isInTextChannel() {
        return currentTextChannel != null;
    }

    public synchronized boolean isInVoiceChannel() {
        return currentVoiceChannel != null;
    }

    public synchronized void leaveGuild() {
        currentGuild = null;
        leaveTextChannel();
        leaveVoiceChannel();
    }

    public synchronized void leaveTextChannel() {
        currentTextChannel = null;
    }

    public synchronized void leaveVoiceChannel() {
        currentVoiceChannel = null;
    }

    private <T> List<T> getElements(Function<Guild, List<T>> listFunction) {
        if (!isInGuild()) {
            return null;
        }
        return listFunction.apply(currentGuild);
    }

    private <T> BatchList<T> listElements(Function<Guild, List<T>> listFunction) {
        if (!isInGuild()) {
            return null;
        }
        return listFunction.apply(currentGuild).stream().collect(BatchList.collector(PAGE_SIZE));
    }

}
