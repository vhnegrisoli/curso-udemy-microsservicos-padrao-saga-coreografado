# Projeto: Curso Udemy - Arquitetura de Microsserviços: Padrão Saga Coreografado

Repositório contendo o projeto de AULA BÔNUS desenvolvido do curso Arquitetura de Microsserviços: Padrão Saga Orquestrado, ministrado por mim para a plataforma Udemy.

Para acessar o curso na plataforma, basta acessar esta URL: https://www.udemy.com/course/arquitetura-microsservicos-padrao-saga-orquestrado/ (ainda não publicado)

## Tecnologias

* **Java 17**
* **Spring Boot 3**
* **Apache Kafka**
* **API REST**
* **PostgreSQL**
* **MongoDB**
* **Docker**
* **docker-compose**
* **Redpanda Console**

# Ferramentas utilizadas

* **IntelliJ IDEA Community Edition**
* **Docker**
* **Gradle**

# Arquitetura Proposta

No curso, desenvolveremos a seguinte aquitetura:

![Arquitetura](Conte%C3%BAdos/Arquitetura%20Proposta.png)

Em nossa arquitetura, teremos 4 serviços:

* **Order-Service**: microsserviço responsável apenas por gerar um pedido inicial, e receber uma notificação. Aqui que teremos endpoints REST para inciar o processo e recuperar os dados dos eventos. O banco de dados utilizado será o MongoDB.
* **Product-Validation-Service**: microsserviço responsável por validar se o produto informado no pedido existe e está válido. Este microsserviço guardará a validação de um produto para o ID de um pedido. O banco de dados utilizado será o PostgreSQL.
* **Payment-Service**: microsserviço responsável por realizar um pagamento com base nos valores unitários e quantidades informadas no pedido. Este microsserviço guardará a informação de pagamento de um pedido. O banco de dados utilizado será o PostgreSQL.
* **Inventory-Service**: microsserviço responsável por realizar a baixa do estoque dos produtos de um pedido. Este microsserviço guardará a informação da baixa de um produto para o ID de um pedido. O banco de dados utilizado será o PostgreSQL.

Todos os serviços da arquitetura irão subir através do arquivo **docker-compose.yml**.

## Execução do projeto

Há várias maneiras de executar os projetos:

1. Executando tudo via `docker-compose`
2. Executando tudo via `script` de automação que eu disponibilizei (`build.py`)
3. Executando apenas os serviços de bancos de dados e message broker (Kafka) separadamente
4. Executando as aplicações manualmente via CLI (`java -jar` ou `gradle bootRun` ou via IntelliJ)

Para rodar as aplicações, será necessário ter instalado:

* **Docker**
* **Java 17**
* **Gradle 7.6 ou superior**

### 01 - Execução geral via docker-compose

Basta executar o comando no diretório raiz do repositório:

`docker-compose up --build -d`

**Obs.: para rodar tudo desta maneira, é necessário realizar o build das 5 aplicações, veja nos passos abaixo sobre como fazer isto.**

### 02 - Execução geral via automação com script em Python

Basta executar o arquivo `build.py`. Para isto, **é necessário ter o Python 3 instalado**.

Para executar, basta apenas executar o seguinte comando no diretório raiz do repositório:

`python build.py`

Será realizado o `build` de todas as aplicações, removidos todos os containers e em sequência, será rodado o `docker-compose`.

### 03 - Executando os serviços de bancos de dados e Message Broker

Para que seja possível executar os serviços de bancos de dados e Message Broker, como MongoDB, PostgreSQL e Apache Kafka, basta ir no diretório raiz do repositório, onde encontra-se o arquivo `docker-compose.yml` e executar o comando:

`docker-compose up --build -d order-db kafka product-db payment-db inventory-db`

Como queremos rodar apenas os serviços de bancos de dados e Message Broker, é necessário informá-los no comando do `docker-compose`, caso contrário, as aplicações irão subir também.

Para parar todos os containers, basta rodar:

`docker-compose down` 

Ou então:

`docker stop ($docker ps -aq)`
`docker container prune -f`

### 04 - Executando manualmente via CLI

Antes da execução do projeto, realize o `build` da aplicação indo no diretório raiz e executando o comando:

`gradle build -x test`

Para executar os projetos com Gradle, basta entrar no diretório raiz de cada projeto, e executar o comando:

`gradle bootRun` 

Ou então, entrar no diretório: `build/libs` e executar o comando:

`java -jar nome_do_jar.jar`

### Acessando a aplicação

Para acessar as aplicações e realizar um pedido, basta acessar a URL:

http://localhost:3000/swagger-ui.html

Você chegará nesta página:

![Swagger](Conte%C3%BAdos/Documentacao.png)

As aplicações executarão nas seguintes portas:

* Order-Service: 3000
* Product-Validation-Service: 8090
* Payment-Service: 8091
* Inventory-Service: 8092
* Apache Kafka: 9092
* Redpanda Console: 8081
* PostgreSQL (Product-DB): 5432
* PostgreSQL (Payment-DB): 5433
* PostgreSQL (Inventory-DB): 5434
* MongoDB (Order-DB): 27017

### Acessando tópicos com Redpanda Console

Para acessar o Redpanda Console e visualizar tópicos e publicar eventos, basta acessar:

http://localhost:8081

Você chegará nesta página:

![Redpanda](Conte%C3%BAdos/Redpanda%20Kafka.png)

## Dados da API

É necessário conhecer o payload de envio ao fluxo da saga, assim como os produtos cadastrados e suas quantidades.

### Produtos registrados e seu estoque

Existem 3 produtos iniciais cadastrados no serviço `product-validation-service` e suas quantidades disponíveis em `inventory-service`: 

* **COMIC_BOOKS** (4 em estoque)
* **BOOKS** (2 em estoque)
* **MOVIES** (5 em estoque)
* **MUSIC** (9 em estoque)

### Endpoint para iniciar a saga:

**POST** http://localhost:3000/api/order

Payload:

```json
{
  "products": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.50
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.90
      },
      "quantity": 1
    }
  ]
}
```

Resposta:

```json
{
  "id": "65235b034a6fa17dc661679b",
  "products": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.5
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.9
      },
      "quantity": 1
    }
  ],
  "createdAt": "2023-10-09T01:44:35.655",
  "transactionId": "1696815875655_44ae5c2d-5549-427f-861c-9eef24676b7c",
  "totalAmount": 0,
  "totalItems": 0
}
```

### Endpoint para visualizar a saga:

É possível recuperar os dados da saga pelo **orderId** ou pelo **transactionId**, o resultado será o mesmo:

**GET** http://localhost:3000/api/event?orderId=65235b034a6fa17dc661679b

**GET** http://localhost:3000/api/event?transactionId=1696815875655_44ae5c2d-5549-427f-861c-9eef24676b7c

Resposta:

```json
{
  "id": "65235b034a6fa17dc661679c",
  "transactionId": "1696815875655_44ae5c2d-5549-427f-861c-9eef24676b7c",
  "orderId": "65235b034a6fa17dc661679b",
  "payload": {
    "id": "65235b034a6fa17dc661679b",
    "products": [
      {
        "product": {
          "code": "COMIC_BOOKS",
          "unitValue": 15.5
        },
        "quantity": 3
      },
      {
        "product": {
          "code": "BOOKS",
          "unitValue": 9.9
        },
        "quantity": 1
      }
    ],
    "createdAt": "2023-10-09T01:44:35.655",
    "transactionId": "1696815875655_44ae5c2d-5549-427f-861c-9eef24676b7c",
    "totalAmount": 56.4,
    "totalItems": 4
  },
  "source": "ORDER_SERVICE",
  "status": "SUCCESS",
  "eventHistory": [
    {
      "source": "ORDER_SERVICE",
      "status": "SUCCESS",
      "message": "Saga started!",
      "createdAt": "2023-10-09T01:44:35.728"
    },
    {
      "source": "PRODUCT_VALIDATION_SERVICE",
      "status": "SUCCESS",
      "message": "Products are validated successfully!",
      "createdAt": "2023-10-09T01:44:36.196"
    },
    {
      "source": "PAYMENT_SERVICE",
      "status": "SUCCESS",
      "message": "Payment realized successfully!",
      "createdAt": "2023-10-09T01:44:36.639"
    },
    {
      "source": "INVENTORY_SERVICE",
      "status": "SUCCESS",
      "message": "Inventory updated successfully!",
      "createdAt": "2023-10-09T01:44:37.117"
    },
    {
      "source": "ORDER_SERVICE",
      "status": "SUCCESS",
      "message": "Saga finished successfully!",
      "createdAt": "2023-10-09T01:44:37.21"
    }
  ],
  "createdAt": "2023-10-09T01:44:37.209"
}
```

### Acesso ao MongoDB

Para conectar-se ao MongoDB via linha de comando (cli) diretamente do docker-compose, basta executar o comando abaixo:

**docker exec -it order-db mongosh "mongodb://admin:123456@localhost:27017"**

Para listar os bancos de dados existentes:

**show dbs**

Para selecionar um banco de dados:

**use admin**

Para visualizar as collections do banco:

**show collections**

Para realizar queries e validar se os dados existem:

**db.order.find()**

**db.event.find()**

**db.order.find(id=ObjectId("65235b034a6fa17dc661679b"))**

**db.order.find({ "products.product.code": "COMIC_BOOKS"})**

## Autor

### Victor Hugo Negrisoli
### Desenvolvedor de Software Back-End
