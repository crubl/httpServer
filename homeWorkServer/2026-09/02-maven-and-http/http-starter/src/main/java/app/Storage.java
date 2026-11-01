package app;
import app.player.Player;
import app.tourment.Tourment;
import app.tourment.Registration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Storage {
    public final ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, Tourment> tourments = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, Registration> registrations = new ConcurrentHashMap<>();

    public final AtomicInteger playerSeq = new AtomicInteger(1);
    public final AtomicInteger tourmentSeq = new AtomicInteger(1);
    public final AtomicInteger registrationSeq = new AtomicInteger(1);
}
