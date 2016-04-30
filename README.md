# Competition Practice Player


### Overview
Competition Practice Player is an Android application intended for West Coast Swing dancers who are practicing and preparing for competitions.

In West Coast Swing competitions, you receive your partner and song draw when you step onto the competition floor.  In competition, each song is played for either 1min 30sec or 2mins, so practicing to the remaining time in the song is generally undesireable.

For this reason, Competition Practice Player includes these features:

  - Populates with the user's Spotify playlists
  - Gives an option to end the song at 1:30 or 2:00
  - Gives a notification beep 5 seconds before the song ends
  - Gives a pause before playing the next song in the list

### Version
1.0.0

### Tech

Competition Practice Player uses a number of open source projects to work properly:

* [Retrofit 2.0] - A type-safe HTTP client for Android and Java
* [OKHttp] - Request/response API
* [RoboGuice] - Dependency injection


And of course Competition Practice Player itself is open source with a [public repository](https://github.com/alliejc/compeitionpracticeplayer)
 on GitHub.

### Installation

```sh
$ git clone [git-repo-url] Competition Practice Player
```
Open in Android studio

### Development

Want to contribute? Great!

Add your spotify api key to the gradel.properties file. You need the following key:

CLIENT_ID="{your key}"

### Todos

 - Write Tests
 - Change Activites to Fragments
 - Add a Navigation Drawer
 - Add an Artist list, Album List, and Song list
 - Add a search bar
 - Add customizeable time and beep options


[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [OkHttp]: http://square.github.io/okhttp/
   [Retrofit 2.0]: http://square.github.io/retrofit/
   [RoboGuice]: https://github.com/roboguice/roboguice


