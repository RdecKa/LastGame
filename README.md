# LastGame
Assignment #5 for Computer Graphics (Reykjavik University)  
Karin Frlic, November 2017

## Aim of the Game
Get to the star before your opponent does.

## How to run (Linux)
1. Open terminal in the root directory. Run `cd serverCode && javac GameServer.java && java GameServer`
2. Open two new terminals in the root directory. Run `gradle run` in both of them.
3. Both players (in both windows) should see the opponent now. If this is not the case, close both running games and run `gradle run` again.

### Using network
It is possible to play the game on two computers:
1. Start the server code on your server.
2. Change line 34 in `LastGame/core/src/com/ru/tgra/network/GameClient.java`: Replace `localhost` with the address of your server.
3. Open two terminals (on different computers) in the root directory. Run `gradle run` in both of them.

## How to play
* Press **V** to switch between first and third person view.
* Press **R** to reset the level if you get stuck.

### First Person View
* Use **WASD** to *move* around.
* Use **arrow keys** to *look* around.
* Press **SPACE** to shoot.

### Third Person View
* Use **arrow keys** to zoom in/out and move around the maze.
* You cannot move around in third person view.
