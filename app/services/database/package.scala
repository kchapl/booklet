package services

import zio._

package object database {
  type Database = Has[Database.Service]
}
