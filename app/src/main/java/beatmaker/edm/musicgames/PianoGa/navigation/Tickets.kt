package beatmaker.edm.musicgames.PianoGa.navigation

typealias RouteToken = String

object Tickets {
    const val LOADING = "LOADING"
    const val GAME = "GAME"
    const val RULES = "RULES"
}

fun RouteToken.isLoading() = this == Tickets.LOADING
fun RouteToken.isGame() = this == Tickets.GAME
fun RouteToken.isRules() = this == Tickets.RULES