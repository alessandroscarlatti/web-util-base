function doSomethingLong() {

    var csrfParameterName = $("meta[name=_csrf]").attr("content");
    var csrfParameterValue = $("meta[name=_csrf_parameter]").attr("content");

    console.log("csrf parameter", csrfParameterName)
    console.log("csrf token", csrfParameterValue)

    $.post("task1", {
        ttl: "5000",
        [csrfParameterName]: csrfParameterValue
    }).done(function(data, status, xhr) {
        console.log("response", data)
        console.log("status", status)
        console.log("xhr", xhr)
    });

    console.log("hello world.")
}