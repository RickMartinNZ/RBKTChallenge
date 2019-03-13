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
    passed = testOrderRobot(game)
    if (!passed) {
        System.out.println("testOrderRobot failed")
        return
    }
    passed = testTurningRobot(game)
    if (!passed) {
        System.out.println("testTurningRobot failed")
        return
    }
    passed = testMovingRobot(game)
    if (!passed) {
        System.out.println("testMovingRobot failed")
        return
    }
    passed = testLostRobot(game)
    if (!passed) {
        System.out.println("testLostRobot failed")
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

fun testOrderRobot(game: Game): Boolean {
    System.out.println("Order robot regex failure (junk command string)")
    game.resetTestMode()
    var data = arrayOf("10 10", "5 5 N", "abc")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.lastError != Game.EError.EBadInputFormat) return false
    System.out.println("Order robot regex failure (spaces in command)")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "F F F F")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.lastError != Game.EError.EBadInputFormat) return false
    System.out.println("Order robot regex failure (unknown command)")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "FFFX")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.lastError != Game.EError.EBadInputFormat) return false
    System.out.println("Order robot regex failure (too long command)")
    game.resetTestMode()
    data = arrayOf(
        "10 10", "5 5 N",
        "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"
    )
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.lastError != Game.EError.EBadInputFormat) return false
    System.out.println("Order robot ok command")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "LLLL")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.lastError != Game.EError.ENone) return false
    if (game.getRobotDirection() != Game.EDir.ENorth) return false
    return true
}

fun testTurningRobot(game: Game): Boolean {
    System.out.println("Turn robot - left once")
    game.resetTestMode()
    var data = arrayOf("10 10", "5 5 N", "L")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.EWest) return false
    System.out.println("Turn robot - left twice")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "LL")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ESouth) return false
    System.out.println("Turn robot - left three times")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "LLL")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.EEast) return false
    System.out.println("Turn robot - left four times")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "LLLL")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ENorth) return false
    System.out.println("Turn robot - right once")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "R")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.EEast) return false
    System.out.println("Turn robot - right twice")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "RR")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ESouth) return false
    System.out.println("Turn robot - right three times")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "RRR")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.EWest) return false
    System.out.println("Turn robot - right four times")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "RRRR")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ENorth) return false
    return true
}

fun testMovingRobot(game: Game): Boolean {
    System.out.println("Move robot - North -> forward 1")
    game.resetTestMode()
    var data = arrayOf("10 10", "5 5 N", "F")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ENorth) return false
    if (game.getRobotPosition() != Point(5, 6)) return false
    System.out.println("Move robot - East -> forward 1")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 E", "F")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.EEast) return false
    if (game.getRobotPosition() != Point(6, 5)) return false
    System.out.println("Move robot - South -> forward 1")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 S", "F")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ESouth) return false
    if (game.getRobotPosition() != Point(5, 4)) return false
    System.out.println("Move robot - West -> forward 1")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 W", "F")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.EWest) return false
    if (game.getRobotPosition() != Point(4, 5)) return false
    System.out.println("Move robot - North -> clockwise cirle")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "FRFRFRFR")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ENorth) return false
    if (game.getRobotPosition() != Point(5, 5)) return false
    System.out.println("Move robot - North -> anti-clockwise cirle")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "FLFLFLFL")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ENorth) return false
    if (game.getRobotPosition() != Point(5, 5)) return false
    System.out.println("Move robot - complex path back to start")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "FFLFFLFLFRFLFL")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ENorth) return false
    if (game.getRobotPosition() != Point(5, 5)) return false
    return true
}

fun testLostRobot(game: Game): Boolean {
    System.out.println("Lost to left")
    game.resetTestMode()
    var data = arrayOf("10 10", "5 5 N", "LFFFFFF")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.EWest) return false
    if (game.getRobotPosition() != Point(-1, 5)) return false
    if (!game.isRobotLost()) return false
    System.out.println("Lost to right")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "RFFFFFF")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.EEast) return false
    if (game.getRobotPosition() != Point(10, 5)) return false
    if (!game.isRobotLost()) return false
    System.out.println("Lost to top")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "FFFFFF")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ENorth) return false
    if (game.getRobotPosition() != Point(5, 10)) return false
    if (!game.isRobotLost()) return false
    System.out.println("Lost to bottom")
    game.resetTestMode()
    data = arrayOf("10 10", "5 5 N", "RRFFFFFF")
    game.setTestMode(data)
    game.testStep() // to set up world
    game.testStep() // to set up robot
    game.testStep()
    if (game.getRobotDirection() != Game.EDir.ESouth) return false
    if (game.getRobotPosition() != Point(5, -1)) return false
    if (!game.isRobotLost()) return false
    return true
}
