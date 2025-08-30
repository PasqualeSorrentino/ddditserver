# Dddit Server

<p align="center"><img src='https://i.postimg.cc/QxnvK4LL/dddit-upscaled.png' alt="Quixel_Texel_Logo" height="400"></p>

## 👋 Author

**Angelo Antonio Prisco** - [AngeloAntonioPrisco](https://github.com/AngeloAntonioPrisco)  

At the moment, I am the only contributor to this project.  
I am a student at **University of Salerno (UNISA)**, currently enrolled in the Master's program in **Software Engineering**.

## 📌 What is it?

**Dddit Server** is a **Java Spring** project designed to provide *versioning services* for **3D resources**.  
Currently, it supports:

- **FBX models** versioning
- **Materials** versioning, understood as sets of PNG textures  

Main features include:
- Creating and managing **repositories**  
- Handling **resources**, **branches**, and **versions**  
- Each version includes **author**, **comment** and additional metadata  
- A **repository invitations system** to enable collaboration among multiple users on the same repo

## 🚀 How to try it

The server is intended to run on an **Azure VM**, but it can also be executed locally using **IntelliJ IDEA**.

### Run locally
1. Clone the repo:
   ```bash
   git clone https://github.com/AngeloAntonioPrisco/ddditserver.git
   ```

2. Open the project in IntelliJ.

3. Edit the Run/Debug configuration to load environment variables from a custom *.env* file, like:
    ```bash
    GREMLIN_USERNAME="gremlin-username"
    GREMLIN_KEY="gremlin-key"

    COSMOS_SQL_ENDPOINT="cosmos-sql-endpoint"
    COSMOS_SQL_KEY="cosmos-sql-key"
    COSMOS_SQL_DATABASE="cosmos-sql-database"
    COSMOS_SQL_CONTAINER_VERSIONS="cosmos-sql-container-versions"
    COSMOS_SQL_CONTAINER_TOKEN_BLACKLIST="cosmos-sql-token-blacklist"

    BLOB_STORAGE_CONNECTION_STRING="blob-storage-connection-string"
    BLOB_STORAGE_CONTAINER_MESHES="blob-storage-container-meshes"
    BLOB_STORAGE_CONTAINER_MATERIALS="blob-storage-container-materials"

    JWT_SECRET="jwt-secret"

    MODELS_FOLDER_PATH="models-folder-path"
    FROM_EMAIL="from-email"
    TO_EMAIL="to-email"
    APP_PASSWORD="app-password"
    ```
4. Start the application from IntelliJ.

**Notes**: At the moment, one of the classes in the project, **CosmosVersionRepositoryImpl**, contains a `saveVersion` method that builds an ad-hoc Cosmos DB URL pointing to the developer’s personal Azure account.  
If you want to use your own Azure setup, you should update this return operation so that it conforms to your Azure account configuration.

## 🧱 Built With

- [Java](https://www.oracle.com/java/) – Programming language used for the server implementation.  
- [Spring Framework](https://spring.io/projects/spring-framework) – Provides dependency injection, REST APIs, and overall application structure.  
- [JUnit](https://junit.org/junit5/) – Framework for unit testing Java code.  
- [Mockito](https://site.mockito.org/) – Library for mocking objects in unit tests.  
- [Azure Cosmos DB](https://azure.microsoft.com/services/cosmos-db/) – Globally distributed NoSQL database used for storing resources and versions.  
- [Azure Gremlin DB](https://learn.microsoft.com/azure/cosmos-db/graph/graph-introduction) – Graph database used for modeling relationships between entities.  
- [Azure Blob Storage](https://azure.microsoft.com/services/storage/blobs/) – Storage service for 3D models and texture files.  
- [JWT (JSON Web Tokens)](https://jwt.io/) – Used for authentication and securing API access.
- [Jakarta Mail](https://eclipse-ee4j.github.io/mail/) – Used for AI module monitoring and alerting via email.
- [Eclipse Angus](https://projects.eclipse.org/projects/ee4j.angus) – Implementation of Jakarta Mail and Activation APIs.


## 🔗 Related resources
- [Dddit Client](https://github.com/AngeloAntonioPrisco/ddditclient): The official Python client to interact with the Dddit Server APIs, useful for testing and consuming the server's functionalities.
