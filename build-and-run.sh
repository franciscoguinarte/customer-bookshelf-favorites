#!/bin/bash
set -e

echo "---> Passo 1 de 2: Construindo o projeto com Maven..."
./mvnw clean package

echo "\n---> Passo 2 de 2: Construindo a imagem Docker e iniciando os contêineres..."
docker-compose up --build

echo "\nProcesso concluído! A aplicação deve estar disponível em http://localhost:8080"

