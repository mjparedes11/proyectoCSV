import com.github.tototoshi.csv.*

import java.io.{BufferedWriter, File, FileWriter, OutputStreamWriter}
import java.io.File
import cats.*
import cats.implicits.*
import scala. math. Fractional. Implicits. infixFractionalOps

object proyect {
  @main def mainScriptGenerator(): Unit ={

    implicit object MyFormat extends DefaultCSVFormat {
      override val delimiter: Char = ';'
    }

    val direccion: String = "C:\\Users\\USUARIO\\Downloads\\pi_movies_complete.csv"
    val small: String = "C:\\Users\\USUARIO\\Downloads\\small_pi_movies.csv"
    val reader: CSVReader = CSVReader.open(new File(direccion))

    val dataMap = reader.allWithHeaders()
    reader.close()

    //campo a analizar
    val col = "budget"
    val campo: List[Long] = dataMap
      .flatMap(row => row.get(col) //devuelve List[Option[String]]
        .flatMap(bdgt => scala.util.Try(bdgt.trim.toLong).toOption))

    def prom(vals: List[Long]): Double =
      val sumCount: Tuple2[Double, Int] =
        vals.filter(_ > 0)
          .foldLeft((0.0, 0))((t2, curr) => (t2._1 + curr, t2._2 +  1))
      sumCount._1 / sumCount._2

    def desviaEstandar(lista: List[Long]): Double =
      val media = prom(lista)
      val sumaCuadrados = lista.map(x => math.pow(x - media, 2)).sum
      math.pow(sumaCuadrados / (lista.length - 1), 0.5)

    def moda(datos: List[String]): List[String] = {
      if (datos.isEmpty) return List()

      val frecuencias = datos.groupBy(identity).view.mapValues(_.size).toMap
      val maxFrecuencia = frecuencias.values.max

      frecuencias.filter { case (_, freq) => freq == maxFrecuencia }.keys.toList
    }

    println(campo.min)
    println(campo.max)
    println(prom(campo))
    println(desviaEstandar(campo))

    val colString: String = "original_language"

    val colSt: List[String] = dataMap.flatMap(row => row.get(colString))
    //Valores únicos
    //colSt.distinct.foreach(print)
    //conteofrecuencias
    println("Frequencias:")
    println(colSt
      .groupBy(identity)
      .map(x => (x._1.trim, x._2.length)))

    println("Moda"+moda(colSt))

    //generación de scripts
    val filePath = "C:\\Users\\USUARIO\\Desktop\\scriptproyecto.sql"

    // Define las columnas que deseas conservar
    val columnasConservar = Seq("id", "imdb_id", "adult", "budget", "homepage","original_language","original_title","overview","popularity","poster_path","release_date","revenue","runtime","status","tagline","video","vote_average","vote_count")

    // Filtra las columnas y limpia los valores
    val pelicula: List[Map[String, String]] = dataMap.map { fila =>
      fila
        .filter { case (columna, _) => columnasConservar.contains(columna) }
        .map { case (columna, valor) => columna -> valor.trim } // Limpia espacios extra
        .toMap
    }

    def escape(input: Option[String]): String = input match {
      case Some(value) if value.nonEmpty =>
        value
          .replace("\\", "\\\\") // Escapa barra invertida
          .replace("'", "\\'") // Escapa comillas simples
          .replace("\"", "\\\"") // Escapa comillas dobles
          .replace("\b", "\\b") // Escapa retroceso
          .replace("\n", "\\n") // Escapa nueva línea
          .replace("\r", "\\r") // Escapa retorno de carro
          .replace("\t", "") // Elimina tabulaciones
      case _ => "NULL" //
    }

    def toIntSafe(value: Option[String]): Int = value.flatMap(_.toIntOption).getOrElse(0)
    def toDoubleSafe(value: Option[String]): Double = value.flatMap(_.toDoubleOption).getOrElse(0.0)

    def generateInsert(row: Map[String, String]): String = {
      val id = toIntSafe(row.get("id"))
      val imdbId = escape(row.get("imdb_id"))
      val adult = escape(row.get("adult")) // En MySQL boolean se usa como TINYINT(1)
      val budget = toIntSafe(row.get("budget"))
      val homepage = escape(row.get("homepage"))
      val originalLanguage = escape(row.get("original_language"))
      val originalTitle = escape(row.get("original_title"))
      val overview = escape(row.get("overview"))
      val popularity = toDoubleSafe(row.get("popularity"))
      val posterPath = escape(row.get("poster_path"))
      val releaseDate = escape(row.get("release_date")) // Si está vacío, será NULL
      val revenue = toIntSafe(row.get("revenue"))
      val runtime = toIntSafe(row.get("runtime"))
      val status = escape(row.get("status"))
      val tagline = escape(row.get("tagline"))
      val video = escape(row.get("video")) // En MySQL boolean se usa como TINYINT(1)
      val voteAverage = toDoubleSafe(row.get("vote_average"))
      val voteCount = toIntSafe(row.get("vote_count"))

      s"""
         |INSERT INTO pelicula (id_pelicula, imdb_id, adult, budget, homepage, original_language,
         |original_title, overview, popularity, poster_path, release_date, revenue, runtime,
         |status, tagline, video, vote_average, vote_count)
         |VALUES ($id, '$imdbId', $adult, $budget, '$homepage', '$originalLanguage',
         |'$originalTitle', '$overview', $popularity, '$posterPath', '$releaseDate',
         |$revenue, $runtime, '$status', '$tagline', $video , $voteAverage, $voteCount);
    """.stripMargin.trim
    }

    // Escritura segura del archivo SQL
    try {
      val file = new BufferedWriter(new FileWriter(filePath))
      pelicula.foreach { row =>
        file.write(generateInsert(row))
        file.newLine()
      }
      println(s"Archivo SQL generado correctamente en: $filePath")
      file.close()
    } catch {
      case e: Exception => println(s"Error al escribir el archivo SQL: ${e.getMessage}")
    }

    println("Finalizó el programa")
  }
}
