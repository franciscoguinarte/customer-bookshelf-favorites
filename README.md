# Customer Bookshelf API

Uma API RESTful desenvolvida em Java com Spring Boot para gerenciar clientes e suas listas de livros favoritos. Este projeto foi criado como parte de um desafio técnico e inclui funcionalidades robustas como processamento assíncrono e tolerância a falhas.

## Funcionalidades

- **Gerenciamento de Clientes (CRUD completo)**
  - Criação, busca, atualização e remoção de clientes.
  - Validação de e-mail e CPF (formato e unicidade).
  - Resposta de erro detalhada para dados de entrada inválidos.
  - CPF imutável após a criação.

- **Gerenciamento de Livros Favoritos**
  - Adicionar e remover livros da lista de favoritos de um cliente usando o ISBN.
  - Consulta paginada da lista completa de favoritos de um cliente.
  - Consulta de um livro específico na lista de favoritos.
  - **Cache em Banco de Dados:** A API externa (BrasilAPI) só é consultada uma vez por ISBN, e o resultado é salvo no banco de dados local.
  - **Cache em Memória:** As chamadas à API externa também são cacheadas em memória para performance máxima em buscas repetidas pelo mesmo ISBN.

- **Funcionalidades Avançadas**
  - **Adição em Massa Assíncrona:** Endpoint para adicionar múltiplos livros em segundo plano, com resposta imediata (HTTP 202 Accepted).
  - **Tolerância a Falhas:** Mecanismo de retentativas automáticas para chamadas à API externa.
  - **Logging Estruturado:** Logs detalhados em console e em arquivos com rotação diária.
  - **Testes Unitários:** Cobertura de testes para a camada de serviço, garantindo a lógica de negócio.

- **Ambiente Dockerizado**
  - Suporte completo a Docker e Docker Compose para um setup de desenvolvimento rápido e consistente.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3
- Spring Data JPA / Hibernate
- Spring Cache
- Spring Retry & Async
- MySQL
- Maven
- Lombok
- Logback (para Logging)
- JUnit 5 & Mockito (para Testes)
- Docker & Docker Compose
- Springdoc OpenAPI (Swagger)

---

## Como Executar

Existem duas maneiras de executar o projeto: usando Docker (recomendado) ou localmente.

### 1. Usando Docker (Recomendado)

Este é o método mais simples e garante que o ambiente seja idêntico ao de desenvolvimento.

**Pré-requisitos:**
- Docker
- Docker Compose

**Passos:**

1.  Clone este repositório.
2.  Na raiz do projeto, execute o seguinte comando:
    ```bash
    docker-compose up --build
    ```
3.  A aplicação e o banco de dados MySQL serão iniciados. A API estará disponível em `http://localhost:8080`.

4.  **Para visualizar os logs** da aplicação em tempo real, abra outro terminal e execute:
    ```bash
    docker-compose logs -f app
    ```

### 2. Localmente (Sem Docker)

**Pré-requisitos:**
- JDK 17
- Maven 3.8+
- Uma instância do MySQL 8 em execução.

**Passos:**

1.  Clone este repositório.
2.  Configure o acesso ao seu banco de dados local no arquivo `src/main/resources/application.properties`.
3.  Na raiz do projeto, execute o seguinte comando:
    ```bash
    mvn spring-boot:run
    ```
4.  A API estará disponível em `http://localhost:8080`.

---

## Documentação e Testes

### Documentação da API (Swagger)

Após iniciar a aplicação, a documentação interativa da API, gerada com Springdoc OpenAPI, estará disponível em:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Coleção Postman

Na raiz deste projeto, você encontrará o arquivo `customer-bookshelf.postman_collection.json`. Importe-o no seu Postman para ter acesso a uma coleção completa com todos os endpoints prontos para serem testados.

### Testes Unitários

Para executar a suíte de testes unitários, utilize o comando:
```bash
   mvn test
```

---

## Estratégia de Logs

A aplicação utiliza **SLF4J** com **Logback** para um logging estruturado e configurável.

- **Níveis de Log:**
  - `INFO`: Registra os principais eventos de negócio (criação de cliente, adição de livro, etc.) e requisições recebidas.
  - `DEBUG`: Fornece detalhes finos sobre a execução dos métodos nos serviços (útil para desenvolvimento).
  - `WARN`: Alertas sobre situações que não são erros, como retentativas de chamadas a APIs.
  - `ERROR`: Registra todas as exceções e falhas que ocorrem na aplicação.
- **Saída dos Logs:**
  - Os logs são exibidos no **console** durante a execução.
  - São também salvos em arquivos na pasta `/logs`, com uma política de rotação diária para evitar arquivos muito grandes.
- **Configuração:** O comportamento dos logs é controlado pelo arquivo `src/main/resources/logback-spring.xml`.

---

## Principais Endpoints

#### Clientes
- `POST /api/v1/customers`
- `GET /api/v1/customers` (Suporta paginação com `?page`, `size` e `sort`)
- `GET /api/v1/customers/{id}`
- `PUT /api/v1/customers/{id}`
- `DELETE /api/v1/customers/{id}`

#### Livros Favoritos
- `POST /api/v1/customers/{customerId}/favorites/{isbn}`
- `DELETE /api/v1/customers/{customerId}/favorites/{isbn}`
- `GET /api/v1/customers/{customerId}/favorites` (Suporta paginação com `?page`, `size` e `sort`)
- `GET /api/v1/customers/{customerId}/favorites/{isbn}`
- `POST /api/v1/customers/{customerId}/favorites/bulk-add`
