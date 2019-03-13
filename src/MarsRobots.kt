import java.awt.Point

/**
 * Main entry point to game
 */
fun main() {
    val game = Game()
    game.initialize()
    // comment this out for play mode
    // uncomment for test mode
    runTests(game)
    // comment this out for test mode
    // uncomment for play mode
    //game.play()
}

// TODO add tests as we go along
fun runTests(game: Game) {
    var passed = testCreateWorld(game)
    if (!passed) {
        System.out.println("testCreateWorld failed")
        return
    }
    passed = testCreateRobot(game)
    if (!passed) {
        System.out.println("testCreateRobot failed")
        return
    }

    System.out.println("All tests passed")
}

fun testCreateWorld(game: Game): Boolean {
    System.out.println("Create world regex failure (junk dimensions)")
    game.resetTestMode()
    var data = arrayOf("abc", "", "")
    game.setTestMode(data)
    game.testStep()
    if (game.lastError != Game.EError.EBadInputFormat) return false
    System.out.println("Create world regex failure (number of digits too big)")
    game.resetTestMode()
    data = arrayOf("111 111", "", "")
    game.setTestMode(data)
    game.testStep()
    if (game.lastError != Game.EError.EBadInputFormat) return false
    System.out.println("Create world size too small")
    game.resetTestMode()
    data = arrayOf("0 0", "", "")
    game.setTestMode(data)
    game.testStep()
    if (game.lastError != Game.EError.ECreateWorldBadSize) return false
    System.out.println("Create world size too big")
    game.resetTestMode()
    data = arrayOf("60 60", "", "")
    game.setTestMode(data)
    game.testStep()
    if (game.lastError != Game.EError.ECreateWorldBadSize) return false
    System.out.println("Create world minimum size")
    game.resetTestMode()
    data = arrayOf("1 1", "", "")
    game.setTestMode(data)
    game.testStep()
    System.out.println("Last error ${game.lastError}")
    if (game.lastError != Game.EError.ENone) return false
    System.out.println("Create world maximum size")
    game.resetTestMode()
    data = arrayOf("50 50", "", "")
    game.setTestMode(data)
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    System.out.println("Create world just over maximum size")
    game.resetTestMode()
    data = arrayOf("50 51", "", "")
    game.setTestMode(data)
    game.testStep()
    if (game.lastError != Game.EError.ECreateWorldBadSize) return false
    System.out.println("Create world allowable size")
    game.resetTestMode()
    data = arrayOf("10 30", "", "")
    game.setTestMode(data)
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    return true
}

fun testCreateRobot(game: Game): Boolean {
    System.out.println("Create robot regex failure (junk position)")
    game.resetTestMode()
    var data = arrayOf("10 10", "abc", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.EBadInputFormat) return false
    System.out.println("Create robot regex failure (position digits too big)")
    game.resetTestMode()
    data = arrayOf("10 10", "111 111", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.EBadInputFormat) return false
    System.out.println("Create robot regex failure (missing direction)")
    game.resetTestMode()
    data = arrayOf("10 10", "20 20", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.EBadInputFormat) return false
    System.out.println("Create robot regex failure (unknown direction)")
    game.resetTestMode()
    data = arrayOf("10 10", "20 20 X", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.EBadInputFormat) return false
    System.out.println("Create robot position off grid")
    game.resetTestMode()
    data = arrayOf("10 10", "20 20 N", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.ECreateRobotOffGrid) return false
    System.out.println("Create robot ok position north")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    if (game.getRobotPosition() != Point(5, 5)) return false
    if (game.getRobotDirection() != Game.EDir.ENorth) return false
    System.out.println("Create robot ok position east")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 E", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    if (game.getRobotPosition() != Point(5, 5)) return false
    if (game.getRobotDirection() != Game.EDir.EEast) return false
    System.out.println("Create robot ok position south")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 S", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    if (game.getRobotPosition() != Point(5, 5)) return false
    if (game.getRobotDirection() != Game.EDir.ESouth) return false
    System.out.println("Create robot ok position west")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 W", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    if (game.getRobotPosition() != Point(5, 5)) return false
    if (game.getRobotDirection() != Game.EDir.EWest) return false
    System.out.println("Create robot ok at left edge")
    game.resetTestMode()
    data = arrayOf("10 10", "0 5 W", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    if (game.getRobotPosition() != Point(0, 5)) return false
    if (game.getRobotDirection() != Game.EDir.EWest) return false
    System.out.println("Create robot ok at right edge")
    game.resetTestMode()
    data = arrayOf("10 10", "9 5 W", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    if (game.getRobotPosition() != Point(9, 5)) return false
    if (game.getRobotDirection() != Game.EDir.EWest) return false
    System.out.println("Create robot ok at bottom edge")
    game.resetTestMode()
    data = arrayOf("10 10", "5 0 W", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    if (game.getRobotPosition() != Point(5, 0)) return false
    if (game.getRobotDirection() != Game.EDir.EWest) return false
    System.out.println("Create robot ok at top edge")
    game.resetTestMode()
    data = arrayOf("10 10", "5 9 W", "")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    if (game.getRobotPosition() != Point(5, 9)) return false
    if (game.getRobotDirection() != Game.EDir.EWest) return false
    return true
}
