# cljsnode

Reference implementation for universal (nee isomporhic) Clojurescript React SPA.
Runs on Heroku using node express, bootstrap, reagent and Kioo templates,
with code shared between frontend and backend, where Figwheel hotloads code changes
to both.

Check it out at https://cljsnode.herokuapp.com

Fork on github as a starting point for your own projects.
Soon to be available as a Leiningen template.

## Requirements

leiningen, heroku, npm

## Run Locally

To start a server on your own computer:

    lein do clean, deps, compile
    lein run

Point your browser to the displayed local port.
Click on the displayed text to refresh.

## Deploy to Heroku

To start a server on Heroku:

    heroku apps:create
    git push heroku master
    heroku open

This will open the site in your browser.

## Development Workflow

Start figwheel for interactive development with
automatic builds and code loading:

    lein figwheel app server

Wait until Figwheel is ready to connect, then
start a server in another terminal:

    lein run

Open the displayed URL in a browser.
Figwheel will push code changes to the app and server.

Alternatively, to set the local environment from a .env file, start the server with:

    heroku local web

To test the system, execute:

    lein test

## License

Copyright © 2015-2018 Terje Norderhaug

Based on an app concept by Marian Schubert.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

# Update

Needed to add a dependency to the project file to get this to run off the cloned repo:
https://github.com/njordhov/cljsnode