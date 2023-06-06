package com.github.craxlor.discordbot.util.reply;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public class LogHelper {

  @SuppressWarnings("null")
  public static String logCommand(GenericCommandInteractionEvent event, Status status, String statusDetail) {
    String guild = "";
    if (event.getGuild() != null)
      guild = event.getGuild().getName();
    else
      guild = "private channel";

    return """
        %s
          Guild: %s
        Command: %s
         Author: %s
         Detail: %s""".formatted(status.toString(), guild, event.getFullCommandName(),
        event.getUser().getName(),
        statusDetail);
  }


  public static String logAutoroom(String guildName, String channelname, String action, String channeltype) {
    return """
            %s
              Guild: %s
        Channeltype: %s
        Channelname: %s""".formatted(action, guildName, channeltype, channelname);
  }

  public static String logMusicPermissonError(String guildName, String errortype, String errorDetail) {
    return """
        ERROR
         Guild: %s
         Error: %s
        Detail: %s""".formatted(guildName, errortype, errorDetail);
  }

}
