var spinner;
var body_spinner;
var spin_target;

var spinner_opts = {
    lines:13, // The number of lines to draw
    length:20, // The length of each line
    width:10, // The line thickness
    radius:30, // The radius of the inner circle
    corners:1, // Corner roundness (0..1)
    rotate:0, // The rotation offset
    direction:1, // 1: clockwise, -1: counterclockwise
    color:'#000', // #rgb or #rrggbb or array of colors
    speed:1, // Rounds per second
    trail:60, // Afterglow percentage
    shadow:false, // Whether to render a shadow
    hwaccel:false, // Whether to use hardware acceleration
    className:'spinner', // The CSS class to assign to the spinner
    zIndex:2e9, // The z-index (defaults to 2000000000)
    top:'50%', // Top position relative to parent
    left:'50%' // Left position relative to parent
};

function indicatorShow(parentId) {
    spin_target = document.getElementById(parentId);
    spin_target.classList.add("spin_background");
    spinner = new Spinner(spinner_opts).spin(spin_target);
}

function indicatorHide() {
    spinner.stop();
    spin_target.classList.remove("spin_background");
}

function bodyStartProgress() {
    var body_target = document.getElementById("eltiland_body");
    body_target.classList.add("spin_background");
    body_spinner = new Spinner(spinner_opts).spin(body_target);
}

function bodyStopProgress() {
    var body_target = document.getElementById("eltiland_body");
    if (body_target.classList.contains("spin_background")) {
        body_target.classList.remove("spin_background");
        body_spinner.stop();
    }
}