package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

object CodeGen extends App {
  slick.codegen.SourceCodeGenerator.run(
    "slick.jdbc.PostgresProfile",
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost/exuldb?user=exul&password=admin222",
    "/home/bradly/Documents/bloks/app",
    "models", None, None, true, false
  )
}