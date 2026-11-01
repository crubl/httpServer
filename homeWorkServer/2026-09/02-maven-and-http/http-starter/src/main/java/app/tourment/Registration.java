package app.tourment;

public class Registration {
    private final int id;
    private final int tournamentId;
    private final int playerId;

    public Registration(int id, int tournamentId, int playerId) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.playerId = playerId;
    }

    public int getId() { return id; }
    public int getTournamentId() { return tournamentId; }
    public int getPlayerId() { return playerId; }
}