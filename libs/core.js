// Copy this file into your Unity project

static function each(f : Function, coll : Array) {
	for (var i = coll.length - 1; i >= 0; i--) {
		coll[i] = f(coll[i]);
	};
}

static function doseq(f : Function, coll : Array) {
	for(var item in coll) {
		f(item);
	}
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
		return "null";
	}
	var t = o.GetType();
	if(o as Array || o as System.Object[] || o as System.Int32[] || o as System.Single[]) {
		return ArrayToStr(o);
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
}