// Copy this file into your Unity project

static function _add_fn(a, b) { return a + b; }
static function _sub_fn(a, b) { return a - b; }
static function _mul_fn(a, b) { return a * b; }
static function _div_fn(a, b) { return a / b; }

static function inc(x) { return x + 1; }
static function dec(x) { return x - 1; }
static function identity(x) { return x; }

static function range(n) {
	return range(0, n, 1);
}

static function range(start, end) {
	return range(start, end, 1);
}

static function range(start, end, step) {
	var n = (end - start) / step;
	if(n < 0) {
		throw new System.Exception("Range from " + start + " to " + end + " with step " + step);
	}
	var l = new int[n];
	var counter = start;
	for (var i = 0; i < n; i++) {
		l[i] = counter;
		counter += step;
	};
	return l;
}

static function rand_int(max) {
	return Random.Range(0, max);
}

static function rand_int(min, max) {
	return Random.Range(min, max);
}

static function reduce(f : Function, coll : Array) {
	current = coll[0];
	for (var i = 1; i < coll.length; i++) {
		current = f(current, coll[i]);
	};
	return current;
}

static function reduce(f : Function, start : Object, coll : Array) {
	//print("Will reduce " + coll + " using function " + f);
	current = start;
	for (var i = 0; i < coll.length; i++) {
		current = f(current, coll[i]);
	};
	return current;
}

// Do function f to each item in coll (modifies the collection)
static function each_BANG(f : Function, coll : Array) {
	for (var i = coll.length - 1; i >= 0; i--) {
		coll[i] = f(coll[i]);
	};
	return null;
}

// Do funcion f for each item in coll
static function foreach(f : Function, coll : Array) {
	for(var item in coll) {
		f(item);
	}
	return null;
}

static function map(f : Function, coll : Array) {
	var newColl = new Array();
	for(var item in coll) {
		newColl.Add(f(item));
	}
	return newColl;
}

static function HashToStr(hash) : String {
	var s = "{";
	var i = 0;
	for(var o in hash) {
		s += Str(o.Key) + ": " + Str(o.Value);
		if(i < hash.Count - 1) {
			s += ", ";
		}
		i++;
	}
	s += "}";
	return s;
}

static function ArrayToStr(array : Array) : String {
	var s = "[";
	for (var i = 0; i < array.length; i++) {
		s += Str(array[i]);
		if(i < array.length - 1) {
			s += ", ";
		}
	};
	s += "]";
	return s;
}

static function Str(o) {
	if(o == null) {
		return "nil";
	}
	var t = o.GetType();
	if(o as Array || o as System.Object[] || o as System.Int32[] || o as System.Single[]) {
		return ArrayToStr(o);
	}
	else if(t == typeof(Hashtable)) {
		return o;
	}
	else if(t == typeof(Boo.Lang.Hash)) {
		return HashToStr(o);
	}
	else {
		return o;
		//throw "Function Str can't handle type " + o.GetType();
	}
}

static function pp(o) {
	print(Str(o));
	return null;
}
