package models

import com.google.inject.AbstractModule
import models.tasks.StartupTasks

class StartupModule extends AbstractModule{

  override def configure(): Unit = {
    bind(classOf[StartupTasks]).asEagerSingleton()
  }

}
