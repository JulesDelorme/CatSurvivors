package io.github.some_example_name.game;

public class LevelChoice {
    public final UpgradeType type;
    public final String title;
    public final String description;

    public LevelChoice(UpgradeType type, String title, String description) {
        this.type = type;
        this.title = title;
        this.description = description;
    }
}
