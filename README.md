# go
go gameManager for studies

# How to run application?
use mvn package
to run server: java -cp .\target\Go-1.0-SNAPSHOT.jar Server 
to run clientj: java -cp .\target\Go-1.0-SNAPSHOT.jar lista4.Client 

--
klient podaje w formacie: "[A-Z] [0-9]"
mamy Adaptery między serwerem a grą 

--
Used design patterns:
- Observer (OutputGameAdapter has map of Observers (Clients) and send's information to them, for example the board after somebodies move)
- Adapter (It will be easier to extend application - now we added a adapters for go gameManager (one input, one output) with proper methods)
- State (Game has a state when it is waiting for 2 players, white move or black move or stop)