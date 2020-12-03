package fail.enormous.carmate

class CarList(val brand: String, val model: String, val year: String, val color: String, val type: String, val price: String) {
    // This sets every variable within the CarList class as a string, despite the JSON files using price and year as ints! Easier for displaying this data.
}