package app.tourment;

import app.Storage;
import app.exception.NotFoundException;
import app.exception.ValidationException;
import java.util.List;

public class TourmentService {
    private final Storage store;

    public TourmentService(Storage store) {
        this.store = store;
    }

    public List<Tourment> getAll(String type) {
        return store.tourments.values().stream()
                .filter(t -> type == null || t.type().equalsIgnoreCase(type))
                .toList();
    }

    public Tourment getById(int id) {
        if (id <= 0) { throw new ValidationException("id должен быть положительным"); };

        Tourment tourment = store.tourments.get(id);
        if (tourment == null) { throw new NotFoundException("Турнир с id " + id + " не найден"); };
        return tourment;
    }

    public Tourment create(String type, String name,  int gameId, int minExp, int minPlayers, int duration) {
        if (name == null || name.isBlank()) { throw new ValidationException("Название турнира не может быть пустым"); }
        if (type == null || type.isBlank()) { throw new ValidationException("Тип турнира не может быть пустым"); }

        int id = store.tourmentSeq.getAndIncrement();

        Tourment tourment = switch (type.toUpperCase()) {
            case "NOOB" -> new NoobTourment(id, gameId, name, minExp, minPlayers, duration);
            case "PRO" -> new ProTourment(id, gameId, name, minExp, minPlayers, duration);
            default -> throw new ValidationException("Неизвестный тип турнира: " + type);
        };

        store.tourments.put(id, tourment);
        return tourment;
    }

    public Tourment update(int id, String newName) {
        Tourment tourment = getById(id);
        if (newName != null && !newName.isBlank()) { tourment.setName(newName); }
        return tourment;
    }

    public void delete(int id) {
        getById(id);
        store.tourments.remove(id);
    }
}