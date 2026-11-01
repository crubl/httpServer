package app.player;

import app.exception.ConflictException;
import app.exception.NotFoundException;
import app.exception.ValidationException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class PlayerHandler implements HttpHandler {
    private final PlayerService service;
    private final JsonMapper mapper = JsonMapper.builder().build();

    public PlayerHandler(PlayerService service) { this.service = service; }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/players") || path.equals("/players/")) {
                if (method.equals("GET")) {
                    byte[] body = mapper.writeValueAsBytes(service.getAll(null));
                    sendResponse(exchange, 200, body);
                }
                else if (method.equals("POST")) {
                    Map<?, ?> bodyMap = readBody(exchange);
                    Player p = service.create((String) bodyMap.get("name"), (String) bodyMap.get("type"));
                    sendResponse(exchange, 201, mapper.writeValueAsBytes(p));
                }
                else { sendError(exchange, 405, "Method Not Allowed"); }
                return;
            }

            String[] parts = path.split("/");
            if (parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
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

    private Map<?, ?> readBody(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            return mapper.readValue(in, Map.class);
        }
    }
}