@esac class Person(age: Int, name: String)

@main object Hello { 
  println("Hello, World!")
  val p = Person(26, "Tenchi")
  println(p match {
    case Person(age, name) if (age < 30) => "You're young!"
    case _                               => "You're old!"
  })
}
