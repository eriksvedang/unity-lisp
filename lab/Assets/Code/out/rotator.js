import core;

import Mathf;

var rot_speed = 10.0;

function Update() : Object {
	this.transform.Rotate(new Vector3(0, (Time.deltaTime * rot_speed), 0));
	transform.localScale.x = (1.0 + (0.5 * Sin(Time.time)));
};