package app.player;

public class ProPlayer extends Player {
    public ProPlayer(int id, String name) { super(id, name); }
    @Override public int maxActiveTourments() { return 5; }
    @Override public String type() { return "Pro"; }
}
