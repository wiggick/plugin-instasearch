# InstaSearch (revamped) WIP - Eclipse plug-in for quick code search



![](./instasearch/icons/search_results.gif) InstaSearch is an  [Eclipse IDE](http://eclipse.org) plug-in for performing quick and advanced search of workspace files. It indexes files using [Lucene] 2.9.1(http://lucene.apache.org/core/) and keeps the index up to date automatically. The search is performed instantly as-you-type and resulting files are displayed in an Eclipse view.

Each file then can be previewed using few most matching and relevant lines. A double-click on the match leads to the matching line in the file.

## Work in Progress

This is an attempt of getting the archived [/ajermakovics/eclipse-instasearch](https://github.com/ajermakovics/eclipse-instasearch/) working on the latest versions of Eclipse

## The 2.9.1 Lucene jars in the project
Yeah I know, just making it easier and it's my whole project backup.

[https://www.apache.org/licenses/LICENSE-2.0.txt (https://www.apache.org/licenses/LICENSE-2.0.txt)

## What's changing

### Slightly newer version of Lucene
The original version implements Lucene Token class(es) that were deprecated.  The
last version of Lucene to use the class was 2.9.1. 

### It's plugin project
I couldn't get maven-tycho to work with lucene that the original project used so  but started over
by packaging the lucene 2.9.1 jars by importing them into a new OSGI template and then copying the SRC files over, mostly as is.  

I changed the packaging to org.intersuite so as not to conflict with the original plugin, and well, the original is a dead project.  .

I'm working with Eclipse 2021-03 and Java 16.0.1

## What's Working
Runs under debug, builds indexes, searches, etc.  

## TODO
Testing and export and install as a plugin

Try with Eclipse 2021-06 etc.

Far Future: Use latest Lucene Jar's and revamp to not use the deprecated classes

## Use

Once InstaSearch is successfully installed, you'll see a nice little "InstaSearch" search tab appear at the bottom:

You can also click the `Search` menu option at the top --> `InstaSearch...`

## Main Features

* Instantly shows search results
* Shows a preview using relevant lines
* Periodically updates the index
* Matches partial words (e.g. case in CamelCase)
* Opens and highlights matches in files
* Searches JAR source attachments
* Supports filtering by extension/project/working set


## Search Tips

Lucene [query syntax](http://lucene.apache.org/core/old_versioned_docs/versions/3_0_0/queryparsersyntax.html) can be used for searching. This includes:

* Wildcard searches
  * `app* initialize`
* Excluding words
  * `application -initialize`
* Fuzzy searches to find similar matches
   * `application init~`
* Limit by location - directory, projects or working set
   * `proj:MyProject,OtherProject  application  init `
   * `ws:MyWorkingSet  dir:src  init `
* Limit by filename, extension or modification time
   * `name:app*  ext:java,xml,txt  modified:yesterday  `
* Search by file name initials (e.g. FOS to find FileOutputStream.java)
   * `name:FOS`

To exclude some folders from search indexing, mark them as *Derived* in the folder's properties.
There are also useful [Eclipse Search Tips](https://github.com/ajermakovics/eclipse-instasearch/wiki/Eclipse-search-tips).

**Note**: Fuzzy search is started automatically if no exact matches are found



## Authors/Contributors

Current Author: [Chris Wigginton](http://github.com/wiggick)

Original Author:  [Andrejs Jermakovics](http://github.com/ajermakovics)

Contributors:  [Holger Voormann](http://eclipsehowl.wordpress.com/), [solganik](https://github.com/solganik), [on github](https://github.com/ajermakovics/eclipse-instasearch/graphs/contributors)

Contributions are welcome so feel free to get in touch or create a pull request, however this is yet another of my projects and I have a full work schedule and my attention is a bit flighty.

