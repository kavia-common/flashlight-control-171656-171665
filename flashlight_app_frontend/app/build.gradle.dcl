androidApplication {
    namespace = "org.example.app"

    // Keep the app module lean
    dependencies {
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("androidx.core:core-ktx:1.13.1")
        // ConstraintLayout for activity_main
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    }
}
