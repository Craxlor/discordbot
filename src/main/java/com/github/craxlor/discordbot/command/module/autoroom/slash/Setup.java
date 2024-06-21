package com.github.craxlor.discordbot.command.module.autoroom.slash;

import javax.annotation.Nonnull;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.entity.AutoroomTrigger;
import com.github.craxlor.discordbot.database.handler.DBAutoroomTriggerHandler;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Setup extends SlashCommand {

	private static final String CREATE_NAME = "create";
	private static final String CREATE_DESCRIPTION = "Defines an autoroom trigger, which can be used to create autorooms with specifc settings.";
	private static final String EDIT_NAME = "edit";
	private static final String EDIT_DESCRIPTION = "Edits a previously defined autoroom trigger";
	private static final String OPT_NAME_NAME = "name";
	private static final String OPT_NAME_DESCRIPTION = "Set the naming pattern for autorooms created. Additional variables: username / number.";
	private static final String OPT_TRIGGER_NAME = "trigger";
	private static final String OPT_TRIGGER_DESCRIPTION = "Select the voice channel that will act as the trigger";
	private static final String OPT_CATEGORY_NAME = "category";
	private static final String OPT_CATEGORY_DESCRIPTION = "Select a category in which the created Autorooms should be placed in.";
	private static final String OPT_PARENT_NAME = "parent";
	private static final String OPT_PARENT_DESCRIPTION = "Select a parent object which determines the permissions of the autorooms.";
	public static final String CHOICE_TRIGGER = "trigger";
	public static final String CHOICE_CATEGORY = "category";
	private static final String REMOVE_NAME = "remove";
	private static final String REMOVE_DESCRIPTION = "Removes the autoroom trigger configuration for a specific channel.";
	private static final String REMOVE_OPT_CHANNEL_NAME = "channel";
	private static final String REMOVE_OPT_CHANNEL_DESCRIPTION = "Select the autoroom trigger to be removed.";
	private static final String REMOVE_OPT_DELETE_NAME = "delete";
	private static final String REMOVE_OPT_DELETE_DESCRIPTION = "Select if the channel should be deleted.";

	public Setup() {
		// CREATE
		SubcommandData create = new SubcommandData(CREATE_NAME, CREATE_DESCRIPTION);
		OptionData createOptionName = new OptionData(OptionType.STRING, OPT_NAME_NAME,
				OPT_NAME_DESCRIPTION, true);
		OptionData createOptionTrigger = new OptionData(OptionType.CHANNEL, OPT_TRIGGER_NAME,
				OPT_TRIGGER_DESCRIPTION, true);
		OptionData createOptionCategory = new OptionData(OptionType.CHANNEL, OPT_CATEGORY_NAME,
				OPT_CATEGORY_DESCRIPTION, true);
		OptionData createOptionParent = new OptionData(OptionType.STRING, OPT_PARENT_NAME,
				OPT_PARENT_DESCRIPTION, true);
		createOptionParent.addChoice(CHOICE_TRIGGER, CHOICE_TRIGGER);
		createOptionParent.addChoice(CHOICE_CATEGORY, CHOICE_CATEGORY);
		create.addOptions(createOptionName, createOptionTrigger, createOptionCategory, createOptionParent);
		// EDIT
		SubcommandData edit = new SubcommandData(EDIT_NAME, EDIT_DESCRIPTION);
		OptionData editOptionChannel = new OptionData(OptionType.CHANNEL, OPT_TRIGGER_NAME, OPT_TRIGGER_DESCRIPTION,
				true);
		OptionData editOptionName = new OptionData(OptionType.STRING, OPT_NAME_NAME, OPT_NAME_DESCRIPTION);
		OptionData editOptionCategory = new OptionData(OptionType.CHANNEL, OPT_CATEGORY_NAME, OPT_CATEGORY_DESCRIPTION);
		OptionData editOptionParent = new OptionData(OptionType.STRING, OPT_PARENT_NAME, OPT_PARENT_DESCRIPTION);
		editOptionParent.addChoice(CHOICE_TRIGGER, CHOICE_TRIGGER);
		editOptionParent.addChoice(CHOICE_CATEGORY, CHOICE_CATEGORY);
		edit.addOptions(editOptionChannel, editOptionName, editOptionCategory, editOptionParent);
		// REMOVE
		SubcommandData remove = new SubcommandData(REMOVE_NAME, REMOVE_DESCRIPTION);
		OptionData removeOptionTrigger = new OptionData(OptionType.CHANNEL, REMOVE_OPT_CHANNEL_NAME,
				REMOVE_OPT_CHANNEL_DESCRIPTION,
				true);
		OptionData removeOptionDelete = new OptionData(OptionType.BOOLEAN, REMOVE_OPT_DELETE_NAME,
				REMOVE_OPT_DELETE_DESCRIPTION);
		remove.addOptions(removeOptionTrigger, removeOptionDelete);

		commandData.addSubcommands(create, edit, remove);
	}

	@Override
	@Nonnull
	public String getName() {
		return "autoroom";
	}

	@Override
	@Nonnull
	public String getDescription() {
		return "Define an autoroom trigger for this guild.";
	}

	@Override
	@SuppressWarnings("null")
	public Reply execute(SlashCommandInteractionEvent event) throws Exception {
		String subcommandName = event.getSubcommandName();
		Status status = Status.FAIL;
		String statusDetail = "Fatal error!\nPlease contact the developer immediately.\nDiscord Tag: Arty#1006";
		DBAutoroomTriggerHandler dbAutoroomTriggerHandler = new DBAutoroomTriggerHandler();
		Reply reply = new Reply(event);
		// init database session
		Session session = Database.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		switch (subcommandName) {
			case CREATE_NAME -> {
				try { // try catch to validate trigger & category options
					String name = event.getOption(OPT_NAME_NAME).getAsString();
					VoiceChannel trigger = event.getOption(OPT_TRIGGER_NAME)
							.getAsChannel().asVoiceChannel();
					AutoroomTrigger autoroomTrigger = dbAutoroomTriggerHandler.getEntity(session, trigger.getIdLong());
					// prevent double bindings
					if (autoroomTrigger != null) {
						// close database session
						transaction.commit();
						Database.getSessionFactory().getCurrentSession().close();
						return new Reply(event).onCommand(Status.FAIL,
								"The selceted channel is already an autoroom trigger!");
					}
					Category category = event.getOption(OPT_CATEGORY_NAME)
							.getAsChannel().asCategory();
					String parent = event.getOption(OPT_PARENT_NAME).getAsString();

					autoroomTrigger = new AutoroomTrigger();
					autoroomTrigger.setCategory_id(category.getIdLong());
					autoroomTrigger.setInheritance(parent);
					autoroomTrigger.setNaming_pattern(name);
					autoroomTrigger.setId(trigger.getIdLong());
					dbAutoroomTriggerHandler.insert(session, autoroomTrigger);
					status = Status.SUCCESS;
					statusDetail = """
							Set %s as an autoroom trigger.
							The Autorooms will be named: %s.""".formatted(trigger.getAsMention(), name);
					reply.setEphemeral(false);
				} catch (IllegalStateException e) {
					status = Status.FAIL;
					statusDetail = "Either the specified voice channel is not a voice channel or the specified category is not a category!";
				}
			}
			case EDIT_NAME -> {
				VoiceChannel trigger = event.getOption(OPT_TRIGGER_NAME).getAsChannel().asVoiceChannel();
				// check if the provided channel is a trigger
				if (dbAutoroomTriggerHandler.getEntity(session, trigger.getIdLong()) == null) {
					// close database session
					transaction.commit();
					Database.getSessionFactory().getCurrentSession().close();
					return reply.onCommand(Status.FAIL,
							"The selected channel is not an autoroom trigger!");
				}
				OptionMapping option = event.getOption(OPT_NAME_NAME);
				String name = null, parent = null;
				long categoryID = -1;
				// check the optional parameters
				// name
				if (option != null) {
					name = option.getAsString();
					statusDetail = """
							Changed the **Naming pattern** for created autorooms\nfor the trigger %s to: **%s**"""
							.formatted(trigger.getAsMention(), name);
				}
				// category
				if ((option = event.getOption(OPT_CATEGORY_NAME)) != null) {
					try {
						Category category = option.getAsChannel().asCategory();
						categoryID = category.getIdLong();
						statusDetail += """

								Changed the **Category** where autorooms will be created\nfor the trigger %s to **%s**."""
								.formatted(trigger.getAsMention(), category.getAsMention());
					} catch (IllegalStateException e) {
						// close database session
						transaction.commit();
						Database.getSessionFactory().getCurrentSession().close();
						return reply.onCommand(Status.FAIL, "The selected channel has to be a **Category**!");
					}
				}
				// parent
				if ((option = event.getOption(OPT_PARENT_NAME)) != null) {
					parent = option.getAsString();
					statusDetail += """

							Changed the **Parent object** that determines the permissions\nfor the trigger channel to **%s**.
								"""
							.formatted(parent);
				}
				// apply changes
				AutoroomTrigger autoroomTrigger = dbAutoroomTriggerHandler.getEntity(session, trigger.getIdLong());
				autoroomTrigger.setNaming_pattern(name); // could be null
				autoroomTrigger.setCategory_id(categoryID); // could be -1
				autoroomTrigger.setInheritance(parent); // could be null
				dbAutoroomTriggerHandler.update(session, autoroomTrigger);
				reply.setEphemeral(false);
				status = Status.SUCCESS;
			}
			case REMOVE_NAME -> {
				VoiceChannel trigger = event.getOption(REMOVE_OPT_CHANNEL_NAME).getAsChannel().asVoiceChannel();
				if (dbAutoroomTriggerHandler.getEntity(session, trigger.getIdLong()) != null) {
					dbAutoroomTriggerHandler.remove(session, trigger.getIdLong());
					statusDetail = "removed " + trigger.getAsMention();
					OptionMapping optionMapping = event.getOption(REMOVE_OPT_DELETE_NAME);
					if (optionMapping != null && optionMapping.getAsBoolean())
						trigger.delete().queue();
					reply.setEphemeral(false);
					status = Status.SUCCESS;
				} else
					status = Status.FAIL;
				statusDetail = "Could not identify " + trigger.getAsMention() + " as an autoroom trigger!";
			}
		}
		// close database session
		transaction.commit();
		Database.getSessionFactory().getCurrentSession().close();
		return reply.onCommand(status, statusDetail);
	}

	@Override
	public boolean isGuildOnly() {
		return true;
	}

}
