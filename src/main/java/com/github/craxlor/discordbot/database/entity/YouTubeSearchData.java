package com.github.craxlor.discordbot.database.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class YouTubeSearchData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;// = searchTerm

    @ManyToOne
    @JoinColumn(name = "video_id")
    private YouTubeVideoData youTubeVideoData;

}
