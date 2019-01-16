(function ($) {

    // page is now ready, initialize the calendar...

    $('#calendar').fullCalendar({
        
       eventSources: [

    // your event source
    {
      url: '/temperature/get-events',
      type: 'GET',
      error: function() {
        alert('there was an error while fetching events!');
      },
      color: 'yellow',   // a non-ajax option
      textColor: 'black' // a non-ajax option
    }

    // any other sources...

  ]
        
        
    })


}(jQuery));