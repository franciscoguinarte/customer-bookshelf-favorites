# Customer Bookshelf API

Uma API RESTful desenvolvida em Java com Spring Boot para gerenciar clientes e suas listas de livros favoritos. Este projeto foi criado como parte de um desafio técnico e inclui funcionalidades robustas como processamento assíncrono, tolerância a falhas e segurança baseada em tokens.

## Funcionalidades

- **Segurança:** Autenticação M2M (máquina-para-máquina) via Bearer Token (JWT).
- **Gerenciamento de Clientes (CRUD completo)**
  - Criação, busca, atualização e remoção de clientes.
  - Validação de e-mail e CPF (formato e unicidade).
  - CPF imutável após a criação.
- **Gerenciamento de Livros Favoritos**
  - Adicionar e remover livros da lista de favoritos de um cliente usando o ISBN.
  - Consulta paginada da lista completa de favoritos de um cliente.
  - **Cache em Memória e Banco de Dados:** Otimização de performance para buscas de livros.
- **Funcionalidades Avançadas**
  - **Adição em Massa Assíncrona:** Endpoint para adicionar múltiplos livros em segundo plano.
  - **Tolerância a Falhas:** Mecanismo de retentativas automáticas para chamadas à API externa.
  - **Logging Estruturado:** Logs detalhados para monitoramento e depuração.
  - **Testes Unitários:** Cobertura de testes para a camada de serviço.
- **Ambiente Dockerizado**
  - Suporte completo a Docker e Docker Compose para um setup de desenvolvimento rápido.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA / Hibernate
- Spring Cache, Retry & Async
- JWT (JSON Web Tokens)
- MySQL, Maven, Lombok, Logback
- JUnit 5 & Mockito (para Testes)
- Docker & Docker Compose
- Springdoc OpenAPI (Swagger)

---

## Autenticação

A API utiliza um esquema de autenticação **Bearer Token** com JWT para comunicação máquina-para-máquina (M2M). Todas as requisições, exceto as de autenticação e documentação, exigem um token válido.

1.  **Obtenha um Token:** Primeiro, faça uma requisição `POST` para `/api/v1/auth/token` com o `clientId` e `clientSecret` da sua aplicação (definidos no `application.properties`).
2.  **Use o Token:** Inclua o token recebido no cabeçalho `Authorization` de todas as requisições subsequentes.
    - **Formato:** `Authorization: Bearer <seu_token_jwt>`

---

## Como Executar

Existem duas maneiras de executar o projeto: usando Docker (recomendado) ou localmente.

### 1. Usando Docker (Recomendado)

Este método garante um ambiente de execução consistente e isolado.

**Pré-requisitos:**
- JDK 17 e Maven 3.8+ (necessário para o script de build)
- Docker & Docker Compose

**Passos:**

1.  **Execute o script de automação:** Na raiz do projeto, execute o script correspondente ao seu sistema operacional.

    -   **Para Windows (CMD ou PowerShell):**
        ```bash
        .\build-and-run.bat
        ```

    -   **Para Linux ou macOS:**
        *Pode ser necessário dar permissão de execução ao script primeiro: `chmod +x build-and-run.sh`* e `chmod +x mvnw`*
        ```bash
        ./build-and-run.sh
        ```

2.  O script irá automaticamente construir o projeto com Maven e, em seguida, iniciar os contêineres do Docker. A API estará disponível em `http://localhost:8080`.

3.  **Para visualizar os logs** da aplicação em tempo real, abra outro terminal e execute:
    ```bash
    docker-compose logs -f app
    ```

### 2. Localmente (Sem Docker)

**Pré-requisitos:**
- JDK 17, Maven 3.8+, MySQL 8

**Passos:**

1.  Configure o acesso ao seu banco de dados no arquivo `src/main/resources/application.properties`.
2.  Na raiz do projeto, execute: `mvn spring-boot:run`

---

## Documentação e Testes

### Documentação da API (Swagger)

Após iniciar a aplicação, a documentação interativa da API estará disponível em:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Coleção Postman

Na raiz do projeto, você encontrará o arquivo `customer-bookshelf.postman_collection.json`. Ela está pré-configurada para lidar com a autenticação automaticamente. **Apenas execute a requisição `Get API Token` primeiro**, e o token será salvo e reutilizado em todas as outras chamadas.

### Testes Unitários

Para executar a suíte de testes unitários, utilize o comando: `mvn test`

---

## Estratégia de Logs

A aplicação utiliza **SLF4J** com **Logback**. Os logs são exibidos no console e também salvos em arquivos na pasta `/logs` com rotação diária. A configuração se encontra em `src/main/resources/logback-spring.xml`.

---

## Principais Endpoints

#### Autenticação
- `POST /api/v1/auth/token`

#### Clientes
- `POST /api/v1/customers`
- `GET /api/v1/customers` (Suporta paginação)
- `GET /api/v1/customers/{id}`
- `PUT /api/v1/customers/{id}`
- `DELETE /api/v1/customers/{id}`

#### Livros Favoritos
- `POST /api/v1/customers/{customerId}/favorites/bulk-add`
- `POST /api/v1/customers/{customerId}/favorites/{isbn}`
- `DELETE /api/v1/customers/{customerId}/favorites/{isbn}`
- `GET /api/v1/customers/{customerId}/favorites` (Suporta paginação)
- `GET /api/v1/customers/{customerId}/favorites/{isbn}`
