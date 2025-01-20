# Simple Service Mesh Menager - Network Technology Project
Service Mesh Manager enables easy scaling of services. In our project, we use Java to create basic services. Initially, we built a basic CNAPP. In this basic setup, our API Gateway connected directly to the services and started all microservices. Once our CNAPP was completed, we began building the Service Mesh.

## Authors
* [Kacper Ziubiński](https://github.com/kacperziubinsky)
* [Bartłomiej Adamiak](https://github.com/MTXX-op)
* [Oskar Jakimiak](https://github.com/OskarJakimiak1405)

## Technologies
* Java 22
* MySQL

## Project Structure

```

Network-Project/
|-- .git/
|-- .gitignore
|-- .idea/
|-- ManagerLogger.log
|-- mysql-connector-j-9.1.0.jar
|-- out/
|-- README.md
|-- src/
    |-- Agent.java
    |-- ApiGateway.java
    |-- Client.java
    |-- Clientdata/
    |-- DBHandler.java
    |-- FileService.java
    |-- LoginService.java
    |-- PostService.java
    |-- RegisterService.java
    |-- ServerData/
    |-- Service.java
    |-- ServiceMeshManager.java
|-- untitled.iml

```

## Key Directories and Files

* src/: Contains the source code for the project.
  ** ApiGateway.java: Handles routing and forwarding client requests to appropriate services.
  ** ServiceMeshManager.java: Central component managing the Service Mesh.
  ** DBHandler.java: Manages database interactions.
  ** Agent.java: Represents an individual microservice.
  ** FileService.java, LoginService.java, PostService.java, RegisterService.java: Implement various microservice functionalities.
* mysql-connector-j-9.1.0.jar: JDBC driver for MySQL database connections.
* README.md: Project description and credits.
* ManagerLogger.log: Log file for tracking application events.


## Showcase

![Schemat (2)](https://github.com/user-attachments/assets/c44240bc-7cd5-40d5-bcb6-21a1b308983d)

### ServiceMesh Basic Configuration
This is a console content:
```LoginService started on port: 3001
Started LoginService on port 3001
Started LoginService on port 3002
Started ApiGateway on port 3003
LoginService started on port: 3002
Started RegisterService on port 2139
ApiGateway started on port: 3003
Started RegisterService on port 2132
RegisterService started on port: 2139
Started RegisterService on port 2138
RegisterService started on port: 2132
Started PostService on port 2111
RegisterService started on port: 2138
Port discovery service started on port 2137
PostService started on port: 2111
```

### Client Login
#### User login for admin account.
```

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Pliki
5. Wyjście
Wybierz opcje (1-4): 1
Podaj login: admin
Podaj hasło: admin

Odpowiedź: Login successful
```

### Client Register
#### User register for account.
```

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Pliki
5. Wyjście
Wybierz opcje (1-4): 2
Podaj login do rejestracji: wyspa
Podaj hasło do rejestracji: wyspa

Odpowiedź: Registration successful!
```

### Client Posts
#### If User isn't logged in, he or she cannot select the option related to adding posts.
```

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Pliki
5. Wyjście
Wybierz opcje (1-4): 3

Musisz być zalogowany, aby wybrać inne opcje.

```

#### If User is logged in, he or she can select every option.
```

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Pliki
5. Wyjście
Wybierz opcje (1-4): 3

=== Menu Postów ===
1. Zobacz wszystkie posty
2. Stwórz nowy post
3. Powrót do menu
Wybierz opcje (1-3):

```
#### Client wants to write post.

```
=== Menu Postów ===
1. Zobacz wszystkie posty
2. Stwórz nowy post
3. Powrót do menu
Wybierz opcje (1-3): 2
Napisz post: To jest post

Response: Post successful

=== Menu Postów ===
1. Zobacz wszystkie posty
2. Stwórz nowy post
3. Powrót do menu
Wybierz opcje (1-3): 

```
#### Client wants to show posts.

```

=== Menu Postów ===
1. Zobacz wszystkie posty
2. Stwórz nowy post
3. Powrót do menu
Wybierz opcje (1-3): 1

Posty:
- ID: 1 Autor: a Zawartosc postu: b
- ID: 2 Autor: dzban Zawartosc postu: sfdfgdfhj
- ID: 3 Autor: dzban Zawartosc postu: I LOVE SIECI
- ID: 6 Autor: admin Zawartosc postu: sigma
- ID: 7 Autor: admin Zawartosc postu: costam test sigma rel
- ID: 9 Autor: skkf Zawartosc postu: okej
- ID: 12 Autor: admin Zawartosc postu: bvcfcfkjgvb
- ID: 17 Autor: oskar Zawartosc postu: elo elo
- ID: 18 Autor: adminTo+jest+post Zawartosc postu:
- ID: 19 Autor: a Zawartosc postu: ala ma kota
- ID: 20 Autor: a Zawartosc postu: To jest post

=== Menu Postów ===
1. Zobacz wszystkie posty
2. Stwórz nowy post
3. Powrót do menu
Wybierz opcje (1-3):

```

### Client Status
#### Client wants to see is he/she logged in or not.
Not logged in

```

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Status
5. Wyjście
Wybierz opcje (1-4): 4

=== Status Użytkownika ===
Nie jesteś zalogowany.

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Status
5. Wyjście
Wybierz opcje (1-4):

```

Logged in

```

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Status
5. Wyjście
Wybierz opcje (1-4): 4

=== Status Użytkownika ===
Nazwa użytkownika: admin
Status: Zalogowany

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Status
5. Wyjście
Wybierz opcje (1-4):

```

#### There is also 1 option, that User cannot register again, when he/she is logged in.

```

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Status
5. Wyjście
Wybierz opcje (1-4): 4

=== Status Użytkownika ===
Nazwa użytkownika: admin
Status: Zalogowany

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Status
5. Wyjście
Wybierz opcje (1-4): 2

Błąd: Jesteś już zalogowany. Wyloguj się, aby móc się zarejestrować.

=== Projekt Service Mesh Console ===
1. Logowanie
2. Rejestracja
3. Posty
4. Status
5. Wyjście
Wybierz opcje (1-4):

```



## Sources
This app is inspired by [@sambrosz](https://github.com/sambrosz/SSMMP-a-simple-protocol-for-Service-Mesh-management) lectures. 
