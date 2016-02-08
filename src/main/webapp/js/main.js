(function() {
    $("#buzzer").click(function() {
        console.log("Clicked button");
        $.get("http://"+window.location.host+"/hue/", function(err, res) {
           console.log(err)
        });
    })
})();
