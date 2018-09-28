function myFunction() {
     $.post("/luckywheel/wheels/1/spin", JSON.stringify({
         fullname: "Dinh Trong Long",
         email: "tronglongcntt@gmail.com"
     }), function(data) {
         alert("Data Loaded: " + data.label);
     }, "json");
 }
