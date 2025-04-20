
/**
 * Kelas inti yang mengelola status cerita, transisi, dan logika.
 * Mendemonstrasikan penggunaan switch statement untuk menangani scene dan pilihan yang berbeda.
 */
public class StoryManager {
    // Enum for different scenes in the story
    public enum SceneID {
        START, FOREST, CASTLE, FINAL_SHOWDOWN, GAME_OVER, VICTORY,BETRAYAL_ENDING
    }

    // Enum for different choices the player can make
    public enum ChoiceType {
        EXPLORE_FOREST, VISIT_CASTLE, FIGHT_MONSTER,
        HELP_VILLAGERS, STEAL_TREASURE, BEFRIEND_KING,
        CHALLENGE_KING, FACE_DRAGON, RETREAT,
        // New choices for the forest revisit after dragon retreat
        SEEK_ANCIENT_MAGIC, TRAIN_WITH_VILLAGERS,BETRAY_KING
    }

    private SceneID currentScene;
    private int moralityScore;
    private boolean hasWeapon;
    private boolean hasArtifact;
    private boolean hasRetreatedFromDragon;
    private int dragonRetreatCount;
    private boolean isCorruptedByMagic;
    private boolean befriendedKing;

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
        this.befriendedKing = false;
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
                if (!befriendedKing) {
                    moralityScore += 15;
                    befriendedKing = true; // Set the flag to true after befriending the king
                    // Jalur tergantung pada kondisi inventory
                    if (hasArtifact) {
                        currentScene = SceneID.FINAL_SHOWDOWN;
                    } else {
                        currentScene = SceneID.FOREST; // Harus kembali dan menemukan artefak
                    }
                } else {
                    System.out.println("You have already befriended the king.");
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
            case BETRAY_KING:
                if (befriendedKing && hasWeapon && moralityScore <= 10) {
                    currentScene = SceneID.BETRAYAL_ENDING;
                } else {
                    System.out.println("You can't betray the king without his trust, a weapon, and a dark heart.");
                }
                break;
            case FACE_DRAGON:
                currentScene = SceneID.FINAL_SHOWDOWN;
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
                        Kamu sedang berada di persimpangan jalan di sebuah Kerajaan mistis. \
                        Ke utara adalah jalan menuju hutan gelap, yang dirumorkan penuh dengan berbagai \
                        makhluk dan sosok berbahaya, namun juga tersimpan harta karun tersembunyi. \
                        Ke selatan, ada kastil nan megah membentang, kediaman Raja dan para Bangsawan. \
                        Jalan mana yang akan kamu pilih? """;

            case FOREST:
                if (hasRetreatedFromDragon) {
                    return "Kamu kembali ke hutan, carilah cara untuk memperkuat dirimu sebelum melawan Sang Naga lagi! "
                            + "Desa yang diserang sekarang sudah damai berkat bantuanmu, terima kasih. "
                            + "Kamu telah belajar banyak dari petualanganmu, tetapi kekuatanmu masih kurang. "
                            + "Di dalam hutan, kamu merasakan keberadaan kuasa sihir. Apa yang akan kamu lakukan? ";
                } else {
                    String baseDesc = "Hutan ini penuh dengan pohon-pohon purba dan suara-suara aneh. "
                            + "Semakin dalam kamu menjelajah, kamu melihat ada desa kecil sedang diserang. "
                            + "oleh monster yang mengerikan. Para penduduk desa terlihat sangat putus asa. ";

                    // Penambahan teks bersyarat berdasarkan status moralitas
                    if (moralityScore < 0) {
                        baseDesc += "\n\nKamu juga melihat harta karun tersembunyi di dekatmu, tampaknya tak dijaga. ";
                    }
                    return baseDesc;
                }

            case CASTLE:
                if (befriendedKing) {
                    return "Kamu kembali ke kastil setelah menjelajahi hutan. Sang Raja menyambutmu sebagai teman. "
                            + "Kamu merasa lebih kuat dan siap menghadapi tantangan berikutnya.";
                } else {
                    String castleDesc = "Kastil ini sangat sibuk. Para penjaga mencurigaimu ketika kamu memasuki area kastil. ";

                    // Teks bersyarat berdasarkan item di inventory
                    if (hasArtifact) {
                        castleDesc += "\n\nArtefak ini terlihat bersinar oleh kehadiran aura magis dari kastil.";
                    }
                    if (hasWeapon) {
                        castleDesc += "\n\nPara penjaga terlihat waspada dengan senjatamu.";
                    }
                    castleDesc += "\n\nSang Raja memanggilmu untuk menghadap.";
                    return castleDesc;
                }

            case FINAL_SHOWDOWN:
                return """
                        Kau telah mencapai sarang Sang Naga Purba. Makhluk raksasa itu menjaga harta karun \
                        terbesar dan rahasia tergelap dari seluruh negeri.Perjalananmu selama ini \
                        membawamu ke titik penentuan ini.

                        Akankah kau menghadapi sang naga dengan segala yang telah kau kumpulkan, \
                        atau mundur sejenak untuk menghimpun kekuatan yang lebih besar?""";

            case GAME_OVER:
                if (isCorruptedByMagic && !hasWeapon) {
                    return """
                            Sang Naga merasakan kutukan dan kelemahanmu. Tanpa senjata yang layak, \
                            sihir kuno itu sepenuhnya melahapmu. Sang Naga tertawa selagi tubuhmu \
                            larut menjadi kabut hitam.
                            
                            PERMAINAN BERAKHIR""";
                } else if (dragonRetreatCount >= 2) {
                    return "Kamu memilih untuk lari. Namun Sang Naga menghadang jalanmu.\n\nGAME OVER";
                } else if (moralityScore < 0) {
                    return """
                            Tindakan egoismu membawa kehancuran pada dirimu sendiri. Kerajaan jatuh \
                            ke dalam kegelapan, dan namamu lenyap ditelan waktu.   
                            
                            PERMANINAN BERAKHIR""";
                } else {
                    return """
                            Kebaikan hatimu tak cukup menyelamatkanmu kali ini. \
                            Tapi mungkin, takdir bisa berbeda di jalur yang lain.
                            
                            PERMAINAN BERAKHIR""";
                }
            case VICTORY:
                // Teks kemenangan yang berbeda berdasarkan moralitas
                if (isCorruptedByMagic) {
                    return """
                            Sihir kuno mengalir deras dalam nadimu selagi kamu menghabisi sang naga. \
                            Kamu merebut seluruh harta karunnya, namun kutukan merayap dalam jiwamu. \
                            dan dunia akan segera gentar menyebut namamu...
                            
                            KEMENANGAN PENGHANCUR JIWA""";
                } else if (moralityScore > 30) {
                    return """
                            Dengan hati yang teguh dan jiwa yang bersih, kamu berhasil menaklukkan kegelapan. \
                            Namamu kini terukir dalam sejarah, dan kisahmu akan hidup dalam legenda.
                            
                            KAMU MENANG!""";
                } else {
                    return """
                            Dengan kecerdikan dan kelihaiannmu, kamu berhasil mengalahkan sang naga dan merebut \
                            harta karunnya. Namamu akan tercatat dalam lembaran sejarah.
                            
                            KAMU MENANG!""";
                }
                    case BETRAYAL_ENDING:
                        return """
                    Dengan senyum licik, kamu menikam Sang Raja dari belakang. \
                    Kepercayaan yang telah ia berikan kini berubah menjadi pengkhianatan berdarah. \
                    Kerajaan jatuh ke tanganmu, namun tidak ada sukacita dalam kemenangan ini. \
                    Bayangan pengkhianatan akan terus menghantui tahta yang kau rebut.
        
                    AKHIR PENGKHIANATAN - GAME OVER""";
            default:
                return "Error: Takdir yang hilang";
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
                                ChoiceType.HELP_VILLAGERS,
                                ChoiceType.STEAL_TREASURE
                        };
                    }
                }

            case CASTLE:
                if (befriendedKing) {
                    // Setelah kembali dari hutan dalam jalur pengkhianatan
                    if (hasWeapon) {
                        return new ChoiceType[]{
                                ChoiceType.FACE_DRAGON,
                                ChoiceType.BETRAY_KING
                        };
                    } else {
                        return new ChoiceType[]{
                                ChoiceType.FACE_DRAGON,
                                ChoiceType.CHALLENGE_KING
                        };
                    }
                } else {
                    return new ChoiceType[]{
                            ChoiceType.BEFRIEND_KING,
                            ChoiceType.CHALLENGE_KING
                    };
                }
            case FINAL_SHOWDOWN:
                return new ChoiceType[]{
                        ChoiceType.FACE_DRAGON,
                        ChoiceType.RETREAT
                };
            case BETRAYAL_ENDING:
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
            case EXPLORE_FOREST -> "Jelajahi hutan";
            case VISIT_CASTLE -> "Kunjungi kastil";
            case FIGHT_MONSTER -> "Lawan monster";
            case HELP_VILLAGERS -> "Bantu penduduk desa";
            case STEAL_TREASURE -> "Curi harta karun";
            case BEFRIEND_KING -> "Berteman dengan Raja";
            case CHALLENGE_KING -> "Tantang Sang Raja ";
            case FACE_DRAGON -> "Hadapi Sang Naga";
            case RETREAT -> "Kabur";
            case SEEK_ANCIENT_MAGIC -> "Cari sihir kuno";
            case TRAIN_WITH_VILLAGERS -> "Berlatih bersama penduduk";
            case BETRAY_KING -> "Mengkhianati Raja";
            default -> "Pilihan tidak diketahui";
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