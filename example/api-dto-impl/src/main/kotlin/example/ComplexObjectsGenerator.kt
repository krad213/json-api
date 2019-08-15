package example

import java.util.*

class ComplexObjectsGenerator {
    val names = listOf(
            "Ben",
            "Jonas",
            "Leon",
            "Elias",
            "Finn",
            "Noah",
            "Paul",
            "Luis",
            "Lukas",
            "Luca",
            "Felix",
            "Maximilian",
            "Henry",
            "Max",
            "Emil",
            "Moritz",
            "Jakob",
            "Niklas",
            "Tim",
            "Julian",
            "Oskar",
            "Anton",
            "Philipp",
            "David",
            "Liam",
            "Alexander",
            "Theo",
            "Tom",
            "Mats",
            "Jan",
            "Matteo",
            "Samuel",
            "Erik",
            "Fabian",
            "Milan",
            "Leo",
            "Jonathan",
            "Rafael",
            "Simon",
            "Vincent",
            "Lennard",
            "Carl",
            "Linus",
            "Hannes",
            "Jona",
            "Mika",
            "Jannik",
            "Nico",
            "Till",
            "Johannes",
            "Marlon",
            "Leonard",
            "Benjamin",
            "Johann",
            "Mattis",
            "Adrian",
            "Julius",
            "Florian",
            "Constantin",
            "Daniel",
            "Aaron",
            "Maxim",
            "Nick",
            "Lenny",
            "Valentin",
            "Ole",
            "Luke",
            "Levi",
            "Nils",
            "Jannis",
            "Sebastian",
            "Tobias",
            "Marvin",
            "Joshua",
            "Mohammed",
            "Timo",
            "Phil",
            "Joel",
            "Benedikt",
            "John",
            "Robin",
            "Toni",
            "Dominic",
            "Damian",
            "Artur",
            "Pepe",
            "Lasse",
            "Malte",
            "Sam",
            "Bruno",
            "Gabriel",
            "Lennox",
            "Justus",
            "Kilian",
            "Theodor",
            "Oliver",
            "Jamie",
            "Levin",
            "Lian",
            "Noel")

    private val random = Random()

    fun generate(): ComplexObj {
        val name = randomName()


        return ComplexObj(
                name = randomName(),
                amount = random.nextInt().toBigDecimal(),
                people = IntRange(0, 10).map {
                    Person(
                            name = randomName(),
                            surname = randomName()
                    )
                }
        )
    }


    private fun randomName() = names[random.nextInt(names.size)]
}