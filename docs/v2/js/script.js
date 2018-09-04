/*

Style   : MobApp Script JS
Version : 1.0
Author  : Surjith S M
URI	 : https://surjithctly.in/

Copyright © All rights Reserved

*/

$(function() {
	'use strict';

	/*-----------------------------------
	 * FIXED  MENU - HEADER
	 *-----------------------------------*/
	function menuscroll() {
		var $navmenu = $('.nav-menu');
		if ($(window).scrollTop() > 50) {
			$navmenu.addClass('is-scrolling');
		} else {
			$navmenu.removeClass('is-scrolling');
		}
	}
	menuscroll();
	$(window).on('scroll', function() {
		menuscroll();
	});
	/*-----------------------------------
	 * NAVBAR CLOSE ON CLICK
	 *-----------------------------------*/

	$('.navbar-nav > li:not(.dropdown) > a').on('click', function() {
		$('.navbar-collapse').collapse('hide');
	});
	/*
	 * NAVBAR TOGGLE BG
	 *-----------------*/
	var siteNav = $('#navbar');

	// eslint-disable-next-line no-unused-vars
	siteNav.on('show.bs.collapse', function(e) {
		$(this).parents('.nav-menu').addClass('menu-is-open');
	});

	// eslint-disable-next-line no-unused-vars
	siteNav.on('hide.bs.collapse', function(e) {
		$(this).parents('.nav-menu').removeClass('menu-is-open');
	});

	/*-----------------------------------
	 * ONE PAGE SCROLLING
	 *-----------------------------------*/
	// Select all links with hashes
	$('a[href*="#"]').not('[href="#"]').not('[href="#0"]').not('[data-toggle="tab"]').on('click', function(event) {
		// On-page links
		if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
			// Figure out element to scroll to
			var target = $(this.hash);
			target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
			// Does a scroll target exist?
			if (target.length) {
				// Only prevent default if animation is actually gonna happen
				event.preventDefault();
				$('html, body').animate({
					scrollTop: target.offset().top
				}, 1000, function() {
					// Callback after animation
					// Must change focus!
					var $target = $(target);
					$target.focus();
					if ($target.is(':focus')) { // Checking if the target was focused
						return false;
					} else {
						$target.attr('tabindex', '-1'); // Adding tabindex for elements not focusable
						$target.focus(); // Set focus again
					}
				});
			}
		}
	});
	/*-----------------------------------
	 * OWL CAROUSEL
	 *-----------------------------------*/
	var $testimonialsDiv = $('.testimonials');
	if ($testimonialsDiv.length && $.fn.owlCarousel) {
		$testimonialsDiv.owlCarousel({
			items: 1,
			nav: true,
			dots: false,
			navText: ['<span class="ti-arrow-left"></span>', '<span class="ti-arrow-right"></span>']
		});
	}

	var $galleryDiv = $('.img-gallery');
	if ($galleryDiv.length && $.fn.owlCarousel) {
		$galleryDiv.owlCarousel({
			nav: false,
			center: true,
			loop: true,
			autoplay: true,
			dots: true,
			navText: ['<span class="ti-arrow-left"></span>', '<span class="ti-arrow-right"></span>'],
			responsive: {
				0: {
					items: 1
				},
				768: {
					items: 3
				}
			}
		});
	}
}); /* End Fn */

$(document).ready(function() {
	/**
	 * inViewport jQuery plugin by Roko C.B.
	 * http://stackoverflow.com/a/26831113/383904
	 * Returns a callback function with an argument holding
	 * the current amount of px an element is visible in viewport
	 * (The min returned value is 0 (element outside of viewport)
	 */
	(function($, win) {
		$.fn.inViewport = function(cb) {
			return this.each(function(i,el) {
				function visPx(){
					var elementTop = $(el).offset().top;
					var elementBottom = elementTop + $(el).outerHeight();

					var viewportTop = $(win).scrollTop();
					var viewportBottom = viewportTop + $(win).height();

					cb.call(el, Math.max(0, Math.min(elementBottom - viewportTop, viewportBottom - elementTop)));
				}
				visPx();
				$(win).on('resize scroll', visPx);
			});
		};
	}(jQuery, window));

	$('#letUsKnow').click(function(e) {
		e.preventDefault();
		var feedbackBox = $('#feedback');

		feedbackBox.find('.popup-content').html(
			'<iframe class="feedbackForm" src="https://docs.google.com/forms/d/e/1FAIpQLSdh086Uw4dLGar0jKKvGZmQoERUzfhh0jbEnaNkhViohPaUUg/viewform?embedded=true" width="700" height="520" frameborder="0" marginheight="0" marginwidth="0">Loading...</iframe>'
		);
		feedbackBox.show();
	});

	// eslint-disable-next-line no-unused-vars
	$('.overlay .ti-close').click(function(e) {
		$(this).closest('.overlay.overlay-fullscreen').hide();
	});

	var animate = ['#discover-collage' ,'.std-logo'];
	for (var i = 0; i < animate.length; ++i) {
		$(animate[i]).inViewport(function(px) {
			if (px > 350) {
				$(this).addClass('lol-animation');
			}
		});
	}

	$('.cp-year').text(new Date().getFullYear());
});

/*
 * Replace all SVG images with inline SVG
 */
jQuery('svg-inline').each(function(){
	var $img = jQuery(this);
	var imgID = $img.attr('id');
	var imgClass = $img.attr('class');
	var imgURL = $img.attr('src');

	jQuery.get(imgURL, function(data) {
		// Get the SVG tag, ignore the rest
		var $svg = jQuery(data).find('svg');

		// Add replaced image's ID to the new SVG
		if(typeof imgID !== 'undefined') {
			$svg = $svg.attr('id', imgID);
		}
		// Add replaced image's classes to the new SVG
		if(typeof imgClass !== 'undefined') {
			$svg = $svg.attr('class', imgClass+' replaced-svg');
		}

		// Remove any invalid XML tags as per http://validator.w3.org
		$svg = $svg.removeAttr('xmlns:a');

		// Check if the viewport is set, if the viewport is not set the SVG wont't scale.
		if(!$svg.attr('viewBox') && $svg.attr('height') && $svg.attr('width')) {
			$svg.attr('viewBox', '0 0 ' + $svg.attr('height') + ' ' + $svg.attr('width'));
		}

		// Replace image with new SVG
		$img.replaceWith($svg);

	}, 'xml');

});

