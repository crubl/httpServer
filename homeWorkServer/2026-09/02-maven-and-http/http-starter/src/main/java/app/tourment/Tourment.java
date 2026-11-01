package app.tourment;

import app.exception.ValidationException;
import app.exception.ConflictException;

public abstract class Tourment {
    public enum Status {
        Open,
        Going,
        Finished
    }

    private final int id;
    private final int gameId;
    private final int minExp;
    private final int minPlayers;
    private final int duration;
    private Status status;
    private String name;


    protected Tourment(int id, int gameId, String name,
                       int minExp, int minPlayers, int duration) {
        this.id = id;
        this.gameId = gameId;
        this.minExp = minExp;
        this.minPlayers = minPlayers;
        this.name = name;
        this.duration = duration;
        this.status = Status.Open;
    }

    public int getId() { return id; }
    public int getGameId() { return gameId; }
    public String getName() { return name; }
    public int getMinExp() { return minExp;}
    public int getMinPlayers() { return minPlayers; }
    public int getDuration() { return duration; }
    public Status getStatus() { return status; }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("название турнира не может быть пустым");
        }
        this.name = name;
    }

    public void changeStatus() {
        if (status == Status.Open) { this.status = Status.Going; }
        else if (status == Status.Going) { this.status = Status.Finished; }
        else { throw new ConflictException("Турнир уже закончился и возобновить его нельзя"); }
    }

    public abstract String type();
}
