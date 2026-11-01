package app.tourment;

import app.Storage;
import app.player.Player;
import app.player.PlayerService;
import app.exception.ConflictException;
import app.exception.NotFoundException;

public class RegistrationService {
    private final Storage store;
    private final PlayerService playerService;
    private final TourmentService tourmentService;

    public RegistrationService(Storage store, PlayerService playerService, TourmentService tourmentService) {
        this.store = store;
        this.playerService = playerService;
        this.tourmentService = tourmentService;
    }

    public Registration register(int tournamentId, int playerId) {
        Player player = playerService.getById(playerId);
        Tourment tourment = tourmentService.getById(tournamentId);

        boolean alreadyRegistered = store.registrations.values().stream()
                .anyMatch(regis -> regis.getTournamentId() == tournamentId && regis.getPlayerId() == playerId);
        if (alreadyRegistered) {
            throw new ConflictException("Игрок уже зарегистрирован на этот турнир");
        }

        if (player.getExp() < tourment.getMinExp()) {
            throw new ConflictException("Недостаточно опыта. Требуется: " + tourment.getMinExp() + ", у вас: " + player.getExp());
        }

        int id = store.registrationSeq.getAndIncrement();
        Registration reg = new Registration(id, tournamentId, playerId);
        store.registrations.put(id, reg);

        return reg;
    }

    public java.util.List<Registration> getAll() {
        return store.registrations.values().stream().toList();
    }
}