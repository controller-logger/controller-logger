$(function () {
	// trigger tooltips on hovor for desktops and on click for mobile devices
	$("[data-toggle=\"tooltip\"]").tooltip({trigger: window.orientation ? "click" : "hover"});

	// open tooltips by default for .always-show
	$(".annotation.always-show[data-toggle=\"tooltip\"]").tooltip({trigger: ""}).tooltip("show");

	// set 1 CSS pixel = 1 screen pixel. Strangely, browsers don't do this on their own.
	$("#viewport").attr(
		"content",
		"user-scalable=no, initial-scale=" + (1 / window.devicePixelRatio) + ", width=device-width"
	);
	// adjust font size as well to accomodate the above applied rescaling
	$("body").css("font-size", parseInt($("body").css("font-size")) * window.devicePixelRatio);

	$(".example pre").tooltip("show");
});