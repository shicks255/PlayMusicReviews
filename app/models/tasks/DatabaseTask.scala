package models.tasks

import akka.actor.ActorSystem
import com.google.inject.Inject
import models.Cache
import models.Cache._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class DatabaseTask @Inject() (actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {

  actorSystem.scheduler.schedule(0.microseconds, 1.minute) {
    println("ahhhh")

    println("______")
    println("Artists in cache...")
    Cache.printCache()
    println("______")
  }

}
