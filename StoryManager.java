/**
 * Kelas inti yang mengelola status cerita, transisi, dan logika.
 * Mendemonstrasikan penggunaan switch statement untuk menangani scene dan pilihan yang berbeda.
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
    private boolean hasRetreatedFromDragon;
    private int dragonRetreatCount;
    private boolean isCorruptedByMagic;

    /**
     * Konstruktor melakukan inisialisasi status permainan ke nilai awal
     */
    public StoryManager() {
        this.currentScene = SceneID.START;
        this.moralityScore = 0;
        this.hasWeapon = false;
        this.hasArtifact = false;
        this.hasRetreatedFromDragon = false;
        this.dragonRetreatCount = 0;
        this.isCorruptedByMagic = false;
    }

    /**
     * Metode utama yang menangani pilihan pemain menggunakan switch statement
     * untuk mengarahkan ke penangan khusus adegan yang sesuai
     */
    public void handleChoice(ChoiceType userChoice) {
        // Beralih tingkat pertama berdasarkan adegan saat ini (Current Scene)
        switch (currentScene) {
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
                // Ini adalah kondisi akhir, tidak ada pilihan untuk ditangani
                break;
        }
    }

    /**
     * Menangani pilihan untuk adegan START menggunakan switch statement
     */
    private void handleStartSceneChoice(ChoiceType userChoice) {
        // Switch tingkat kedua berdasarkan pilihan pengguna
        switch (userChoice) {
            case EXPLORE_FOREST:
                currentScene = SceneID.FOREST; // Menjelajah dianggap netral
                break;
            case VISIT_CASTLE:
                currentScene = SceneID.CASTLE;
                moralityScore -= 5; // Mencari kekuatan sedikit negatif
                break;
            default:
                throw new IllegalArgumentException("Invalid choice for START scene: " + userChoice);
        }
    }

    /**
     * Menangani pilihan untuk scene FOREST menggunakan switch statement dengan logika bersyarat
     */
    private void handleForestSceneChoice(ChoiceType userChoice) {
        if (hasRetreatedFromDragon) {
            switch (userChoice) {
                case SEEK_ANCIENT_MAGIC:
                    moralityScore -= 20; // Hukuman moralitas besar
                    hasArtifact = true;
                    isCorruptedByMagic = true; // Set corruption flag
                    currentScene = SceneID.FINAL_SHOWDOWN;
                    break;
                case TRAIN_WITH_VILLAGERS:
                    moralityScore += 15;
                    hasWeapon = true; // Dapatkan senjata tanpa menguragi moralitas
                    currentScene = SceneID.FINAL_SHOWDOWN;
                    break;
                case FACE_DRAGON:
                    currentScene = SceneID.FINAL_SHOWDOWN;
                    hasRetreatedFromDragon = false; // mereset variable bertemu dengan boss menjadi FALSE
                    break;
                default:
                    throw new IllegalArgumentException("Invalid choice for retreat forest scene: " + userChoice);
            }
        } else {
            // Logika pemandangan hutan asli (belum lari dari boss)
            switch (userChoice) {
                case FIGHT_MONSTER:
                    // Logika bersyarat di dalam switch case
                    if (moralityScore > 15) {
                        hasWeapon = true; // Reward karena memiliki moralitas yang baik
                        currentScene = SceneID.FINAL_SHOWDOWN;
                    } else {
                        currentScene = SceneID.GAME_OVER; // Tidak cukup kuat secara moral
                    }
                    break;
                case HELP_VILLAGERS:
                    moralityScore += 20; // Peningkatan moralitas yang besar
                    hasArtifact = true; // Item hadiah
                    currentScene = SceneID.CASTLE;
                    break;
                case STEAL_TREASURE:
                    moralityScore -= 25; // Hukuman moralitas yang besar
                    hasWeapon = true; // Masih bisa mendapatkan senjata, dibayar dengan nilai moralitas
                    currentScene = SceneID.CASTLE;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid choice for FOREST scene: " + userChoice);
            }
        }
    }

    /**
     * Menangani pilihan untuk adegan CASTLE menggunakan switch case dengan pemeriksaan inventory
     */
    private void handleCastleSceneChoice(ChoiceType userChoice) {
        switch (userChoice) {
            case BEFRIEND_KING:
                moralityScore += 15;
                // Jalur tergantung pada kondisi inventory
                if (hasArtifact) {
                    currentScene = SceneID.FINAL_SHOWDOWN;
                } else {
                    currentScene = SceneID.FOREST; // Harus kembali dan menemukan artefak
                }
                break;
            case CHALLENGE_KING:
                moralityScore -= 10;
                // Hasil yang berbeda berdasarkan isi inventory
                if (hasWeapon) {
                    currentScene = SceneID.FINAL_SHOWDOWN;
                } else {
                    currentScene = SceneID.GAME_OVER; // Akan mati jika menantang tanpa senjata
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid choice for CASTLE scene: " + userChoice);
        }
    }

    /**
     * Menangani pilihan untuk adegan FINAL_SHOWDOWN dengan evaluasi kondisi yang kompleks
     */
    private void handleShowdownSceneChoice(ChoiceType userChoice) {
        switch (userChoice) {
            case FACE_DRAGON:
                if (isCorruptedByMagic) {
                    // Pemain yang corrupted HARUS memiliki senjata untuk menang
                    currentScene = hasWeapon ? SceneID.VICTORY : SceneID.GAME_OVER;
                } else {
                    // Kondisi kemenangan untuk yang tidak corrupted
                    if (moralityScore > 30 || (hasWeapon && hasArtifact)) {
                        currentScene = SceneID.VICTORY;
                    } else {
                        currentScene = SceneID.GAME_OVER;
                    }
                }
                break;
            case RETREAT:
                // Penghitung kabur penambahan
                dragonRetreatCount++;
                if (dragonRetreatCount >= 2) {
                    currentScene = SceneID.GAME_OVER; // Tidak ada kesempatan kedua!
                } else {
                    currentScene = SceneID.FOREST;
                    hasRetreatedFromDragon = true; // Mengatur variable kabur menjadi true
                    moralityScore -= 10; // Hukuman karena mundur
                    // Menambahkan hukuman tambahan untuk moralitas yang rendah
                    if (moralityScore < 0) {
                        moralityScore -= 5; // Further penalty for evil characters
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid choice for FINAL_SHOWDOWN scene: " + userChoice);
        }
    }

    /**
     * Menggunakan switch statement untuk menghasilkan deskripsi adegan berdasarkan kondisi saat ini
     */
    public String getCurrentSceneDescription() {
        // Mengaktifkan jenis adegan dengan pembuatan string yang kompleks
        switch (currentScene) {
            case START:
                return """
                        You stand at the crossroads of a mystical kingdom. To the north lies a dark forest, \
                        rumored to be filled with dangerous creatures but also treasures. To the east, \
                        a magnificent castle looms over the landscape, home to the king and his court.
                        
                        Which path will you choose?""";

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

                    // Penambahan teks bersyarat berdasarkan status moralitas
                    if (moralityScore < 0) {
                        baseDesc += "\n\nYou also notice a treasure chest hidden nearby, seemingly unguarded.";
                    }
                    return baseDesc;
                }

            case CASTLE:
                String castleDesc = "The grand castle is bustling with activity. Guards eye you suspiciously as you enter.";

                // Teks bersyarat berdasarkan item di inventory
                if (hasArtifact) {
                    castleDesc += "\n\nYour artifact seems to glow in the presence of the castle's magic.";
                }
                if (hasWeapon) {
                    castleDesc += "\n\nThe guards seem wary of your weapon.";
                }
                castleDesc += "\n\nThe king requests an audience with you.";
                return castleDesc;

            case FINAL_SHOWDOWN:
                return """
                        You've reached the ancient dragon's lair. The massive creature guards the realm's \
                        greatest treasure and darkest secrets. Your journey has led to this moment.
                        
                        Do you face the dragon with what you've earned along the way, or retreat to gather more strength?""";

            case GAME_OVER:
                if (isCorruptedByMagic && !hasWeapon) {
                    return """
                            The dragon senses your corruption and weakness. Without a proper weapon, \
                            the ancient magic consumes you entirely. Your body dissolves into dark mist \
                            as the dragon laughs.
                            
                            GAME OVER""";
                } else if (dragonRetreatCount >= 2) {
                    return "The dragon blocks your escape. There's no running from destiny twice.\n\nGAME OVER";
                } else if (moralityScore < 0) {
                    return """
                            Your selfish actions have led to your downfall. The kingdom falls into darkness, \
                            and your name is forgotten in time.
                            
                            GAME OVER""";
                } else {
                    return """
                            Despite your good intentions, your journey has come to a premature end. \
                            Perhaps another path would have led to victory.
                            
                            GAME OVER""";
                }
            case VICTORY:
                // Teks kemenangan yang berbeda berdasarkan moralitas
                if (isCorruptedByMagic) {
                    return """
                            The ancient magic courses through your veins as you strike down the dragon. \
                            You claim its hoard, but feel the corruption spreading. The kingdom will soon \
                            learn to fear your new power...
                            
                            PYRRHIC VICTORY""";
                } else if (moralityScore > 30) {
                    return """
                            Through your courage and moral choices, you have overcome the dragon! \
                            The kingdom celebrates you as a hero, and songs of your deeds will be sung for generations.
                            
                            VICTORY!""";
                } else {
                    return """
                            Through cunning and resourcefulness, you have defeated the dragon and claimed its treasure. \
                            Your name will be remembered in the annals of history.
                            
                            VICTORY!""";
                }
            default:
                return "Error: Unknown scene";
        }
    }

    /**
     * Menggunakan switch statement untuk menentukan pilihan yang tersedia berdasarkan kondisi saat ini (Current State)
     */
    public ChoiceType[] getAvailableChoices() {
        // Switch returning different arrays based on scene and conditions
        switch (currentScene) {
            case START:
                return new ChoiceType[]{
                        ChoiceType.EXPLORE_FOREST,
                        ChoiceType.VISIT_CASTLE
                };

            case FOREST:
                if (hasRetreatedFromDragon) {
                    // Pilihan yang berbeda untuk yang kembali ke hutan
                    return new ChoiceType[]{
                            ChoiceType.SEEK_ANCIENT_MAGIC,
                            ChoiceType.TRAIN_WITH_VILLAGERS,
                            ChoiceType.FACE_DRAGON
                    };
                } else {
                    // Pilihan di hutan yang original/asli (belum kabur)
                    if (moralityScore <= 0) {
                        return new ChoiceType[]{
                                ChoiceType.FIGHT_MONSTER,
                                ChoiceType.HELP_VILLAGERS,
                                ChoiceType.STEAL_TREASURE
                        };
                    } else {
                        return new ChoiceType[]{
                                ChoiceType.FIGHT_MONSTER,
                                ChoiceType.HELP_VILLAGERS
                        };
                    }
                }

            case CASTLE:
                return new ChoiceType[]{
                        ChoiceType.BEFRIEND_KING,
                        ChoiceType.CHALLENGE_KING
                };

            case FINAL_SHOWDOWN:
                return new ChoiceType[]{
                        ChoiceType.FACE_DRAGON,
                        ChoiceType.RETREAT
                };

            case GAME_OVER:
            case VICTORY:
                return new ChoiceType[0]; // Tidak ada pilihan untuk status akhir

            default:
                return new ChoiceType[0];
        }
    }

    /**
     * Menggunakan switch statement untuk mengonversi pilihan enum menjadi teks tombol yang dapat dibaca manusia
     */
    public String getChoiceButtonText(ChoiceType choice) {
        // Nilai enum pemetaan sakelar sederhana untuk menampilkan string
        return switch (choice) {
            case EXPLORE_FOREST -> "Explore the Forest";
            case VISIT_CASTLE -> "Visit the Castle";
            case FIGHT_MONSTER -> "Fight the Monster";
            case HELP_VILLAGERS -> "Help the Villagers";
            case STEAL_TREASURE -> "Steal the Treasure";
            case BEFRIEND_KING -> "Befriend the King";
            case CHALLENGE_KING -> "Challenge the King";
            case FACE_DRAGON -> "Face the Dragon";
            case RETREAT -> "Retreat";
            case SEEK_ANCIENT_MAGIC -> "Seek Ancient Magic";
            case TRAIN_WITH_VILLAGERS -> "Train with Grateful Villagers";
            default -> "Unknown Choice";
        };
    }

    /**
     * Menggunakan switch statement untuk menentukan jalur gambar yang sesuai untuk tiap adegan
     */
    public String getSceneImagePath() {
        // Aktifkan jenis adegan untuk mengembalikan path gambar yang sesuai
        switch (currentScene) {
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
                // Gambar kastil yang berbeda berdasarkan isi inventory
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
                if (isCorruptedByMagic && !hasWeapon) {
                    return "/images/corruption_consumption.png";
                } else if (dragonRetreatCount >= 2) {
                    return "/images/dragon_catches_you.png";
                } else if (moralityScore < 0) {
                    return "/images/dark_ending.png";
                } else {
                    return "/images/game_over.png";
                }

            case VICTORY:
                if (isCorruptedByMagic) {
                    return "/images/corrupted_victory.png";
                } else if (moralityScore > 30) {
                    return "/images/hero_victory.png";
                } else {
                    return "/images/treasure_victory.png";
                }
            default:
                return "/images/placeholder.png";
        }
    }

    // Getters dan setters
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

    public int getDragonRetreatCount() {
        return dragonRetreatCount;
    }
    public boolean isCorrupted() {
        return isCorruptedByMagic;
    }
    /**
     * Mengatur ulang permainan ke kondisi awal
     */
    public void resetGame() {
        this.currentScene = SceneID.START;
        this.moralityScore = 0;
        this.hasWeapon = false;
        this.hasArtifact = false;
        this.hasRetreatedFromDragon = false;
        this.dragonRetreatCount = 0; // Reset the counter
        this.isCorruptedByMagic = false;
    }
}