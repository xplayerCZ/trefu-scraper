
import java.time.LocalDate

suspend fun main() {
    val manager = DatasetManager()
    manager.update(LocalDate.now(), LocalDate.MAX)
}