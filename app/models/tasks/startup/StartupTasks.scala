package models.tasks.startup

import javax.inject.{Inject, Singleton}

@Singleton
class StartupTasks @Inject()(databaseCreator: DatabaseCreator){

  println("Hello everyone, WORKING HARD>????")

//  databaseCreator.run()

}
