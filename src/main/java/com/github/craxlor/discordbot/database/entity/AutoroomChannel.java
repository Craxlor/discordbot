package com.github.craxlor.discordbot.database.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "autoroomChannels")
public class AutoroomChannel {

    @Id
    private long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "trigger_id", referencedColumnName = "id")
    private AutoroomTrigger autoroomTrigger;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private Guild autoroomChannels_guild;

}
