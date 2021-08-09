function myFunction(id){
    var content = document.getElementById(id);
    if (content.style.display === "block") {
        content.style.display = "none";
    } else {
        var divsToHide = document.getElementsByClassName("con");
        for(var i = 0; i < divsToHide.length; i++){
            divsToHide[i].style.display = "none";
        }
        content.style.display = "block";
    }
}
