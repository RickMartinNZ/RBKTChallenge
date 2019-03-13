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
        const val ROBOT_ERROR =
            "Format: 'xx yy D' where xx & yy are digits in the range 0-$MAX_DIM & D (direction) is N,E,S or W"
        const val OFF_GRID_ERROR = "Cannot place robot off the grid"
        const val MAX_LINE_LENGTH = 100
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

    /**
     * This represents the facing of the robot
     *
     * @param inputChar the char used to input this direction
     * @param movement this vector is added to the current position to get the new position
     * based on the facing of the robot
     */
    enum class EDir(val inputChar: Char, val movement: Point) {
        ENorth('N', Point(0, 1)),
        EEast('E', Point(1, 0)),
        ESouth('S', Point(0, -1)),
        EWest('W', Point(-1, 0));

        /**
         * @return the new direction if the robot turns right from this facing
         */
        fun right(): EDir {
            var ord = ordinal + 1
            if (ord > EDir.values().size - 1) ord = 0
            return EDir.values()[ord]
        }

        /**
         * @return the new direction if the robot turns left from this facing
         */
        fun left(): EDir {
            var ord = ordinal - 1
            if (ord < 0) ord = EDir.values().size - 1
            return EDir.values()[ord]
        }

        companion object {
            /**
             * @return the EDir for the given character
             */
            fun getFromInputChar(ch: Char): EDir? {
                for (d in EDir.values()) {
                    if (d.inputChar == ch)
                        return d
                }
                return null
            }
        }

    }

    /**
     * The data class representing the robot
     *
     * @param pos the robot position in the world
     * @param dir the direction (facing) of the robot
     * @param lost if the robot is lost
     */
    private data class Robot(var pos: Point, var dir: EDir, var lost: Boolean = false)

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
    // used to validate the robot data
    private val robotRegex = "([0-9]{1,2})\\s([0-9]{1,2})\\s[NESW]+".toRegex()
    // the robot state information - there will only be one playing at a time
    private var robot: Robot by Delegates.notNull()
    // used to validate the command entered by the user
    // this is not hard coded since there may be new commands
    // and we don't want to have keep track of them in more than one place
    private var commandRegex: Regex by Delegates.notNull()
    // this is constructed as the commands might change
    private var commandError: String by Delegates.notNull()

    /**
     * These are the commands that the robot know how to execute
     * Add more commands here as required
     */
    private val commands: HashMap<Char, (Robot) -> Unit> = hashMapOf(
        'F' to { state ->
            run lambda@{
                state.pos add state.dir.movement
                // TODO handle going off edge
            }
        },
        'R' to { state -> state.dir = state.dir.right() },
        'L' to { state -> state.dir = state.dir.left() },
        'T' to { state -> System.out.println("JUST FOR TEST $state") } // just to test new commands
        // add new commands here
    )

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
        // set up the command regex and the error message for commands
        // the regex just makes sure that the user can only use a known command
        val regexSb = StringBuilder()
        val errorSb = StringBuilder()
        regexSb.append("([")
        errorSb.append("Format: 'CCCCCCCCC...' where C can be one of ")
        for (cmd in commands.keys) {
            regexSb.append(cmd)
            errorSb.append("'").append(cmd).append("' ")
        }
        regexSb.append("]{1,$MAX_LINE_LENGTH})")
        commandRegex = regexSb.toString().toRegex()
        errorSb.append("and no longer than $MAX_LINE_LENGTH characters")
        commandError = errorSb.toString()
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
        lastError = EError.ENone
        // preliminary format check
        if (!robotRegex.matches(input)) {
            lastError = EError.EBadInputFormat
            println(ROBOT_ERROR)
            // don't change state
            return gameState
        }
        val tokens = input.split(' ')
        // no need to check for unparsables because the regex checks that the input is digits
        val x = tokens[0].toInt()
        val y = tokens[1].toInt()
        // grid size constraint check - because regex allows two digit numbers
        if (!(x in 0..MAX_DIM && y in 0..MAX_DIM)) {
            lastError = EError.ECreateRobotBadPosition
            println(GRID_DIMS_ERROR)
            // don't change state
            return gameState
        }
        // no need to check direction valid as the regex handles it
        val pos = Point(x, y)
        // check whether we are off the map
        if (!pos.isOnGrid(gridDims)) {
            lastError = EError.ECreateRobotOffGrid
            println(OFF_GRID_ERROR)
            // don't change state
            return gameState
        }
        // get an EDir from the inputted character
        // we know it'll be found because the regex insists on it
        val dir = EDir.getFromInputChar(tokens[2][0])
        // we use '!!' because we know it cannot be null
        robot = Robot(pos, dir!!)
        // to go next game state
        return EGameState.EOrderRobot
    }

    /**
     * This tell the robot what to do
     *
     * @param input the command string
     */
    private fun orderRobot(input: String): EGameState {
        // TODO implement this
        // to go next game state
        return EGameState.ECreateRobot
    }

    /**
     * Extension function for checking we are on the grid
     *
     * @param grid the grid dimensions
     * @return true if we are on the grid
     */
    private fun Point.isOnGrid(grid: Point): Boolean {
        return this.x in 0 until grid.x && this.y in 0 until grid.y
    }

    /**
     * Extension function for adding vectors (allowing infixing)
     *
     * @param other the point to add to this vector
     */
    private infix fun Point.add(other: Point) {
        this.x += other.x
        this.y += other.y
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
                createRobot(testData!![1])
            }
            else -> {
                orderRobot(testData!![2])
            }
        }
    }

    fun getRobotPosition(): Point {
        return robot.pos
    }

    fun getRobotDirection(): EDir {
        return robot.dir
    }


}
