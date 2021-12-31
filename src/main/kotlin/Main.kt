
import java.time.LocalDate

fun main() {
    val manager = DatasetManager()
    manager.update(LocalDate.now(), LocalDate.now())
}