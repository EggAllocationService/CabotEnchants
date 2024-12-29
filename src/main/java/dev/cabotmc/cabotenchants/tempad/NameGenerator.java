package dev.cabotmc.cabotenchants.tempad;

public class NameGenerator {
    private static final String[] adjectives = {
            "Brave",
            "Silent",
            "Mystic",
            "Crimson",
            "Ancient",
            "Frozen",
            "Golden",
            "Hidden",
            "Eternal",
            "Savage",
            "Blazing",
            "Emerald",
            "Wise",
            "Shadow",
            "Crystal",
            "Storm",
            "Wild",
            "Iron",
            "Sacred",
            "Dark"
    };

    private static final String[] nouns = {
            "Phoenix",
            "Dragon",
            "Knight",
            "Mountain",
            "River",
            "Wolf",
            "Eagle",
            "Warrior",
            "Forest",
            "Tiger",
            "Spirit",
            "Guardian",
            "Crown",
            "Sword",
            "Hunter",
            "Shadow",
            "Storm",
            "Lion",
            "Legend",
            "Star"
    };

    public static String generateRandomName() {
        int randomAdjIndex = (int) (Math.random() * adjectives.length);
        int randomNounIndex = (int) (Math.random() * nouns.length);

        return adjectives[randomAdjIndex] + " " + nouns[randomNounIndex];
    }
}