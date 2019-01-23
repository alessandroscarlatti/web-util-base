function doSomethingLong() {

    var csrfParameterName = $("meta[name=_csrf]").attr("content");
    var csrfParameterValue = $("meta[name=_csrf_parameter]").attr("content");

    function inProgress() {
        $(".hideInProgress").attr("disabled", "");
        $(".showInProgress").css("display", "initial");
        $(".showOnDone").css("display", "none");
        $(".showOnError").css("display", "none");
    }

    function completed() {
        $(".hideInProgress").removeAttr("disabled");
        $(".showInProgress").css("display", "none");
        $(".showOnDone").css("display", "inherit");
        $(".showOnError").css("display", "none");
    }

    function failed(response) {
        $(".hideInProgress").removeAttr("disabled");
        $(".showInProgress").css("display", "none");
        $(".showOnDone").css("display", "none");
        $(".showOnError").css("display", "inherit");

        $(".showOnErrorAlert").text("Error: " + response.status);
    }

    console.log("csrf parameter", csrfParameterName);
    console.log("csrf token", csrfParameterValue);

    inProgress();

    $.post("/activities/basicActivity/task1", {
        ttl: "5000",
        [csrfParameterName]: csrfParameterValue
    }).done(function (data, status, xhr) {
        completed();
        console.log("response", data);
        console.log("status", status);
        console.log("xhr", xhr);
    }).fail(function (response) {
        console.log("response", response);
        console.log("HTTP " + response.status);
        failed(response);
    });
}