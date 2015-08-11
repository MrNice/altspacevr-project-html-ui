# AltspaceVR Programing Project - Spaces Admin Web Frontend... in ClojureScript
Hey Altspace Team! I chose to stay inside my comfort zone and build a "futuristic" HTML 5 administration app. [You can see it live](https://fierce-earth-2877.herokuapp.com). Be sure to change membership & add spaces :)

### Main page
![an image of the main home page](https://cloud.githubusercontent.com/assets/2587335/9207633/a2eca904-4023-11e5-8054-ec40a9353918.png)
### Edit page
![an image of the edit page](https://cloud.githubusercontent.com/assets/2587335/9207634/a44e07ca-4023-11e5-9adf-37ba99599b34.png)
The JS file ends up being ~450kB.

## Local Setup
1. Install [Leiningen](http://leiningen.org/)
2. In the repo's root, do `lein do clean, figwheel`
3. Turn on a lava lamp and wait for it to bubble. Lein should be finished bootstrapping.
4. Open localhost:3449. Your terminal will have a repl into the running application.

##### **Optional**
* Type: `(in-ns 'ui.model)` followed by `@app-state` to see the app's state
* Then type: `(remove-space! 0)` and watch the interface update instantly!!

##### **To notice authentication**
* `(in-ns 'ui.core)` then `(session/put! :current-user 4)`. You can no longer edit things, and errors are logged. The buttons still exist bc the components don't react to session store changes.
* `(session/put! :current-user 9)` then create a space. Note who made that space.

## Reasoning
I really like building JavaScript applications with React. It's comfortable and easy to debug UI issues. It's really valuable to be explicit about view state management.

However, having used Flux & Reflux & rx-flux, the hard thing with React apps is application state management. It is very difficult to prevent flux stores from becoming big-balls-o-mud, particularly because JavaScript only has pass-by-reference, which leads to lots of defensive object cloning when dealing with temporary changes. It turns out that immutable-by-default data structures avoid most of these issues, so I was curious to try them out.

So, in an effort to challenge myself and get out of my comfort zone, I chose to do this project in ClojureScript. In the end, there are only 3 different places with mutable state, and they are all explicitly declared as such. Once I got the hang of using swap! to update the data structures, all state mutations became fairly straight forward.

ClojureScript made it very easy to separate concerns into views, actions, stores, and utility helper functions.

If language choice is problematic, I'll redo this project with rx-flux.

*Aside: another reason to try ClojureScript is John Carmack is toying with building Oculus Rift tooling with Racket, which is a similar Lisp dialect. Specifically, Carmack is interested in racket scripting with game engines, so that game developers can have a rapid iteration cycle similar to what figwheel does for ClojureScript*

## Tech Stack
* [Re-com](http://re-demo.s3-website-ap-southeast-2.amazonaws.com/) - Flexbox powered SPA framework for Reagent. Easy layouts & components.
* [Reagent](https://reagent-project.github.io/) - Reactive Hiccup -> React adapter. Hiccup just turns nested arrays into HTML.
* [React](http://facebook.github.io/react/) - View layer framework from Facebook, pairs nicely with CLJS immutable data
* [Secretary](https://github.com/gf3/secretary) - Macro based routing for ClojureScript.

### Development
* [Figwheel](https://github.com/bhauman/lein-figwheel) - Automatically reload code changes
* [Lein-LESS](https://github.com/montoux/lein-less) - Automatically compile LESS stylesheets into css

### Deployment
* Uberjar - Builds Clojure server & ClojureScript client
* [Heroku](https://www.heroku.com/) - Hosts for free!

## Caveats
Because I really want to work for Altspace VR, I want to keep polishing this interface and adding interactions / features. However, I've spent the 15 hours, and I think it's time to ship this MVP.

The interface is fairly slow to react to user input, taking up to 70ms to for scripting, then 30ms just for painting the new layout, on a 2014 MPBr. This could be significantly improved by actually supplying "key" attributes to the React components that Reagent creates.

I did not use any of the provided JavaScript libraries or data, although in retrospect I should have shimmed the JS data model instead of recreating it. I assume that part of the challenge was to prove that I understand how to interact with a promise-based API, so it's fortunate that I [have given presentations on using bluebird.js in node.js](http://www.slideshare.net/NicholasvandeWalle/promisesdraft).

Associated with the last point, the data model has no notion of time. Therefore, it doesn't use promises or callbacks to manage asynchronous updates which may fail. This is mainly because I wasn't thinking about persistance. However, since ClojureScript data structures are immutable, it's possible to take app-state snapshots, and gracefully roll back the app-state if an XHR fails. Since you change `app-state`, it also triggers a render.

The icons within the buttons are shifted 1 pixel right and down, because they expect their border to be only 1 pixel thick.

It's dog-slow and poorly laid out on my phone.

There's no pagination or incremental rendering, so 10k spaces take forever to load.

IDs are generated the mysql way, by incrementing a counter instead of using a globally unique identifier (GUID) which is the current best practice (it allows clients to create resources independant of the server).

The space creation interface does not validate fields.

Some code is duplicated unnecessarily. Specifically, the edit page's 600px width.

[Bacon ipsum](https://baconipsum.com/) makes me hungry.

No one knows who [Matt Jeffries](https://en.wikipedia.org/wiki/Matt_Jefferies) is.

## Final Thoughts
I thoroughly enjoyed building this interface. ClojureScript was delightful to use, aside from standard cryptic error messages. I was pleasantly surprised how easy it was to refactor across multiple files.

For example, originally I used the index of the space in the space vector / array as its ID. This broke when I wanted to sort spaces by type, as the order was implicitly coupled. It did not take very long to completely remove any notion of index, and update everything to use the new ID field.

Being able to target only latest chromium is very exciting to me. I really enjoyed being able to use flexbox instead of twitter's bootstrap.
