Helium library
==============

This is a small Java library written during the implementation of OpenTrans that implements
some basic general-purpose mechanisms. I decided to keep it separately, because it may be
useful some day.

So, what you can find here?

* Primitives for building in-memory OOP domain model:
    - data manager (repository),
    - relationship construction,
    - unit of work
    - primitives for building working copies.
* History manager

Prerequisites
-------------

The libraries are written in Java 7 and managed by Maven 3. All the dependencies are defined
in `pom.xml`.

Licensing
---------

The source code is available under the terms of New BSD license.

Author: Tomasz Jędrzejewski