package app.tourment;

public class NoobTourment extends Tourment {
    public NoobTourment(int id, int gameId, String name,
                                       int minExp, int minPlayers, int durationMinutes) {
        super(id, gameId, name, minExp, minPlayers, durationMinutes);
    }
    @Override public String type() { return "Noob"; }
}
