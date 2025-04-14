/**
 * Core class that manages the story state, transitions, and logic.
 * Demonstrates extensive use of switch statements for handling different scenes and choices.
 */
public class StoryManager {
    // Enum for different scenes in the story
    public enum SceneID {
        START, FOREST, CASTLE, FINAL_SHOWDOWN, GAME_OVER, VICTORY
    }

    // Enum for different choices the player can make
    public enum ChoiceType {
        EXPLORE_FOREST, VISIT_CASTLE, FIGHT_MONSTER,
        HELP_VILLAGERS, STEAL_TREASURE, BEFRIEND_KING,
        CHALLENGE_KING, FACE_DRAGON, RETREAT,
        // New choices for the forest revisit after dragon retreat
        SEEK_ANCIENT_MAGIC, TRAIN_WITH_VILLAGERS
    }

    private SceneID currentScene;
    private int moralityScore;
    private boolean hasWeapon;
    private boolean hasArtifact;
    private boolean hasRetreatedFromDragon; // New flag to track retreat state

    /**
     * Constructor initializes the game state to starting values
     */
    public StoryManager() {
        this.currentScene = SceneID.START;
        this.moralityScore = 0;
        this.hasWeapon = false;
        this.hasArtifact = false;
        this.hasRetreatedFromDragon = false;
    }

    /**
     * Main method that handles player choices using a switch statement
     * to direct to the appropriate scene-specific handler
     */
    public void handleChoice(ChoiceType userChoice) {
        // First-level switch based on current scene
        switch(currentScene) {
            case START:
                handleStartSceneChoice(userChoice);
                break;
            case FOREST:
                handleForestSceneChoice(userChoice);
                break;
            case CASTLE:
                handleCastleSceneChoice(userChoice);
                break;
            case FINAL_SHOWDOWN:
                handleShowdownSceneChoice(userChoice);
                break;
            case GAME_OVER:
            case VICTORY:
                // These are terminal states, no choices to handle
                break;
        }
    }

    /**
     * Handles choices for the START scene using a switch statement
     */
    private void handleStartSceneChoice(ChoiceType userChoice) {
        // Second-level switch based on user choice
        switch(userChoice) {
            case EXPLORE_FOREST:
                currentScene = SceneID.FOREST;
                moralityScore += 10; // Exploring is considered positive
                break;
            case VISIT_CASTLE:
                currentScene = SceneID.CASTLE;
                moralityScore -= 5; // Seeking power is slightly negative
                break;
            default:
                throw new IllegalArgumentException("Invalid choice for START scene: " + userChoice);
        }
    }

    /**
     * Handles choices for the FOREST scene using a switch statement with conditional logic
     */
    private void handleForestSceneChoice(ChoiceType userChoice) {
        if (hasRetreatedFromDragon) {
            // Special handling for returning to forest after dragon retreat
            switch(userChoice) {
                case SEEK_ANCIENT_MAGIC:
                    moralityScore += 5;
                    hasArtifact = true; // Gain the artifact
                    currentScene = SceneID.FINAL_SHOWDOWN;
                    break;
                case TRAIN_WITH_VILLAGERS:
                    moralityScore += 15;
                    hasWeapon = true; // Gain the weapon
                    currentScene = SceneID.FINAL_SHOWDOWN;
                    break;
                case FACE_DRAGON:
                    // Direct path back to dragon
                    currentScene = SceneID.FINAL_SHOWDOWN;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid choice for retreat forest scene: " + userChoice);
            }
        } else {
            // Original forest scene logic
            switch(userChoice) {
                case FIGHT_MONSTER:
                    // Conditional logic within a switch case
                    if (moralityScore > 15) {
                        hasWeapon = true; // Reward for having good morality
                        currentScene = SceneID.FINAL_SHOWDOWN;
                    } else {
                        currentScene = SceneID.GAME_OVER; // Not morally strong enough
                    }
                    break;
                case HELP_VILLAGERS:
                    moralityScore += 20; // Major morality boost
                    hasArtifact = true; // Reward item
                    currentScene = SceneID.CASTLE;
                    break;
                case STEAL_TREASURE:
                    moralityScore -= 25; // Large morality penalty
                    hasWeapon = true; // Still get weapon, but at a cost
                    currentScene = SceneID.CASTLE;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid choice for FOREST scene: " + userChoice);
            }
        }
    }

    /**
     * Handles choices for the CASTLE scene using a switch statement with inventory checks
     */
    private void handleCastleSceneChoice(ChoiceType userChoice) {
        switch(userChoice) {
            case BEFRIEND_KING:
                moralityScore += 15;
                // Path depends on inventory state
                if (hasArtifact) {
                    currentScene = SceneID.FINAL_SHOWDOWN;
                } else {
                    currentScene = SceneID.FOREST; // Need to go back and find the artifact
                }
                break;
            case CHALLENGE_KING:
                moralityScore -= 10;
                // Different outcome based on inventory
                if (hasWeapon) {
                    currentScene = SceneID.FINAL_SHOWDOWN;
                } else {
                    currentScene = SceneID.GAME_OVER; // Can't challenge without a weapon
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid choice for CASTLE scene: " + userChoice);
        }
    }

    /**
     * Handles choices for the FINAL_SHOWDOWN scene with complex condition evaluation
     */
    private void handleShowdownSceneChoice(ChoiceType userChoice) {
        switch(userChoice) {
            case FACE_DRAGON:
                // Multiple ways to win based on morality OR inventory
                if (moralityScore > 30 || (hasWeapon && hasArtifact)) {
                    currentScene = SceneID.VICTORY;
                } else {
                    currentScene = SceneID.GAME_OVER;
                }
                break;
            case RETREAT:
                // Different outcomes based on morality
                if (moralityScore < 0) {
                    currentScene = SceneID.GAME_OVER; // Evil characters can't retreat
                } else {
                    currentScene = SceneID.FOREST;
                    hasRetreatedFromDragon = true; // Set the retreat flag
                    moralityScore -= 10; // Penalty for retreating
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid choice for FINAL_SHOWDOWN scene: " + userChoice);
        }
    }

    /**
     * Uses a switch statement to generate scene descriptions based on current state
     */
    public String getCurrentSceneDescription() {
        // Switch on scene type with complex string generation
        switch(currentScene) {
            case START:
                return "You stand at the crossroads of a mystical kingdom. To the north lies a dark forest, "
                        + "rumored to be filled with dangerous creatures but also treasures. To the east, "
                        + "a magnificent castle looms over the landscape, home to the king and his court.\n\n"
                        + "Which path will you choose?";

            case FOREST:
                if (hasRetreatedFromDragon) {
                    return "You return to the forest, seeking to strengthen yourself before facing the dragon again. "
                            + "The once-attacked village is now peaceful, thanks to your earlier help. "
                            + "Your previous adventure has taught you much, but you know you need more power. "
                            + "Deep in the forest, you sense powerful magic. What will you do now?";
                } else {
                    String baseDesc = "The forest is thick with ancient trees and strange sounds. "
                            + "As you venture deeper, you encounter a small village under attack "
                            + "by a fearsome monster. The villagers look desperate for help.";

                    // Conditional text addition based on state
                    if (moralityScore < 0) {
                        baseDesc += "\n\nYou also notice a treasure chest hidden nearby, seemingly unguarded.";
                    }
                    return baseDesc;
                }

            case CASTLE:
                String castleDesc = "The grand castle is bustling with activity. Guards eye you suspiciously as you enter.";

                // Conditional text based on inventory items
                if (hasArtifact) {
                    castleDesc += "\n\nYour artifact seems to glow in the presence of the castle's magic.";
                }
                if (hasWeapon) {
                    castleDesc += "\n\nThe guards seem wary of your weapon.";
                }
                castleDesc += "\n\nThe king requests an audience with you.";
                return castleDesc;

            case FINAL_SHOWDOWN:
                return "You've reached the ancient dragon's lair. The massive creature guards the realm's "
                        + "greatest treasure and darkest secrets. Your journey has led to this moment.\n\n"
                        + "Do you face the dragon with what you've earned along the way, or retreat to gather more strength?";

            case GAME_OVER:
                // Different game over text based on morality
                if (moralityScore < 0) {
                    return "Your selfish actions have led to your downfall. The kingdom falls into darkness, "
                            + "and your name is forgotten in time.\n\nGAME OVER";
                } else {
                    return "Despite your good intentions, your journey has come to a premature end. "
                            + "Perhaps another path would have led to victory.\n\nGAME OVER";
                }

            case VICTORY:
                // Different victory text based on morality
                if (moralityScore > 30) {
                    return "Through your courage and moral choices, you have overcome the dragon! "
                            + "The kingdom celebrates you as a hero, and songs of your deeds will be sung for generations.\n\nVICTORY!";
                } else {
                    return "Through cunning and resourcefulness, you have defeated the dragon and claimed its treasure. "
                            + "Your name will be remembered in the annals of history.\n\nVICTORY!";
                }

            default:
                return "Error: Unknown scene";
        }
    }

    /**
     * Uses a switch statement to determine available choices based on current state
     */
    public ChoiceType[] getAvailableChoices() {
        // Switch returning different arrays based on scene and conditions
        switch(currentScene) {
            case START:
                return new ChoiceType[] {
                        ChoiceType.EXPLORE_FOREST,
                        ChoiceType.VISIT_CASTLE
                };

            case FOREST:
                if (hasRetreatedFromDragon) {
                    // Different choices for returning to the forest
                    return new ChoiceType[] {
                            ChoiceType.SEEK_ANCIENT_MAGIC,
                            ChoiceType.TRAIN_WITH_VILLAGERS,
                            ChoiceType.FACE_DRAGON
                    };
                } else {
                    // Original forest choices
                    if (moralityScore < 0) {
                        return new ChoiceType[] {
                                ChoiceType.FIGHT_MONSTER,
                                ChoiceType.HELP_VILLAGERS,
                                ChoiceType.STEAL_TREASURE
                        };
                    } else {
                        return new ChoiceType[] {
                                ChoiceType.FIGHT_MONSTER,
                                ChoiceType.HELP_VILLAGERS
                        };
                    }
                }

            case CASTLE:
                return new ChoiceType[] {
                        ChoiceType.BEFRIEND_KING,
                        ChoiceType.CHALLENGE_KING
                };

            case FINAL_SHOWDOWN:
                return new ChoiceType[] {
                        ChoiceType.FACE_DRAGON,
                        ChoiceType.RETREAT
                };

            case GAME_OVER:
            case VICTORY:
                return new ChoiceType[0]; // No choices for end states

            default:
                return new ChoiceType[0];
        }
    }

    /**
     * Uses a switch statement to convert enum choices to human-readable button text
     */
    public String getChoiceButtonText(ChoiceType choice) {
        // Simple switch mapping enum values to display strings
        switch(choice) {
            case EXPLORE_FOREST:
                return "Explore the Forest";
            case VISIT_CASTLE:
                return "Visit the Castle";
            case FIGHT_MONSTER:
                return "Fight the Monster";
            case HELP_VILLAGERS:
                return "Help the Villagers";
            case STEAL_TREASURE:
                return "Steal the Treasure";
            case BEFRIEND_KING:
                return "Befriend the King";
            case CHALLENGE_KING:
                return "Challenge the King";
            case FACE_DRAGON:
                return "Face the Dragon";
            case RETREAT:
                return "Retreat";
            case SEEK_ANCIENT_MAGIC:
                return "Seek Ancient Magic";
            case TRAIN_WITH_VILLAGERS:
                return "Train with Grateful Villagers";
            default:
                return "Unknown Choice";
        }
    }

    /**
     * Uses a switch statement to determine the appropriate image path for each scene
     */
    public String getSceneImagePath() {
        // Switch on scene type to return the corresponding image path
        switch(currentScene) {
            case START:
                return "/images/crossroads.png";

            case FOREST:
                if (hasRetreatedFromDragon) {
                    return "/images/forest_return.png";
                } else if (moralityScore < 0) {
                    return "/images/dark_forest.png";
                } else {
                    return "/images/forest.png";
                }

            case CASTLE:
                // Different castle images based on inventory state
                if (hasArtifact && hasWeapon) {
                    return "/images/castle_hero.png";
                } else if (hasWeapon) {
                    return "/images/castle_armed.png";
                } else if (hasArtifact) {
                    return "/images/castle_artifact.png";
                } else {
                    return "/images/castle.png";
                }

            case FINAL_SHOWDOWN:
                return "/images/dragon_lair.png";

            case GAME_OVER:
                // Different game over images based on morality
                if (moralityScore < 0) {
                    return "/images/dark_ending.png";
                } else {
                    return "/images/game_over.png";
                }

            case VICTORY:
                // Different victory images based on morality
                if (moralityScore > 30) {
                    return "/images/hero_victory.png";
                } else {
                    return "/images/treasure_victory.png";
                }

            default:
                return "/images/placeholder.png";
        }
    }

    // Getters and setters
    public SceneID getCurrentScene() {
        return currentScene;
    }

    public int getMoralityScore() {
        return moralityScore;
    }

    public boolean hasWeapon() {
        return hasWeapon;
    }

    public boolean hasArtifact() {
        return hasArtifact;
    }

    public boolean hasRetreatedFromDragon() {
        return hasRetreatedFromDragon;
    }

    /**
     * Reset the game to initial state
     */
    public void resetGame() {
        this.currentScene = SceneID.START;
        this.moralityScore = 0;
        this.hasWeapon = false;
        this.hasArtifact = false;
        this.hasRetreatedFromDragon = false;
    }
}