package app;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import app.player.PlayerService;
import app.tourment.RegistrationService;
import app.tourment.TourmentService;

import app.tourment.RegistrationHandler;
import app.tourment.TourmentHandler;
import app.player.PlayerHandler;



public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Каждый контекст — это префикс пути и обработчик для него.
        // Внимание: контекст "/ping" получит и запросы на "/ping/что-угодно".
        server.createContext("/ping", new PingHandler());

        Storage store = new Storage();

        PlayerService playerService = new PlayerService(store);
        TourmentService tourmentService = new TourmentService(store);
        RegistrationService registrationService = new RegistrationService(store, playerService, tourmentService);

        server.createContext("/players", new PlayerHandler(playerService));
        server.createContext("/tournaments", new TourmentHandler(tourmentService));
        server.createContext("/registrations", new RegistrationHandler(registrationService));

        server.start();
        System.out.println("Сервер запущен: http://localhost:" + PORT + "/ping");
    }
}

