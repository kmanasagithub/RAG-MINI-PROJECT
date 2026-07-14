
# RAG Mini Project - Spring AI + Ollama + MariaDB Vector Store

## Overview

This project is a **Retrieval-Augmented Generation (RAG)** application built using:

* **Spring Boot**
* **Spring AI**
* **Ollama LLM**
* **MariaDB Vector Store**
* **JDBC Chat Memory**

The application allows users to ask questions about a company policy document. Instead of sending the complete PDF to the LLM every time, the application converts the PDF content into vector embeddings and stores them in MariaDB.

When a user asks a question, the application:

1. Searches the relevant information from the vector database.
2. Retrieves previous conversation history for that user.
3. Combines the retrieved document context and chat history.
4. Sends the enriched prompt to the LLM.
5. Returns the final answer.

---

# Architecture

```
                 Company Policy PDF
                         |
                         |
                         v
              PDF Document Loader
                         |
                         |
                         v
              Text Chunking / Splitting
                         |
                         |
                         v
              Embedding Generation
                         |
                         |
                         v
              MariaDB Vector Store
                         |
                         |
                         |
User Question ------------+
                         |
                         v
          Retrieval Augmentation Advisor
                         |
                         |
          +--------------+--------------+
          |                             |
          v                             v
   Vector Search                 Chat Memory
   (PDF Context)              (User History)
          |                             |
          +--------------+--------------+
                         |
                         v
                  Spring AI ChatClient
                         |
                         v
                    Ollama LLM
                         |
                         v
                    Final Response
```

---

# Features

## 1. PDF Based Question Answering

The application loads a company policy PDF and indexes its content into MariaDB.

Example questions:

```
What is the dress code policy?

How many annual leaves are provided?

What are the working hours?
```

The answer is generated from the relevant sections of the PDF.

---

## 2. Vector Search

The PDF is split into smaller chunks.

Each chunk is converted into embeddings using:

```
mxbai-embed-large
```

These embeddings are stored in MariaDB using Spring AI MariaDB Vector Store.

When a question is asked, similarity search retrieves the most relevant chunks.

---

## 3. Conversation Memory

The application stores user conversations using JDBC Chat Memory.

Each user has a unique conversation ID.

Example:

```
userId = ManasaUser
```

Conversation:

```
User:
I am Manasa Kurella

Assistant:
Nice to meet you Manasa.
```

Later:

```
User:
What is my name?
```

The assistant can use the previous conversation history.

---

# Technologies Used

| Technology        | Purpose                               |
| ----------------- | ------------------------------------- |
| Spring Boot       | Backend application                   |
| Spring AI         | AI integration framework              |
| Ollama            | Local LLM runtime                     |
| Mistral           | Chat model                            |
| mxbai-embed-large | Embedding model                       |
| MariaDB           | Vector database + chat memory storage |
| Docker            | Database containerization             |

---

# Prerequisites

Install the following:

## Java

Java 17 or higher

Verify:

```bash
java -version
```

---

## Docker

Verify:

```bash
docker --version
```

---

## Ollama

Install Ollama:

[https://ollama.com](https://ollama.com)

Verify:

```bash
ollama --version
```

---

# Running Locally

## 1. Clone Repository

```bash
git clone <repository-url>

cd rag-mini-project
```

---

# 2. Configure Environment Variables

Create a file named:

```
.env
```

in the project root.

Example:

```env
MYSQL_ROOT_PASSWORD=root1234
```

Do not commit this file to GitHub.

---

# 3. Start MariaDB using Docker

Run:

```bash
docker compose up -d
```

Check the container:

```bash
docker ps
```

You should see:

```
rag-mariadb
```

---

# 4. Start Ollama

Run:

```bash
ollama serve
```

Keep this terminal open.

---

# 5. Download Models

Download the chat model:

```bash
ollama pull mistral
```

Download the embedding model:

```bash
ollama pull mxbai-embed-large
```

Verify:

```bash
ollama list
```

---

# 6. Configure Application

`application.properties`

Example:

```properties
spring.ai.ollama.base-url=http://localhost:11434

spring.ai.ollama.chat.options.model=mistral

spring.ai.ollama.embedding.options.model=mxbai-embed-large


spring.datasource.url=jdbc:mariadb://localhost:3307/rag
spring.datasource.username=root
spring.datasource.password=root1234


spring.ai.vectorstore.mariadb.initialize-schema=true

spring.ai.vectorstore.mariadb.distance-type=COSINE


spring.ai.chat.memory.repository.jdbc.initialize-schema=always
```

---

# 7. Run Spring Boot Application

Using Maven:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

or:

```bash
mvn spring-boot:run
```

---

# API Usage

## Chat Endpoint

```
GET /chat
```

Parameters:

```
query
```

Example:

```
http://localhost:8080/chat?query=What is the dress code
```

Header:
```
userId: ManasaUser
```

Response:

```
Business casual attire is expected...
```

---

# First Application Startup

On the first startup:

1. PDF is loaded.
2. Text is extracted.
3. Text is split into chunks.
4. Embeddings are generated.
5. Vectors are stored in MariaDB.

Example log:

```
PDF indexing completed
```

On future startups:

```
PDF already indexed
```

The application skips duplicate indexing.

---

# Database Tables

Spring AI automatically creates:

## Vector Store Table

Stores:

* Document content
* Metadata
* Embeddings

## Chat Memory Tables

Stores:

* User messages
* Assistant responses
* Conversation IDs


# Future Improvements

Possible improvements:

* Add authentication and user management.
* Store user profiles separately from chat memory.
* Add multiple document support.
* Add document upload API.
* Add streaming responses.
* Deploy using Kubernetes.

---

# License

This project is for learning and demonstration purposes.

You can customize the repository URL, author name, and PDF description before pushing it to GitHub.
