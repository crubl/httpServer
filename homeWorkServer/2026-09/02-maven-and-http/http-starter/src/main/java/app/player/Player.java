package app.player;

import app.exception.ValidationException;

public abstract class Player {
    private final int id;
    private String name;
    private long exp;

    protected Player(int id, String name) {
        this.id = id;
        if (name == null || name.isBlank()) {
            throw new ValidationException("Имя игрока не может быть пустым");
        }
        this.name = name;
        this.exp = 0;
    }

    public int getID() { return id;}
    public String getName() { return name; }
    public long getExp() { return exp; }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Имя игрока не может быть пустым");
        }
        this.name = name;
    }

    public void addExp(long time) {
        if (time < 0) {
            throw new ValidationException("Опыт не может быть отрицательным");
        }
        this.exp += time;
    }

    public abstract int maxActiveTourments();
    public abstract String type();
}
