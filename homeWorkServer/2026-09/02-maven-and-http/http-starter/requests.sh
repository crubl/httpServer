#!/usr/bin/env bash

curl -i http://localhost:8080/ping

curl -i -X POST http://localhost:8080/ping

curl -X POST http://localhost:8080/tournaments \
  -H "Content-Type: application/json" \
  -d '{"type":"NOOB", "name":"For Beginners", "minExp":0}'

curl -X POST http://localhost:8080/players \
  -H "Content-Type: application/json" \
  -d '{"name":"Ivan", "type":"NOOB"}'

curl -X POST http://localhost:8080/registrations \
  -H "Content-Type: application/json" \
  -d '{"tournamentId":1, "playerId":1}'

curl -X POST http://localhost:8080/registrations \
  -H "Content-Type: application/json" \
  -d '{"tournamentId":1, "playerId":1}'

curl -X POST http://localhost:8080/tournaments \
  -H "Content-Type: application/json" \
  -d '{"type":"PRO", "name":"Pro League", "minExp":100}'

curl -X POST http://localhost:8080/registrations \
  -H "Content-Type: application/json" \
  -d '{"tournamentId":2, "playerId":1}'