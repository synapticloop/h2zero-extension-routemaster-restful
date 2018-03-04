
# How to test

As the extension will generate additional code for the project, there is a 
slight roundabout way of testing the code.

  1. build the code and publish it to maven local
  1. have a separate build.gradle file that will reference the class path (including maven local repository)

The simplest way is to:

`gradle build -x test`

then

`gradle -b build.h2zero.sqlite3.gradle h2zero`

or more simply (for *NIX machines)

`gradle build -x test; gradle -b build.h2zero.sqlite3.gradle h2zero`

