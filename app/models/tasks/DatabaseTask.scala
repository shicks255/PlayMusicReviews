package models.tasks

import akka.actor.ActorSystem
import com.google.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class DatabaseTask @Inject() (actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {

  actorSystem.scheduler.schedule(0.microseconds, 1.minute) {
    println("ahhhh")
  }

}
