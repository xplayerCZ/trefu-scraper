suspend fun main() {
    val location = System.getenv("LOCATION")?.toIntOrNull() ?: 11
    val start = System.getenv("START_CODE")?.toIntOrNull()
    if (start == null) {
        println("Please set START_CODE environment variable")
        return
    }
    val manager = DatasetManager(location)
    manager.update(start)
}