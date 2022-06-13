
import java.time.LocalDate

suspend fun main() {
    val manager = DatasetManager()
    manager.update(LocalDate.of(LocalDate.now().year, 1, 1), LocalDate.MAX)
}