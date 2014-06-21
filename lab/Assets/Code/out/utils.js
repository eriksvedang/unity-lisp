import core;

static function pos_of_object(name) : Object {
	return function() : Object {/*let*/
		var go = GameObject.Find(name);
		return (go ? go.transform.position : "nil");
	}();
};