# jobluv

A rewrite of the rather verbose ruby version. found here: 

https://github.com/cmm0004/StockChecker/blob/master/lib/endpoints/jobluv.rb

## Usage

Add to a users keys

/jobluv @UserMentionName ++

Subtract from a users keys

/jobluv @UserMentionName --

Count # of keys

/jobluv @userMentionName ?

## BETA

returns a 7 sentence TL;DR (powered by smmry.com)

/tldr \<long email\>


## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

And Java8 JDK

And Clojure1.8

## Running

To start a local web server for the application, run:

    lein ring server
    or
    lein ring server-headless



