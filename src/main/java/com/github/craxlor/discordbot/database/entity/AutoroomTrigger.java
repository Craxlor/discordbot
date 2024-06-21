package com.github.craxlor.discordbot.database.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "autoroomTriggers")
public class AutoroomTrigger {

    @Id
    private long id;

    @Column(name = "category_id")
    private long category_id;

    @Column(name = "naming_pattern")
    private String naming_pattern;

    @Column(name = "inheritance")
    private String inheritance;

    @OneToMany(mappedBy = "autoroomTrigger")
    private List<AutoroomChannel> autoroomChannels;
}
