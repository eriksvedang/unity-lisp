import core;

var original_color : Color;

function Start() : Object {
	original_color = this.renderer.material.color;
};

function OnMouseEnter() : Object {
	this.renderer.material.color = Color.red;
};

function OnMouseExit() : Object {
	this.renderer.material.color = original_color;
};