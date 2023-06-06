package com.github.craxlor.discordbot.database.entity;

public class AutoroomTrigger {
    private long trigger_id, category_id;
    private String naming_pattern, inheritance;

    // SETTER
    public void setTrigger_id(long trigger_id) {
        this.trigger_id = trigger_id;
    }

    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }

    public void setNaming_pattern(String naming_pattern) {
        this.naming_pattern = naming_pattern;
    }

    public void setInheritance(String inheritance) {
        this.inheritance = inheritance;
    }

    // GETTER
    public long getTrigger_id() {
        return trigger_id;
    }

    public long getCategory_id() {
        return category_id;
    }

    public String getNaming_pattern() {
        return naming_pattern;
    }

    public String getInheritance() {
        return inheritance;
    }

}
