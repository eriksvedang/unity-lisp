import core;

function Start() : Object {
	pp("Reduce the values 0 - 9 using addition => ", reduce(_add_fn, range(1, 10)));
	pp("Assoc a value to the map {:name 'Erik' :age 27} => ", assoc({"name": "erik", "age": 27}, "city", "Gothenburg"));
	pp("Nested assoc => ", assoc_in({"erik": {"age": 27}}, ["erik", "city"], "Gothenburg"));
	pp("Update a value inside the map {:a 10 :b 20} => ", update_in({"a": 10, "b": 20}, ["b"], inc));
	pp("Add something to the end of a vector [1 2 3] => ", conj([1, 2, 3], 4));
	pp("Add something to the end of a range => ", conj(range(10, 20), "!!!"));
	function() : Object {/*let*/
		var x = {"a": 10, "b": {"c": 30, "d": 40}};
		update_in_BANG(x, ["b", "c"], function(__ARG__) : Object {
			return (-1 * __ARG__);
		});
		return pp("Destructive update of a map => ", x);
	}();
	pp("Random item from collection => ", rand_nth(range(50, 70, 2)));
};