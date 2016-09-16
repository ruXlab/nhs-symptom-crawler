What is it?
==============

This webapp scrapes all conditions from [Health A-Z - Conditions and treatments](http://www.nhs.uk/Conditions/Pages/hub.aspx) 
and provides REST-like search

Originally it develped as a test 


What does it use?
================

* Ratpack
* Apache lucene 
* Jsoup


System dependencies 
=====================


* Java 8
* sh-like shell
* writable home directory


How to use
===========

* Navigate to root of the project
* Run ./gradlew 
* At first run app will scrape, parse and save pages as json files in ```$HOME/nscraper```
* After initial setup app is available on http://localhost:5050/
* To make queries just put query after slash in url, like [http://localhost:5050/symptoms of gout](http://localhost:5050/symptoms%20of%20gout)



