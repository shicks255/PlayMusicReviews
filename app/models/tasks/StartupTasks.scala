package models.tasks

import javax.inject.{Inject, Singleton}
import models.DatabaseCreator

@Singleton
class StartupTasks @Inject()(databaseCreator: DatabaseCreator){

  println("Hello everyone, WORKING HARD>????")

  databaseCreator.run()

}
