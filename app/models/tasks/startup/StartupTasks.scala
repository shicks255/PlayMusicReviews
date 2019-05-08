package models.tasks.startup

import javax.inject.{Inject, Singleton}

@Singleton
class StartupTasks @Inject()(databaseCreator: DatabaseCreator, initializeCache: InitializeCache){
  val startAt = System.currentTimeMillis();
  println("Starting Steve's Review")
  println("""
     _____ _                 _       _____           _
    /  ___| |               ( )     | ___ \         (_)
    \ `--.| |_ _____   _____|/ ___  | |_/ /_____   ___  _____      _____
    `--. \ __/ _ \ \ / / _ \ / __|  |    // _ \ \ / / |/ _ \ \ /\ / / __|
    /\__/ /||  __/\ V /  __/ \__ \  | |\ \  __/\ V /| |  __/\ V  V /\__ \
    \____/ \__\___|\_/\___| |___/   \_\ \ \___| \_/ |_|\___| \_/\_/ |___/
    """)
  databaseCreator.run()
  initializeCache.run()

  println("Finished in " + (System.currentTimeMillis() - startAt) + " ms")
}
