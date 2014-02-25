// Copy this file into your Unity project

static function _add_fn(a, b) { return a + b; }
static function _sub_fn(a, b) { return a - b; }
static function _mul_fn(a, b) { return a * b; }
static function _div_fn(a, b) { return a / b; }
static function _less_than_fn(a, b) { return a < b; }
static function _greater_than_fn(a, b) { return a > b; }

static function inc(x) { return x + 1; }
static function dec(x) { return x - 1; }
static function identity(x) { return x; }

static function doall(e : IEnumerator) {
  while(e.MoveNext()) {
  }
  return null;
}

static function doall(coll : IEnumerable) {
  return doall(coll.GetEnumerator());
}

static function range(n) {
	return range(0, n, 1);
}

static function range(start, end) {
	return range(start, end, 1);
}

/*
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
*/

static function range(start, end, step) : IEnumerable {
	var n = (end - start) / step;
	if(n < 0) {
		throw new System.Exception("Range from " + start + " to " + end + " with step " + step);
	}
	var counter = start;
	for (var i = 0; i < n; i++) {
    yield counter;
    counter += step;
	};
}

static function rand_int(max) {
	return Random.Range(0, max);
}

static function rand_int(min, max) {
	return Random.Range(min, max);
}

static function rand_nth(coll : Array) {
  return coll[Random.Range(0, coll.Count)];
}

static function _full_copy_coll(coll : Array) {
  var newColl = new Array();
	for(var item in coll) {
		newColl.Add(item);
	}
	return newColl;
}

/*
static function shuffle(coll : Array) {
  var newColl = _full_copy_coll(coll);
  shuffle_BANG(newColl);
  return newColl;
}
*/

static function shuffle(coll : Array) {
  for(var i = 0; i < coll.Count; i++) {
    var j = Random.Range(0, i + 1);
    var temp = coll[i];
    coll[i] = coll[j];
    coll[j] = temp;
  }
  return coll;
}

/*
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
*/

static function reduce(f : Function, e : IEnumerator) {
  e.MoveNext();
  v = e.Current;
	while(e.MoveNext()) {
		v = f(v, e.Current);
	};
	return v;
}

static function reduce(f : Function, coll : IEnumerable) {
 return reduce(f, coll.GetEnumerator());
}

static function reduce(f : Function, start : Object, e : IEnumerator) {
	var v = start;
	while(e.MoveNext()) {
		v = f(v, e.Current);
	};
	return v;
}

static function reduce(f : Function, start : Object, coll : IEnumerable) {
  return reduce(f, coll.GetEnumerator());
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


/*
static function map(f : Function, coll : Array) {
	var newColl = new Array();
	for(var item in coll) {
		newColl.Add(f(item));
	}
	return newColl;
}
*/


static function map(f : Function, e : IEnumerator) {
  while(e.MoveNext()) {
    yield f(e.Current);
  }
}

static function map(f : Function, coll : IEnumerable) {
  return map(f, coll.GetEnumerator());
}

static function HashToStr(hash) : String {
	var s = "{";
	var i = 0;
	for(var o in hash) {
		s += str(o.Key) + ": " + str(o.Value);
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
		s += str(array[i]);
		if(i < array.length - 1) {
			s += ", ";
		}
	};
	s += "]";
	return s;
}

static function str(o) : String {
	if(o == null) {
		return "nil";
	}
	var t = o.GetType();
	if(o as Array || o as System.Object[] || o as System.Int32[] || o as System.Single[]) {
		return ArrayToStr(o);
	}
	else if(t == typeof(Hashtable)) {
		//return o.ToString();
    return HashToStr(o);
	}
	else if(t == typeof(Boo.Lang.Hash)) {
		return HashToStr(o);
	}
  else if(t == typeof(System.String)) {
		return o.ToString();
	}
  else {
    var enumerator = o as IEnumerator;
    var enumerable = o as IEnumerable;
    if(enumerator) {
      //print("enumerator!");
      return ArrayToStr(EnumeratorToArray(enumerator));
	  }
    else if(enumerable) {
      //print("enumerable!");
      return ArrayToStr(EnumerableToArray(enumerable));
	  }
  	else {
      //print(o.GetType());
		  return o.ToString();
	  }
  }
}

static function pp(o) {
	print(str(o));
	return null;
}

static function EnumeratorToArray(e : IEnumerator) {
  var l = new Array();
  while(e.MoveNext()) {
    l.Add(e.Current);
  }
  return l;
}

static function EnumerableToArray(coll : IEnumerable) {
  var e = coll.GetEnumerator();
  var l = new Array();
  while(e.MoveNext()) {
    l.Add(e.Current);
  }
  return l;
}


static function _full_copy_map(m : Hashtable) {
  var n = new Hashtable();
  for(var k in m.Keys) {
    n[k] = m[k];
  }
  return n;
}

static function assoc(m, k, v) {
  var n = _full_copy_map(m);
  n[k] = v;
  return n;
}

static function assoc_BANG(m, k, v) {
  m[k] = v;
  return m;
}

static function assoc_in_BANG(m, ks, v) {
  var i = 0;
  while(i < (ks.Length - 1)) {
    m = m[ks[i]];
    i++;
  }
  m[ks[(ks.Length - 1)]] = v;
}

static function update_in_BANG(m, ks, f) {
  var i = 0;
  while(i < (ks.Length - 1)) {
    m = m[ks[i]];
    i++;
  }
  m[ks[(ks.Length - 1)]] = f(m[ks[(ks.Length - 1)]]);
}






