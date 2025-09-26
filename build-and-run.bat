@echo off

ECHO ---> Passo 1 de 2: Construindo o projeto com Maven...

CALL mvnw.cmd clean package

IF %ERRORLEVEL% NEQ 0 (
    ECHO.
    ECHO O build do Maven falhou. Abortando.
    EXIT /B %ERRORLEVEL%
)

ECHO.
ECHO ---> Passo 2 de 2: Construindo a imagem Docker e iniciando os contêineres...

docker-compose up --build

ECHO.
ECHO Processo concluído! A aplicação deve estar disponível em http://localhost:8080
