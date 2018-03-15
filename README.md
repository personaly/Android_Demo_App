# Personaly

Demo project that demonstrates how to impement Personaly SDK on Android.

**MainActivity.class** has working example with all the main ad types: Rewarded, Interstitial, Native, Popup, App Wall, Offer Wall.
 
 **AdMobActivity.class** shows how-to implement AdMob with Personaly mediation adapter.
 
  **MoPubActivity.class** shows how-to implement MoPub with Personaly mediation adapter.

For more details please read official documentation: https://persona.ly/knowledge/?ht_kb=android-integration-rephrase


Configuration for build.gradle:

```
repositories {
    maven { url 'https://dl.bintray.com/personaly/maven' }
}

dependencies {
    compile 'ly.persona.sdk:personaly:1.0.632@aar'
    compile 'ly.persona.sdk:mediation_admob:1.0.621@aar'
    compile 'ly.persona.sdk:mediation_mopub:1.0.621@aar'
}
```
