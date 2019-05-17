package models.tasks.startup

import javax.inject.{Inject, Singleton}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class StartupTasks @Inject()(databaseCreator: DatabaseCreator, initializeCache: InitializeCache){
  implicit val global = ExecutionContext.global
  val startAt = System.currentTimeMillis();
  println("Starting Steve's Review")
  println(
    """
     _____ _                 _       _____           _
    /  ___| |               ( )     | ___ \         (_)
    \ `--.| |_ _____   _____|/ ___  | |_/ /_____   ___  _____      _____
    `--. \ __/ _ \ \ / / _ \ / __|  |    // _ \ \ / / |/ _ \ \ /\ / / __|
    /\__/ /||  __/\ V /  __/ \__ \  | |\ \  __/\ V /| |  __/\ V  V /\__ \
    \____/ \__\___|\_/\___| |___/   \_\ \ \___| \_/ |_|\___| \_/\_/ |___/
    """)

  val databaseResult = databaseCreator.run()
  val cacheResult = initializeCache.run()
  Await.result(cacheResult, 2 minutes)

  val results = List(databaseResult, cacheResult)

  val ready = Future.sequence(results)
  ready.onComplete {
    case Success(res) => println("Finished in " + (System.currentTimeMillis() - startAt) + " ms")
    case Failure(res) => println("Startup ERROR" + res)
  }
}
