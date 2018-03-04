

# Overview

This project is an extension to `h2zero` that generates a restful interface to the database (GET, POST, PUT, DELETE).  

## What you need to do

  1. Create an extension class that `extends` `synapticloop.h2zero.extension.Extension`
  1. Implement the `public void generate(JSONObject extensionOptions, Database database, Options options, File outFile, boolean verbose) throws RenderException, ParseException;` method
  1. Set-up the h2zero file:
  1. Away you go

## The h2zero configuration file

see the `src/test/resources/sample-include-sqlite3.h2zero` file for a configuration example below


## A simple (ish) example

The main class entry point is the `RoutemasterRestfulServletExtension.java`