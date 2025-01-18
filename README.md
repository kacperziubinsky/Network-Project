# Simple Service Mesh Menager - Network Technology Project
Service Mesh Manager enables easy scaling of services. In our project, we use Java to create basic services. Initially, we built a basic CNAPP. In this basic setup, our API Gateway connected directly to the services and started all microservices. Once our CNAPP was completed, we began building the Service Mesh.

## Authors
* [Kacper Ziubiński](https://github.com/kacperziubinsky)
* [Bartłomiej Adamiak](https://github.com/MTXX-op)
* [Oskar Jakimiak](https://github.com/OskarJakimiak1405)

## Technologies
* Java 22
* MySQL

## Showcase


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


## Sources
This app is inspired by [@sambrosz](https://github.com/sambrosz/SSMMP-a-simple-protocol-for-Service-Mesh-management) lectures. 
