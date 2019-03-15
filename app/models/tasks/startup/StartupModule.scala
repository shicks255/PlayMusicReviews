package models.tasks.startup

import com.google.inject.AbstractModule

class StartupModule extends AbstractModule{

  override def configure(): Unit = {
    bind(classOf[StartupTasks]).asEagerSingleton()
  }

}
