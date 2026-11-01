package app.player;

public class NoobPlayer extends Player {
    public NoobPlayer(int id, String name) { super(id, name); }
    @Override public int maxActiveTourments() { return 2; }
    @Override public String type() { return "Noob"; }
}
