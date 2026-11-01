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

public class TourmentHandler implements HttpHandler {
    private final TourmentService service;
    private final JsonMapper mapper = JsonMapper.builder().build();

    public TourmentHandler(TourmentService service) { this.service = service; }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/tournaments") || path.equals("/tournaments/")) {
                if (method.equals("GET")) {
                    sendResponse(exchange, 200, mapper.writeValueAsBytes(service.getAll(null)));
                }
                else if (method.equals("POST")) {
                    Map<String, Object> bodyMap = readBody(exchange);
                    Tourment t = service.create(
                            (String) bodyMap.get("type"),
                            (String) bodyMap.get("name"),
                            (int) bodyMap.getOrDefault("gameId", 0),
                            (int) bodyMap.getOrDefault("minExp", 0),
                            (int) bodyMap.getOrDefault("minPlayers", 2),
                            (int) bodyMap.getOrDefault("duration", 7)
                    );
                    sendResponse(exchange, 201, mapper.writeValueAsBytes(t));
                }
                else { sendError(exchange, 405, "Method Not Allowed"); }
                return;
            }

            String[] parts = path.split("/");
            if (parts.length >= 3) {
                int id = Integer.parseInt(parts[2]);

                if (parts.length == 4 && parts[3].equals("status") && method.equals("POST")) {
                    Tourment t = service.getById(id);
                    t.changeStatus();
                    sendResponse(exchange, 200, mapper.writeValueAsBytes(t));
                    return;
                }

                if (method.equals("GET")) {
                    sendResponse(exchange, 200, mapper.writeValueAsBytes(service.getById(id)));
                }
                else if (method.equals("DELETE")) {
                    service.delete(id);
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                }
                else if (method.equals("PUT")) {
                    Map<?, ?> bodyMap = readBody(exchange);
                    sendResponse(exchange, 200, mapper.writeValueAsBytes(service.update(id, (String) bodyMap.get("name"))));
                }
                else { sendError(exchange, 405, "Method Not Allowed"); }
                return;
            }

            sendError(exchange, 404, "Not Found");
        }
        catch (NotFoundException exception) {
            sendError(exchange, 404, exception.getMessage());
        }
        catch (ConflictException exception) {
            sendError(exchange, 409, exception.getMessage());
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