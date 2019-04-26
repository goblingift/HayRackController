/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Defines several enums, which contains sequences (Playtimes and waittimes)
 * for the sound-modules.
 * @author andre
 */
public enum Playlist implements Supplier<Playlist> {
    
    TRACK_1(1, "track_1", 500, 500, 150, 150, 10),
    TRACK_2(2, "track_2", 100, 50, 100, 300, 20),
    TRACK_3(3, "track_3", 500, 50, 500, 50, 10),
    TRACK_4(4, "track_4", 150, 150, 300, 300, 15),
    TRACK_5(5, "track_5", 50, 100, 100, 200, 25),
    TRACK_6(6, "track_6", 30, 30, 150, 150, 50);
    
    private int id;
    private String title;
    private int PLAYTIME_1;
    private int WAITTIME_1;
    private int PLAYTIME_2;
    private int WAITTIME_2;
    private int REPEATS;

    private static final List<Playlist> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    
    private Playlist(int id, String title, int PLAYTIME_1, int WAITTIME_1, int PLAYTIME_2,
            int WAITTIME_2, int REPEATS) {
        
        this.id = id;
        this.title = title;
        this.PLAYTIME_1 = PLAYTIME_1;
        this.WAITTIME_1 = WAITTIME_1;
        this.PLAYTIME_2 = PLAYTIME_2;
        this.WAITTIME_2 = WAITTIME_2;
        this.REPEATS = REPEATS;
    }

    /**
     * Returns a random playlist object.
     * @return a random playlist object.
     */
    public static Playlist getRandomPlaylist() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    public int getPLAYTIME_1() {
        return PLAYTIME_1;
    }

    public int getWAITTIME_1() {
        return WAITTIME_1;
    }

    public int getPLAYTIME_2() {
        return PLAYTIME_2;
    }

    public int getWAITTIME_2() {
        return WAITTIME_2;
    }

    public int getREPEATS() {
        return REPEATS;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    
    public static List<Playlist> getVALUES() {
        return VALUES;
    }
    
    /**
     * Find an item by the given identifier.
     * @param id
     * @return 
     */
    public static Optional<Playlist> findById(int id) {
        return VALUES.stream()
                .filter(p -> p.getId() == id)
                .findAny();
    }
    
    @Override
    public String toString() {
        return "Playlist{" + "PLAYTIME_1=" + PLAYTIME_1 + ", WAITTIME_1=" + WAITTIME_1 + ", PLAYTIME_2=" + PLAYTIME_2 + ", WAITTIME_2=" + WAITTIME_2 + ", REPEATS=" + REPEATS + '}';
    }

    @Override
    public Playlist get() {
        return getRandomPlaylist();
    }

}
