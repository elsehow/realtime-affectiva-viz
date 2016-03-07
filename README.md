# reagent-figwheel-browserify

a copy of [reagent-template](https://github.com/reagent-project/reagent-template), with support for npm

## quickstart

clone this repository and

        npm install 
        lein figwheel

## installing npm modules

        npm install [my module]
        npm run build


## how does it work?

in `src/js/deps.js`, we expose namespaces to `window`

the `npm run build` command produces a browserif bundle in `target/cljsbuild/public/js/deps-bundle.js`

then, 
