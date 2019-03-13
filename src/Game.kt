/**
 * The class that represents the game
 */
class Game {

    // used for statics and constants
    companion object {

    }

    // the game state
    private var gameState = EGameState.ECreateWorld

    /**
     * The game state
     *
     * It starts with creating the world
     *
     * Then we can create a robot
     * Then we can give the robot orders
     * Then we can create another robot...
     */
    private enum class EGameState {
        ECreateWorld,
        ECreateRobot,
        EOrderRobot
    }

    /**
     * This sets up anything needed to play the game
     */
    fun initialize() {
    }

    /**
     * Play the game
     */
    fun play() {
        while (true) {
            val input = getInput()
            gameState = when (gameState) {
                EGameState.ECreateWorld -> createWorld(input)
                EGameState.ECreateRobot -> createRobot(input)
                else -> orderRobot(input)
            }
        }
    }

    /**
     * Get input from the user - keeps spinning until the user enters something
     *
     * @return the input
     */
    private fun getInput(): String {
        // print a prompt
        System.out.println(
            when (gameState) {
                EGameState.ECreateWorld -> "Enter World Dimensions"
                EGameState.ECreateRobot -> "Enter Robot Position And Direction"
                else -> "Enter Robot Orders"
            }
        )
        // get the input
        var line = readLine()!!
        line = line.trim()
        while (line.isEmpty()) {
            line = readLine()!!
            line = line.trim()
        }
        return line
    }

    /**
     * This creates the world... basically just sets the grid dimensions
     *
     * @param input the string from the user
     * @return the new game state - game state does not change in case of error
     */
    private fun createWorld(input: String): EGameState {
        // TODO add functionality here
        // to go next game state
        return EGameState.ECreateRobot
    }

    /**
     * This creates the robot... basically just sets the location and facing
     *
     * @param input the string from the user
     * @return the new game state - game state does not change in case of error
     */
    private fun createRobot(input: String): EGameState {
        // TODO add functionality here
        // to go next game state
        return EGameState.EOrderRobot
    }

    /**
     * This tell the robot what to do
     *
     * @param input the command string
     */
    private fun orderRobot(input: String): EGameState {
        // TODO add functionality here
        // to go next game state
        return EGameState.ECreateRobot
    }
}
