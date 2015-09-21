# Zenefits note project (in cljs first)
This is a refactoring of my Altspace project to be a Zenefits project.

## Reasoning
I built something very similar to the zenefits note taking application for Altspace VR. I wanted to see how difficult a "large" refactor would be.

I'll be rebuilding this in redux so there will be a JavaScript version that shares the same HTML and CSS.

## Results
It was fairly straightforward to remove the extra webpages and shove the edit-page component into the note-cards. Then I removed the edit atom, and configured the edit page to rely on the main state atom.

In the process, I ended up fixing CSS bugs I left in before.

I'm probably happiest that I was able to remove over a hundred lines of code and simplify the app's model state.

## Explore
### Local Setup
1. Install [Leiningen](http://leiningen.org/)
2. In the repo's root, do `lein do clean, figwheel`
3. Turn on a lava lamp and wait for it to bubble. Lein should be finished bootstrapping.
4. Open localhost:3449. Your terminal will have a repl into the running application.

##### **Optional**
* Type: `(in-ns 'ui.model)` followed by `@app-state` to see the app's state
* Then type: `(remove-space! 0)` and watch the interface update instantly!!

## Tech Stack
* [Re-com](http://re-demo.s3-website-ap-southeast-2.amazonaws.com/) - Flexbox powered SPA framework for Reagent. Easy layouts & components.
* [Reagent](https://reagent-project.github.io/) - Reactive Hiccup -> React adapter. Hiccup just turns nested arrays into HTML.
* [React](http://facebook.github.io/react/) - View layer framework from Facebook, pairs nicely with CLJS immutable data
* [Secretary](https://github.com/gf3/secretary) - Macro based routing for ClojureScript.

### Development
* [Figwheel](https://github.com/bhauman/lein-figwheel) - Automatically reload code changes
