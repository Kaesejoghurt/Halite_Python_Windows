import hlt.*;

import java.util.ArrayList;
import java.util.Iterator;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("TheSwarm");

        // We now have 1 full minute to analyse the initial map.
        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        Log.log(initialMapIntelligence);

        final ArrayList<Move> moveList = new ArrayList<>();
        for (;;) {
            moveList.clear();
            networking.updateMap(gameMap);
            
            int undocked_ship_counter = 0;
            Planet localDestinationPlanet;
            int localDestinationCounter = 0;
            int localVelocity =  Constants.MAX_SPEED;
            		
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }

				undocked_ship_counter  ++;
                
                for (Planet planet : gameMap.getAllPlanets().values()) {
                	


                    if (ship.canDock(planet)) {
                        moveList.add(new DockMove(ship, planet));

                        break;
                    }
                    if (planet.isOwned()) {
                    	if (undocked_ship_counter < 40)
                    		continue;
                    	if(planet.getOwner() == gameMap.getMyPlayerId())
                    		continue;
                    }
                    if(undocked_ship_counter == 1 ||undocked_ship_counter == 2) 
                    {
                    	 
                    	localDestinationPlanet = gameMap.getPlanet(6);
                        if (ship.canDock(localDestinationPlanet)) {
                            moveList.add(new DockMove(ship, localDestinationPlanet));
                            break;
                        }
                        if(localDestinationPlanet.getOwner() != gameMap.getMyPlayerId()) {
                        	for (int i = 0; i < localDestinationPlanet.getDockedShips().size(); i++) {
                        		Log.log("\n");
                        		Log.log(Integer.toString(localDestinationPlanet.getDockedShips().get(i)));
                        		Log.log("\n");
							}
                        	
                        }
                        if (localDestinationPlanet.isOwned()&&localDestinationPlanet.isFull()) {
                            continue;
                        }
                        planet = localDestinationPlanet;
                    }
                    if(undocked_ship_counter == 4 || undocked_ship_counter == 5) 
                    {
                    	 
                    	localDestinationPlanet = gameMap.getPlanet(9);
                        if (ship.canDock(localDestinationPlanet)) {
                            moveList.add(new DockMove(ship, localDestinationPlanet));
                            break;
                        }
                        if (localDestinationPlanet.isOwned()&&localDestinationPlanet.isFull()) {
                            continue;
                        }
                        planet = localDestinationPlanet;
                    }
                    	
                    
                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet,localVelocity);
                    if (newThrustMove != null) {
                        moveList.add(newThrustMove);
                    }

                    break;
                }
            }
            Networking.sendMoves(moveList);
        }
    }
}
