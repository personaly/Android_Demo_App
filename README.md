# Personaly

For more details please read: https://persona.ly/knowledge/?ht_kb=android-integration-rephrase


Add the following lines to build.gradle of your app and build the project:

repositories {
    maven { url 'https://dl.bintray.com/personaly/maven' }
}

dependencies {
    compile 'ly.persona.sdk:personaly:1.0.621@aar'
}
