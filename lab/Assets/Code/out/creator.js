import core;

var prefab : GameObject;

function Start() : Object {
	create_stuff();
};

function create_stuff() : Object {
	return function() : Object {/*let*/
		var positions = [v3(-1, 0, -1), v3(1, 0, -1), v3(-1, 0, 1), v3(1, 0, 1), v3(0, 1, 0), v3(0, -1, 0)];
		return doall(map(function(__ARG__) : Object {
			return make_go(prefab, __ARG__);
		}, positions));
	}();
};

function make_go(prefab, pos) : Object {
	return GameObject.Instantiate(prefab, (pos + transform.position), Quaternion.identity);
};