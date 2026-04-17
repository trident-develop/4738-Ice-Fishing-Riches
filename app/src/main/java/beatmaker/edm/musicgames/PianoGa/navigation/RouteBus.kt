package beatmaker.edm.musicgames.PianoGa.navigation

object RouteBus {
    @Volatile
    var route: RouteToken = Tickets.LOADING
        private set

    private val listeners =
        java.util.concurrent.CopyOnWriteArraySet<(RouteToken) -> Unit>()

    fun addListener(listener: (RouteToken) -> Unit, emitCurrent: Boolean = true) {
        listeners.add(listener)
        if (emitCurrent) listener(route)
    }

    fun removeListener(listener: (RouteToken) -> Unit) {
        listeners.remove(listener)
    }

    fun game() {
        route = Tickets.GAME
        listeners.forEach { it(route) }
    }
}