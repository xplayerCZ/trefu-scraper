
import org.joda.time.LocalDate

fun main() {
    val manager = DatasetManager()
    manager.update(LocalDate.now(), LocalDate.now())
}