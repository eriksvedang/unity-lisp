import core;

function Start() : Object {
	/* nothingness */;
};

static function tests() : Object {
	assert(eq(55, reduce(_add_fn, range(1, 10))));
	assert(eq({"name": "Erik", "age": 27, "city": "Gbg "}, assoc({"name": "Erik", "age": 27}, "city", "Gbg")));
	assert(eq({"a": {"b": 10}}, assoc_in({"a": {"b": 27}}, ["a", "b"], 10)));
	assert(eq({"a": 10, "b": 21}, update_in({"a": 10, "b": 20}, ["b"], inc)));
	assert(eq([1, 2, 3, 4], conj([1, 2, 3], 4)));
	assert(eq([10, 11, 12, 13, 14, 15, 16, 17, 18, 19, "!!!"], conj(range(10, 20), "!!!")));
	function() : Object {/*let*/
		var x = {"a": 10, "b": {"c": 30, "d": 40}};
		update_in_BANG(x, ["b", "c"], function(__ARG__) : Object {
			return (-1 * __ARG__);
		});
		assert(eq({"a": 10, "b": {"c": -30, "d": 40}}, x));
		return true;
	}();
	return assert(eq([1, 2, 3, 4, 5], conj([1, 2, 3], 4)));
};