package com.github.craxlor.discordbot.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "guilds")
public class DiscordServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "musicLog_id")
    private long musicLog_id;

    @Column(name = "name")
    private String name;

    @Column(name = "modules")
    private String modules;

    @Column(name = "colorHex")
    private String colorHex;

    @Nullable
    public List<String> getModulesAsList() {
        if (modules == null)
            return null;

        String[] array = modules.split(",");
        List<String> list = new ArrayList<>();
        for (String module : array) {
            list.add(module);
        }
        return list;
    }

}
