import java.awt.Point
import kotlin.properties.Delegates

/**
 * The class that represents the game
 */
class Game {

    // used for statics and constants
    companion object {
        // specification constraints
        const val MAX_DIM = 50
        // error messages
        const val GRID_DIMS_ERROR = "Format: 'xx yy' where xx & yy are digits in the range 1-$MAX_DIM"
    }

    /**
     * Needed for testing the application
     */
    enum class EError {
        ENone,
        EBadInputFormat,
        ECreateWorldBadSize,
        ECreateRobotBadPosition,
        ECreateRobotOffGrid
    }

    // the game state
    private var gameState = EGameState.ECreateWorld
    // last error encountered
    var lastError = EError.ENone
    // the dimensions of the grid
    private var gridDims: Point by Delegates.notNull()
    // used to validate the grid dims
    private val gridRegex = "([0-9]{1,2})\\s+([0-9]{1,2})".toRegex()
    // if this is true we are in test mode
    private var isInTestMode = false
    // the test data
    private var testData: Array<String>? = null

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
        lastError = EError.ENone
        // preliminary format check
        if (!gridRegex.matches(input)) {
            lastError = EError.EBadInputFormat
            println(GRID_DIMS_ERROR)
            // don't change state
            return gameState
        }
        val tokens = input.split(' ')
        // no need to check for unparsables because the regex checks that the input is digits
        val x = tokens[0].toInt()
        val y = tokens[1].toInt()
        // grid size constraint check - because regex allows two digit numbers
        // ASSUMPTION not in spec that grid is at least one unit each way!
        if (!(x in 1..MAX_DIM && y in 1..MAX_DIM)) {
            lastError = EError.ECreateWorldBadSize
            println(GRID_DIMS_ERROR)
            // don't change state
            return gameState
        }
        // ok set the grid size
        gridDims = Point(x, y)
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

    // ================== Test Stuff

    /**
     * This enables test mode
     *
     * @param data this is the test data that will be used instead of user input
     */
    fun setTestMode(data: Array<String>) {
        isInTestMode = true
        testData = data
    }

    /**
     * This disables test mode and resets the game to starting conditions
     */
    fun resetTestMode() {
        isInTestMode = false
        testData = null
        gameState = EGameState.ECreateWorld
    }

    /**
     * Just walks through the game as though a human player were playng it
     *
     * This is is driven by the tests - see main class
     */
    fun testStep() {
        gameState = when (gameState) {
            EGameState.ECreateWorld -> {
                createWorld(testData!![0])
            }
            EGameState.ECreateRobot -> {
                System.out.println("CREATE ROBOT")
                createRobot(testData!![1])
            }
            else -> {
                orderRobot(testData!![2])
            }
        }
    }

}
