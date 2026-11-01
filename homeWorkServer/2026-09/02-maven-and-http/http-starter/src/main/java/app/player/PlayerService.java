package app.player;

import app.Storage;
import app.exception.ValidationException;
import app.exception.NotFoundException;
import java.util.List;

public class PlayerService {
    private final Storage store;

    public PlayerService(Storage store) { this.store = store; }

    public List<Player> getAll(String type) {
        return store.players.values().stream()
                .filter(p -> type == null || p.type().equalsIgnoreCase(type))
                .toList();
    }

    public Player create(String name, String type) {
        if (name == null || name.isBlank()) { throw new ValidationException("name не должно быть пустым"); }

        int id = store.playerSeq.getAndIncrement();

        Player player = switch (type.toUpperCase()) {
            case "NOOB" -> new NoobPlayer(id, name);
            case "PRO" -> new ProPlayer(id, name);
            default -> throw new ValidationException("Неизвестный тип игрока: " + type);
        };

        store.players.put(id, player);
        return player;
    }

    public Player getById(int id) {
        if (id <= 0) { throw new ValidationException("id не может быть отрицательным"); }

        Player player = store.players.get(id);
        if (player == null) { throw new NotFoundException("Игрок с id " + id + " не найден"); }

        return player;
    }

    public Player update(int id, String newName) {
        Player player = getById(id);
        player.setName(newName);
        return player;
    }

    public void delete(int id) {
        getById(id);
        store.players.remove(id);
    }
}