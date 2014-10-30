# CrossRef Util

Library for common CrossRef-related tasks, written in Clojure.

## Installation

With Leiningen:

    [crossref-util "0.1.6"]
    
## Usage

### Auth

Authenticate CrossRef member credentials. Returns success and list of DOI prefixes the user is allowed to administrate. Currently has a FIXME because the SSL certificate doesn't appear to be in the standard JVM key store.

 - `authenticate` - pass in CrossRef username and password to authenticate.

When run in test mode, which is set with `set-test-mock-mode!`, this looks up a set of test-users, each of which returns a different set of prefixes. See source for details.

### Config

Simple config file reader. Usage: `(:require [crossref.util.config :refer [config]])`. `config` will be a hashmap of config values.

 - `config` - configuration read from `config.edn` (falling back to `config.dev.edn`)

### Date

CrossRef dates aren't ISO dates. The [CrossRef Deposit Schema](http://www.crossref.org/help/schema_doc/4.3.4/4_3_4.html#month) defines a set of special values to represent dates like 'Spring 2010' or 'Third Quarter of 2013'. This is a library for parsing, storing and representing these dates in a [clj-time](https://github.com/seancorfield/clj-time)/[JodaTime](http://www.joda.org/joda-time/) compliant way.

 - `crossref-date` - build CrossRef date with year [month [day]]
 - `parse` - parse string resembling ISO8601 into CrossRef date

### DOI

A couple of functions for validating and normalizing [DOIs](http://www.crossref.org/02publishers/doi_display_guidelines.html).

 - `normalise-doi` Convert a DOI to comply with CrossRef display guidelines.
 - `non-url-doi` Convert a DOI to remove leading scheme.
 - `get-prefix` Extract the prefix from a DOI.

### String

A couple of common string functions that may be useful when dealing with CrossRef metadata.

 - `remove-leading` Remove a prefix from a string if present.
 - `add-leading` Add a prefix from a string if not present.
 - `md5` MD5 of string
 - `parse-int` Parse an int from a string to a number, even if there is other text in it.

### URL

A couple of URL functions that may be useful when dealing with CrossRef metadata.

 - `http-scheme?` Does a string have a URI scheme?
 - `ensure-scheme` Ensure a string has an HTTP URI scheme.

## Testing

`lein test`

## Development

Note that the `date` namespace includes a `deftype` which can cause class-file compilation errors. If you experience compilation errors whilst working on this, clean your `target` directory and re-compile. The `deftype` was used rather than `defrecord` because of interface clashes (see comments in `date.clj`).

## License

Copyright © 2014 CrossRef

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
