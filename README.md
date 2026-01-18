# go
go game for studies

# How to run application?
- in yor console run:

git clone https://github.com/Krzesimir04/go_second_iteration

cd go_second_iteration 

mvn package

java -cp target/Go-1.0-SNAPSHOT.jar lista4.backend.Server

 - in other consoles (in the same directory with pom.xml file) run two GUI clients to play by using:

mvn javafx:run

--

To run console client in other consoles (in the same directory with pom.xml file) run two clients to play by using:

java -cp target/Go-1.0-SNAPSHOT.jar lista4.frontend.Client

--
Used design patterns:
- Observer (OutputGameAdapter has map of Observers (Clients) and send's information to them, for example the board after somebodies move)
- Adapter (It will be easier to extend application - now we added a adapters for go gameManager (one input, one output) with proper methods)
- State (Game has a state when it is waiting for 2 players, white move or black move or stop)