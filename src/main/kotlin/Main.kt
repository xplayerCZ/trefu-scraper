
import java.time.LocalDate

fun main() {
    val manager = DatasetManager()
    manager.update(LocalDate.of(LocalDate.now().year, 1,1), LocalDate.MAX)
}