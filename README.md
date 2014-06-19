# Unity Lisp

A Clojure program that translates (very Clojure-like) Lisp code to Unity Script.

The target audience is people who desperately crave macros and lisp terseness when making games using the excellent (Unity)[http://www.unity3d.com] game engine.

Tries to compile into efficient and readable Unity Script – not overly concerned with immutability at the moment (but a lot better than normal C# / Unity Script). Uses the IEnumerable interface to get some of the benefits of laziness and mitigate the need for excessive copying of arrays.


## Usage

1. Clone this repo and compile using Leiningen:
```bash
lein uberjar
```
2. Copy the resulting jar to a folder in your Unity project
3. Also make sure that the 'core.js' file is somewhere in your project folder
4. Run the jar in a folder with your Unity-lisp files:
```bash
java -jar Unity-Lisp-0.1.0-uberjar.jar
```
5. The output will be put into a subfolder called '/out'
6. Attach the generated .js-files as components to your game objects and prefabs. When the source file (written in Unity Lisp) changes the component will be automatically updated!


## Important information
```clojure
- *defvoid* must be used to define functions that calls a void function at tail position instead of returning a value, use this for overriding the built in MonoBehaviour methods
- *defn* will generate a static function, use *defmethod* for generating methods that can mutate the state of your component
- keywords (i.e. :red) will compile to a string, use the funky λ:red syntax to make it into a function that looks up itself in a map
```


## Examples
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

Have a look at the test suit for more examples of what you can do.


## Bugs
Tons of them still!

## License

Copyright © 2014 Erik Svedäng

Distributed under the Eclipse Public License, the same as Clojure.
