function myFunction(id){
    var content = document.getElementById(id)
    if (content.style.display === "flex") {
        content.style.display = "none";
    } else {
        content.style.display = "flex";
    }
}