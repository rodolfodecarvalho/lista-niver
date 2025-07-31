# API de Pessoas

## Descrição
Esta API fornece endpoints para gerenciamento completo de pessoas no sistema, incluindo operações de criação, busca, atualização e exclusão (CRUD).

## Endpoints Disponíveis

### 1. Criar Nova Pessoa
- **Método:** `POST /pessoas`
- **Descrição:** Cria uma nova pessoa no sistema
- **Corpo da Requisição:** `PessoaInputDTO`
- **Respostas:**
    - `201 Created` - Pessoa criada com sucesso
    - `400 Bad Request` - Data inválida
    - `422 Unprocessable Entity` - Dados inválidos
    - `409 Conflict` - Pessoa já existe

### 2. Buscar Pessoa por ID
- **Método:** `GET /pessoas/{id}`
- **Descrição:** Busca uma pessoa específica pelo ID
- **Parâmetros:**
    - `id` (Path) - ID da pessoa (obrigatório)
- **Respostas:**
    - `200 OK` - Pessoa encontrada
    - `404 Not Found` - Pessoa não encontrada

### 3. Listar Todas as Pessoas
- **Método:** `GET /pessoas`
- **Descrição:** Retorna uma lista com todas as pessoas cadastradas
- **Respostas:**
    - `200 OK` - Lista de pessoas retornada com sucesso

### 4. Atualizar Pessoa
- **Método:** `PUT /pessoas/{id}`
- **Descrição:** Atualiza os dados de uma pessoa existente
- **Parâmetros:**
    - `id` (Path) - ID da pessoa (obrigatório)
- **Corpo da Requisição:** `PessoaUpdateDTO`
- **Respostas:**
    - `200 OK` - Pessoa atualizada com sucesso
    - `400 Bad Request` - Data inválida
    - `422 Unprocessable Entity` - Dados inválidos
    - `404 Not Found` - Pessoa não encontrada
    - `409 Conflict` - Pessoa com dados duplicados já existe

### 5. Deletar Pessoa
- **Método:** `DELETE /pessoas/{id}`
- **Descrição:** Remove uma pessoa do sistema
- **Parâmetros:**
    - `id` (Path) - ID da pessoa (obrigatório)
- **Respostas:**
    - `204 No Content` - Pessoa deletada com sucesso
    - `404 Not Found` - Pessoa não encontrada

### 6. Buscar Pessoas por Nome
- **Método:** `GET /pessoas/buscar`
- **Descrição:** Busca pessoas que contenham o nome especificado
- **Parâmetros:**
    - `nome` (Query) - Nome ou parte do nome da pessoa (obrigatório)
- **Respostas:**
    - `200 OK` - Lista de pessoas encontradas

## Modelos de Dados

### PessoaInputDTO
DTO utilizado para criação de uma nova pessoa.

### PessoaUpdateDTO
DTO utilizado para atualização de dados de uma pessoa existente.

### PessoaOutputDTO
DTO de resposta contendo os dados da pessoa.

## Códigos de Status HTTP

| Código | Descrição                                               |
|--------|---------------------------------------------------------|
| 200    | OK - Operação realizada com sucesso                     |
| 201    | Created - Recurso criado com sucesso                    |
| 204    | No Content - Operação realizada sem conteúdo de retorno |
| 400    | Bad Request - Data inválida na requisição               |
| 422    | Unprocessable Entity - Dados inválidos na requisição    |
| 404    | Not Found - Recurso não encontrado                      |
| 409    | Conflict - Conflito com dados existentes                |

## Validações

A API implementa validações automáticas utilizando Bean Validation (`@Valid`). Requisições com dados inválidos retornarão erro 400 com detalhes sobre os problemas encontrados.

## Tratamento de Erros

A API utiliza o padrão RFC 7807 (Problem Details) para retorno de erros, fornecendo informações estruturadas sobre problemas encontrados durante o processamento das requisições.

## Documentação Swagger

Esta API está documentada com Swagger/OpenAPI 3.0, permitindo visualização interativa dos endpoints e execução de testes diretamente na interface de documentação.
- http://localhost:8080/swagger-ui/index.html
## Exemplo de Uso

```bash
# Criar uma pessoa
curl -X POST http://localhost:8080/pessoas \
  -H "Content-Type: application/json" \
  -d '{"nome": "João Silva", "email": "joao@email.com"}'

# Buscar pessoa por ID
curl -X GET http://localhost:8080/pessoas/1

# Listar todas as pessoas
curl -X GET http://localhost:8080/pessoas

# Buscar pessoas por nome
curl -X GET http://localhost:8080/pessoas/buscar?nome=João

# Atualizar pessoa
curl -X PUT http://localhost:8080/pessoas/1 \
  -H "Content-Type: application/json" \
  -d '{"nome": "João Santos", "email": "joao.santos@email.com"}'

# Deletar pessoa
curl -X DELETE http://localhost:8080/pessoas/1
```

## Docker e Containerização

### Executando com Docker Compose

O projeto inclui configuração completa para execução com Docker, incluindo banco PostgreSQL.

#### Pré-requisitos
- Docker
- Docker Compose

#### Comandos para execução:

```bash
# Construir e executar todos os serviços
docker-compose up --build

# Executar em background
docker-compose up -d

# Parar os serviços
docker-compose down

# Parar e remover volumes (limpa dados do banco)
docker-compose down -v

# Ver logs da aplicação
docker-compose logs -f app

# Ver logs do banco
docker-compose logs -f postgresql
```

### Serviços Configurados

#### 1. PostgreSQL Database
- **Container:** `lista-db`
- **Porta:** `5432`
- **Database:** `lista_db`
- **Usuário:** `postgres`
- **Senha:** `postgres`
- **Volume:** Dados persistidos em `postgres_data`
- **Health Check:** Verificação automática de saúde

#### 2. Aplicação Spring Boot
- **Container:** `lista-app`
- **Porta:** `8080`
- **Profile:** `docker`
- **Dependência:** Aguarda PostgreSQL estar saudável
- **Logs:** Mapeados para `./logs`
- **Restart:** `unless-stopped`

### Dockerfile Multi-stage

O projeto utiliza build multi-stage para otimização:

1. **Stage Builder:** Compila a aplicação com JDK 21
2. **Stage Runtime:** Executa com JRE 21 Alpine (menor footprint)

#### Características de Segurança:
- Usuário não-root (`appuser`)
- Imagem Alpine (menor superfície de ataque)
- Health check configurado

#### Otimizações JVM:
- Garbage Collector G1
- String Deduplication
- Memory settings otimizados

### Executando apenas o banco

```bash
# Apenas PostgreSQL para desenvolvimento local
docker-compose up postgresql
```

### Variáveis de Ambiente

| Variável | Valor Padrão | Descrição |
|----------|--------------|----------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgresql:5432/lista_db` | URL do banco |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Senha do banco |
| `SPRING_PROFILES_ACTIVE` | `docker` | Profile ativo |
| `TZ` | `America/Sao_Paulo` | Timezone |

### Monitoramento

- **Health Check:** `/actuator/health`
- **Logs:** Disponíveis via `docker-compose logs`
- **Métricas:** Actuator endpoints disponíveis

### Desenvolvimento

Para desenvolvimento local com banco Docker:

```bash
# 1. Subir apenas o banco
docker-compose up postgresql

# 2. Executar aplicação localmente
./mvnw spring-boot:run
```

## Tecnologias Utilizadas

- **Backend:** Spring Boot 3.5.3, Java 21
- **Web:** Spring Web, Bean Validation
- **Banco:** PostgreSQL 17.5, Spring Data JPA
- **Documentação:** Swagger/OpenAPI 3.0
- **Containerização:** Docker, Docker Compose
- **Build:** Maven, Multi-stage Dockerfile
- **Monitoramento:** Spring Actuator