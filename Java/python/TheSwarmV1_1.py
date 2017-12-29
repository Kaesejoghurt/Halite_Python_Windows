"""
Welcome to your first Halite-II bot!

This bot's name is Settler. It's purpose is simple (don't expect it to win complex games :) ):
1. Initialize game
2. If a ship is not docked and there are unowned planets
2.a. Try to Dock in the planet if close enough
2.b If not, go towards the planet

Note: Please do not place print statements here as they are used to communicate with the Halite engine. If you need
to log anything use the logging module.
"""
# Let's start by importing the Halite Starter Kit so we can interface with the Halite engine
import hlt
# Then let's import the logging module so we can print out information
import logging

# GAME START
# Here we define the bot's name as Settler and initialize the game, including communication with the Halite engine.
game = hlt.Game("The Swarm v1_1")
# Then we print our start message to the logs
logging.info("Your Queen Listenes!")

while True:
    # TURN START
    # Update the map for the new turn and get the latest version
    game_map = game.update_map()

    # Here we define the set of commands to be sent to the Halite engine at the end of the turn
    command_queue = []
    shipcount = -1
    planetcount = 0
    dontQueueNavigateCommand = 0
    # For every ship that I control
    for ship in game_map.get_me().all_ships():
        
        # If the ship is docked
        if ship.docking_status != ship.DockingStatus.UNDOCKED:
            # Skip this ship
            continue
        shipcount += 1
        # For each planet in the game (only non-destroyed planets are included)
        for planet in game_map.all_planets():
            # If the planet is owned
        


            # If we can dock, let's (try to) dock. If two ships try to dock at once, neither will be able to.

            if planet.is_owned():
                # Skip this planet
                continue
            if ship.can_dock(planet):
                # We add the command by appending it to the command_queue
                command_queue.append(ship.dock(planet))

            else:
                
                # If we can't dock, we move towards the closest empty point near this planet (by using closest_point_to)
                # with constant speed. Don't worry about pathfinding for now, as the command will do it for you.
                # We run this navigate command each turn until we arrive to get the latest move.
                # Here we move at half our maximum speed to better control the ships
                # In order to execute faster we also choose to ignore ship collision calculations during navigation.
                # This will mean that you have a higher probability of crashing into ships, but it also means you will
                # make move decisions much quicker. As your skill progresses and your moves turn more optimal you may
                # wish to turn that option off.
                

                if shipcount == 1:
                    expand_planet = 6
                    planet_s1 = game_map.get_planet(expand_planet)
                    #logging.info(planet.get_owner())
                    if planet_s1.is_owned():
                        expand_planet += 1
                        continue
                    if ship.can_dock(planet_s1):
                        # We add the command by appending it to the command_queue
                        command_queue.append(ship.dock(planet_s1))
                        dontQueueNavigateCommand = 1
                        break
                    else:
                        navigate_command = ship.navigate(
                        ship.closest_point_to(planet_s1),
                        game_map,
                        speed=int(hlt.constants.MAX_SPEED),
                        ignore_ships=True)
                    
                elif shipcount == 2:
                    expand_planet = 12
                    for planet_s2 in game_map.all_planets():
                        count = 0
                        count +=1
                        if count == expand_planet:
                            if planet_s2.is_owned():
                                expand_planet += 1
                                continue
                        if ship.can_dock(planet_s2):
                            # We add the command by appending it to the command_queue
                            command_queue.append(ship.dock(planet_s2))
                            dontQueueNavigateCommand = 1
                            break
                        else:
                            navigate_command = ship.navigate(
                            ship.closest_point_to(planet_s2),
                            game_map,
                            speed=int(hlt.constants.MAX_SPEED-1),
                            ignore_ships=True)
                else:
                    navigate_command = ship.navigate(
                    ship.closest_point_to(planet),
                    game_map,
                    speed=int(hlt.constants.MAX_SPEED/2),
                    ignore_ships=True)
                
                # If the move is possible, add it to the command_queue (if there are too many obstacles on the way
                # or we are trapped (or we reached our destination!), navigate_command will return null;
                # don't fret though, we can run the command again the next turn)
                if dontQueueNavigateCommand == 1:
                    dontQueueNavigateCommand = 0
                elif navigate_command:
                    command_queue.append(navigate_command)
            break

    # Send our set of commands to the Halite engine for this turn
    game.send_command_queue(command_queue)
    # TURN END
# GAME END


