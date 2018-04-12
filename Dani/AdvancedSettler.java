import hlt.*;

import java.util.ArrayList;

public class AdvancedSettler {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("PussyDestroyer-Destroyer");

        // We now have 1 full minute to analyse the initial map.
        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        Log.log(initialMapIntelligence);
        
        int undocked_ship_counter =0;

        final ArrayList<Move> moveList = new ArrayList<>();
        for (;;) {
            moveList.clear();
            networking.updateMap(gameMap);
            undocked_ship_counter = 0;
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
            	
            	undocked_ship_counter++;
            	
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }
                
                for (Planet planet : gameMap.getAllPlanets().values()) {

                    if (planet.isFull() ||( planet.getOwner() != gameMap.getMyPlayerId()&& planet.isOwned())) {
                        continue;
                    }
                    if (undocked_ship_counter == 2)
                    	planet = gameMap.getPlanet(6);

                    if (ship.canDock(planet)) {
                        moveList.add(new DockMove(ship, planet));
                        break;
                    }

                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED/2);
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
