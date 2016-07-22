import scala.meta._

import scala.collection.immutable.Seq

object util {
  def banner(text: String, width: Int = 30): Unit = {
    println("=" * width)
    println(text.toUpperCase())
    println("=" * width)
  }

  def showDiff(defn: Any, src: Any): Unit = {
    val defnLines = defn.toString.split("\n")
    val defnWidth = defnLines.maxBy(_.length).length
    val padded = defnLines.map(l => l.padTo(defnWidth, ' ') + " │ ")

    val srcLines = src.toString.split("\n")
    val srcWidth = srcLines.maxBy(_.length).length

    val both = padded.zipWithIndex.map(t => t._1 + srcLines(t._2))
    val remaining = (both.length until srcLines.length).map(i => " " * defnWidth + " │ " + srcLines(i))

    banner("Original code" + " " * (defnWidth - 10) + "Generated code", defnWidth + 3 + srcWidth)
    both.foreach(l => println(l))
    remaining.foreach(l => println(l))
    println()
  }
}

class main extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any) = meta {
    val q"object $name { ..$stats }" = defn
    val main = q"""
      def main(args: Array[String]): Unit = { ..$stats }
    """
    val src = q"object $name { $main }"
    
    util.showDiff(defn, src)

    src
  }
}

class esac extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any) = meta {
    val q"class $name(..$params) { ..$stats }" = defn

    val termName = Term.Name(name.toString)
    val valParams = params.map(p => Term.Param(Seq(Mod.ValParam()), p.name, p.decltpe, p.default))

    val newInstance = s"""
      new $name(${params.map(param => param.name.toString).mkString(", ")})
    """.parse[Term].get

    val applyDef = q"def apply(..$params) = $newInstance"

    val unapplyDef = {
      val paramName = name.toString.toLowerCase()
      s"""
        def unapply($paramName: $name) = Some((${params.map(param => s"$paramName.${param.name}").mkString(", ")}))
      """.parse[Stat].get
    }

    val src = q"""
      class $name(..$valParams) { ..$stats }
      object $termName {
        $applyDef
        $unapplyDef
      }
    """
    
    util.showDiff(defn, src)

    src
  }
}
