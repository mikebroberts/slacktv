# slacktv

SlackTV is a web app to show most recent messages from a [Slack](https://slack.com/) channel, suitable for
office TV Dashboards.

## Usage

At present the best way to use this app is to fork, or download, the repository. To configure SlackTV you
need to set 2 environment variables:

* `SLACK_KEY` : An API key / token as decribed at https://api.slack.com/web
* `SLACKTV_CHANNEL` : The name of the channel you want to display

You can set these environment variables in the context in which you are running the app, or if you're 
a little more savvy with clojure you can create a `profiles.clj` file in the project root and set them there.

To run a development SlackTV from a terminal run `bin/lein ring server`. Wait
a few seconds and after starting the app it should launch your default browser
pointing to the app (at localhost:3000)

To run in production you have a few options:
* Run `bin/lein with-profile production trampoline run -m slacktv.web`. By
default the app will run on port 3001, but you can change that by setting a `PORT`
environment variable.
* If running on Heroku just push the source to a new Heroku app - the included
`Procfile` file should do all you need
* If running on Amazon Elastic Beanstalk create a generic 'Single Container' docker 
application for the application artifact run the following command, and use the 
resulting zip file: `git archive --format=zip HEAD > slacktv.zip`
* If you're more Clojure savvy you can compile an Uberjar or Uberwar and use that.

I'd welcome feedback in Github, or on twitter @mikebroberts
