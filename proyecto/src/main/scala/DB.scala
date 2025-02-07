import cats.*
import cats.effect.*
import cats.effect.unsafe.implicits.global
import doobie._
import doobie.implicits._

object DB {
  @main def crearDB(): Unit = {
    val coneccion = Transactor.fromDriverManager[IO](
      driver = "com.mysql.cj.jdbc.Driver", // JDBC driver
      url = "jdbc:mysql://localhost:3306/pruebas", // URL de conexión
      user = "root", // Nombre de la base de datos
      password = "1234", // Password
      logHandler = None // Manejo de la información de Log
    )

    def creartabla1(): ConnectionIO[Int] = {
      sql"""CREATE TABLE pelicula (
           |    id_pelicula INT PRIMARY KEY AUTO_INCREMENT,
           |    imdb_id VARCHAR(20) UNIQUE,
           |    adult BOOLEAN,
           |    budget FLOAT,
           |    homepage VARCHAR(255),
           |    original_language VARCHAR(10),
           |    original_title VARCHAR(255),
           |    overview TEXT,
           |    popularity FLOAT,
           |    poster_path VARCHAR(255),
           |    release_date VARCHAR(255),
           |    revenue FLOAT,
           |    runtime INT,
           |    status VARCHAR(50),
           |    tagline TEXT,
           |    video BOOLEAN,
           |    vote_average FLOAT,
           |    vote_count INT
           |)
           |""".stripMargin
        .update.run
    }

    def creartabla2(): ConnectionIO[Int] = {
      sql"""CREATE TABLE genres (
           |    id_gen INT PRIMARY KEY AUTO_INCREMENT,
           |    name VARCHAR(100) UNIQUE
           |)""".stripMargin
        .update.run
    }

    def creartabla3(): ConnectionIO[Int] = {
      sql"""CREATE TABLE production_countries (
           |    code_count VARCHAR(10) PRIMARY KEY,
           |    name_pcount VARCHAR(255) UNIQUE
           |)""".stripMargin
        .update.run
    }

    def creartabla4(): ConnectionIO[Int] = {
      sql"""CREATE TABLE pelicula_genres (
           |    id_gen INT,
           |    id_pelicula INT,
           |    PRIMARY KEY (id_gen, id_pelicula),
           |    FOREIGN KEY (id_gen) REFERENCES genres(id_gen) ON DELETE CASCADE,
           |    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
           |)""".stripMargin
        .update.run
    }

    def creartabla5(): ConnectionIO[Int] = {
      sql"""CREATE TABLE belong_to (
           |    id_belong INT PRIMARY KEY AUTO_INCREMENT,
           |    name VARCHAR(255),
           |    poster_path VARCHAR(255),
           |    backdrop_path VARCHAR(255),
           |    id_pelicula INT,
           |    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
           |)""".stripMargin
        .update.run
    }

    def creartabla6(): ConnectionIO[Int] = {
      sql"""CREATE TABLE production_companies (
           |    id_comp INT PRIMARY KEY AUTO_INCREMENT,
           |    name_pcompa VARCHAR(255) UNIQUE
           |)""".stripMargin
        .update.run
    }

    def creartabla7(): ConnectionIO[Int] = {
      sql"""CREATE TABLE pelicula_pcomp (
           |    id_comp INT,
           |    id_pelicula INT,
           |    PRIMARY KEY (id_comp, id_pelicula),
           |    FOREIGN KEY (id_comp) REFERENCES production_companies(id_comp) ON DELETE CASCADE,
           |    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
           |)""".stripMargin
        .update.run
    }

    def creartabla8(): ConnectionIO[Int] = {
      sql"""CREATE TABLE pelicula_pcunt (
           |    code_count VARCHAR(10),
           |    id_pelicula INT,
           |    PRIMARY KEY (code_count, id_pelicula),
           |    FOREIGN KEY (code_count) REFERENCES production_countries(code_count) ON DELETE CASCADE,
           |    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
           |)""".stripMargin
        .update.run
    }

    def creartabla9(): ConnectionIO[Int] = {
      sql"""CREATE TABLE spoken_languages (
           |    code_lang VARCHAR(10) PRIMARY KEY,
           |    name_sl VARCHAR(255) UNIQUE
           |)""".stripMargin
        .update.run
    }

    def creartabla10(): ConnectionIO[Int] = {
      sql"""CREATE TABLE pelicula_sl (
           |    code_lang VARCHAR(10),
           |    id_pelicula INT,
           |    PRIMARY KEY (code_lang, id_pelicula),
           |    FOREIGN KEY (code_lang) REFERENCES spoken_languages(code_lang) ON DELETE CASCADE,
           |    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
           |)""".stripMargin
        .update.run
    }

    def creartabla11(): ConnectionIO[Int] = {
      sql"""CREATE TABLE keywords (
           |    id_kw INT PRIMARY KEY AUTO_INCREMENT,
           |    name_kw VARCHAR(255) UNIQUE
           |)""".stripMargin
        .update.run
    }

    def creartabla12(): ConnectionIO[Int] = {
      sql"""CREATE TABLE pelicula_keywords (
           |    id_kw INT,
           |    id_pelicula INT,
           |    PRIMARY KEY (id_kw, id_pelicula),
           |    FOREIGN KEY (id_kw) REFERENCES keywords(id_kw) ON DELETE CASCADE,
           |    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
           |)""".stripMargin
        .update.run
    }

    def creartabla13(): ConnectionIO[Int] = {
      sql"""CREATE TABLE cast (
           |    id_kw INT PRIMARY KEY AUTO_INCREMENT,
           |    name_cast VARCHAR(255),
           |    id_pelicula INT,
           |    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
           |)""".stripMargin
        .update.run
    }

    def creartabla14(): ConnectionIO[Int] = {
      sql"""CREATE TABLE crew (
           |    id_crew INT PRIMARY KEY AUTO_INCREMENT,
           |    department VARCHAR(255),
           |    gender INT,
           |    id INT,
           |    job VARCHAR(255),
           |    name VARCHAR(255),
           |    profile_path VARCHAR(255),
           |    id_pelicula INT,
           |    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
           |)""".stripMargin
        .update.run
    }

    def creartabla15(): ConnectionIO[Int] = {
      sql"""CREATE TABLE ratings (
           |    userId INT,
           |    rating FLOAT,
           |    timestamp BIGINT,
           |    id_pelicula INT,
           |    PRIMARY KEY (userId, id_pelicula),
           |    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
           |)""".stripMargin
        .update.run
    }

    val script1 = creartabla1().transact(coneccion).unsafeRunSync()
    val script2 = creartabla2().transact(coneccion).unsafeRunSync()
    val script3 = creartabla3().transact(coneccion).unsafeRunSync()
    val script4 = creartabla4().transact(coneccion).unsafeRunSync()
    val script5 = creartabla5().transact(coneccion).unsafeRunSync()
    val script6 = creartabla6().transact(coneccion).unsafeRunSync()
    val script7 = creartabla7().transact(coneccion).unsafeRunSync()
    val script8 = creartabla8().transact(coneccion).unsafeRunSync()
    val script9 = creartabla9().transact(coneccion).unsafeRunSync()
    val script10 = creartabla10().transact(coneccion).unsafeRunSync()
    val script11 = creartabla11().transact(coneccion).unsafeRunSync()
    val script12 = creartabla12().transact(coneccion).unsafeRunSync()
    val script13 = creartabla13().transact(coneccion).unsafeRunSync()
    val script14 = creartabla14().transact(coneccion).unsafeRunSync()
    val script15 = creartabla15().transact(coneccion).unsafeRunSync()
  }
}
