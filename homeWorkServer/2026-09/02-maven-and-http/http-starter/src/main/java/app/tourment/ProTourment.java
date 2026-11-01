package app.tourment;

public class ProTourment extends Tourment {
    public ProTourment(int id, int gameId, String name,
                        int minExp, int minPlayers, int durationMinutes) {
        super(id, gameId, name, minExp, minPlayers, durationMinutes);
    }
    @Override public String type() { return "Pro"; }
}