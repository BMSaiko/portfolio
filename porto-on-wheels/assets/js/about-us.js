$(document).ready(function() {

    // Multi-Item Carousel
    var multiItemCarousel = function() {
      $('.carousel-multi-item').each(function(){
        var $this = $(this);
        var itemsPerSlide = $this.data('items-per-slide') || 4;
        var items = $this.find('.carousel-item');
        var itemsTotal = items.length;
        var slideInterval = $this.data('interval') || 5000;
  
        if (itemsTotal > itemsPerSlide) {
          for (var i = 0; i < itemsTotal; i += itemsPerSlide) {
            var slideItems = items.slice(i, i + itemsPerSlide);
  
            var slide = $('<div/>', {
              class: 'carousel-item'
            }).append(slideItems);
  
            $this.find('.carousel-inner').append(slide);
          }
        }
  
        $this.find('.carousel-item').first().addClass('active');
  
        if (itemsTotal <= itemsPerSlide) {
          $this.find('.carousel-indicators, .carousel-control').hide();
        }
  
        $this.carousel({
          interval: slideInterval,
          pause: 'hover'
        });
      });
    };
  
    multiItemCarousel();
  
    $(window).on('resize', function() {
      multiItemCarousel();
    });
  
  });
  