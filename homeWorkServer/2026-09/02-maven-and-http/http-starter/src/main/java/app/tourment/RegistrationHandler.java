package app.tourment;

import app.exception.ConflictException;
import app.exception.NotFoundException;
import app.exception.ValidationException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class RegistrationHandler implements HttpHandler {
    private final RegistrationService service;
    private final JsonMapper mapper = JsonMapper.builder().build();

    public RegistrationHandler(RegistrationService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/registrations") && method.equals("POST")) {
                Map<String, Object> bodyMap = readBody(exchange);
                int tournamentId = (int) bodyMap.getOrDefault("tournamentId", 0);
                int playerId = (int) bodyMap.getOrDefault("playerId", 0);

                Registration reg = service.register(tournamentId, playerId);
                sendResponse(exchange, 201, mapper.writeValueAsBytes(reg));

            }
            else if (path.equals("/registrations") && method.equals("GET")) {
                sendResponse(exchange, 200, mapper.writeValueAsBytes(service.getAll()));
            }
            else { sendError(exchange, 405, "Method Not Allowed"); }
        }
        catch (NotFoundException exception) {
            sendError(exchange, 404, exception.getMessage());
        }
        catch (ConflictException exception) {
            sendError(exchange, 409, exception.getMessage()); // 409 - это конфликт (уже записан, мало exp)
        }
        catch (ValidationException exception) {
            sendError(exchange, 400, exception.getMessage());
        }
        catch (Exception exception) {
            sendError(exchange, 500, "Internal Error");
        }
        finally {
            exchange.close();
        }
    }

    private void sendResponse(HttpExchange exchange, int status, byte[] body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream out = exchange.getResponseBody()) { out.write(body); }
    }

    private void sendError(HttpExchange exchange, int status, String message) throws IOException {
        sendResponse(exchange, status, mapper.writeValueAsBytes(Map.of("error", message)));
    }

    private Map<String, Object> readBody(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            return mapper.readValue(in, new TypeReference<Map<String, Object>>() {});
        }
    }
}