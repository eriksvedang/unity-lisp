# Unity Lisp

A program that translates (very Clojure-like) lisp code to Unity Script.

The target audience is people who desperately crave macros and lisp terseness when making games using the excellent [Unity](http://www.unity3d.com) game engine.

Tries to compile into efficient and readable Unity Script – not overly concerned with immutability at the moment (but still handles it a bit nicer than Unity Script). Uses the IEnumerable interface to get some of the benefits of laziness and mitigate the need for excessive copying of arrays.


## Usage

* Clone this repo and compile using [Leiningen](http://leiningen.org/):
```bash
lein uberjar
```
* Copy the resulting jar to a folder in your Unity project
* Also make sure that the 'core.js' file is somewhere in your project folder
* Run the jar in a folder with your Unity-lisp files:
```bash
java -jar unity-lisp-0.1.0-standalone.jar
```
* The output will be put into a subfolder called '/out'
* Attach the generated .js-files as components to your game objects and prefabs. When the source file (written in Unity Lisp) changes the component will be automatically updated!


## Examples

### Rotator
Here's a simple Unity component that will make a game object rotate infinitely:
```clojure
(def ^float rot-speed 10)

(defvoid Update []
  (.Rotate this.transform (new Vector3 0 (* Time.deltaTime rot-speed) 0)))
```
This will generate the following Unity Script:
```javascript
var rot_speed : float = 10;

function Update() {
    this.transform.Rotate(new Vector3(0, Time.deltaTime * rot_speed, 0));
}
```

### Want more?
Have a look at the test suit for more examples of what you can do.


## Important details
- *defvoid* must be used to define functions that calls a void function at tail position instead of returning a value, use this for overriding the built in MonoBehaviour methods. If anyone knows how to get around this limitation, please help out :)
- *defn* will generate a static function, use *defmethod* for generating methods that can mutate the state of your component
- keywords (i.e. :red) will compile to a string, use the funky λ:red syntax to make it into a function that looks up itself in a map


## Things that are missing from Clojure
* Macros (this is priority #1 right now)
* List comprehensions (also coming soon)
* Persistent data structures
* Metadata
* Very useful libraries like core.async, core.match, etc
* Lots more...


## Bugs
Tons of them still!

## License

Copyright © 2014 Erik Svedäng

Distributed under the Eclipse Public License, the same as Clojure.
