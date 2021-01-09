package fail.enormous.carmate

import java.math.BigDecimal

// CarList Class
class CarList(val brand: String, val model: String, val year: Int, val color: String, val type: String, val price: BigDecimal) {
    // This sets every variable within the CarList class as a string, despite the JSON files using price and year as ints! (or floats) - Easier for displaying this data.
    // Don't need to put anything in here like getBrand() functions like Java, Kotlin has this built-in.
}