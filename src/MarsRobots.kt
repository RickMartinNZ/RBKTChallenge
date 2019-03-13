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
