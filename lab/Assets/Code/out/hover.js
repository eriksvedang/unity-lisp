import core;

var original_color : Color;

var hover_color : Color = Color.red;

function Start() : Object {
	original_color = this.renderer.material.color;
};

function OnMouseEnter() : Object {
	this.renderer.material.color = hover_color;
};

function OnMouseExit() : Object {
	this.renderer.material.color = original_color;
};