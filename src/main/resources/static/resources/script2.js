function TaskExecution(paramsObjOrConfig) {

    let config = null;
    let params = null;
    if (paramsObjOrConfig.constructor.name === "Function") {
        config = paramsObjOrConfig;
    } else {
        params = paramsObjOrConfig;
    }

    if (config == null) {
        config = function(){};
    }

    if (params == null) {
        params = {};
    }

    let context = {
        params: params,
        status: "STARTING",
        exitCode: "UNKNOWN"
    };

    let listenerKey = 0;

    function isRunning() {
        return !this.isFinished();
    }

    function isFinished() {
        return (this.context.status === "COMPLETED" || this.context.status === "FAILED" || this.context.status === "STOPPED");
    }

    function statusStarting() {
        // only allow if task is ready.
        context.status = "STARTING";
        context.exitCode = "UNKNOWN";
        updateSubscribers();
    }

    function statusStarted() {
        context.status = "STARTED";
        updateSubscribers();
    }

    function statusStopping() {
        context.status = "STOPPING";
        updateSubscribers();
    }

    function statusStopped(exitCode) {
        if (exitCode == null)
            exitCode = "STOPPED";

        context.status = exitCode;
        updateSubscribers();
    }

    /**
     * @param args array of args, either a result object,
     * or an exit code object and a result object.
     */
    function statusCompleted(...args) {
        context.status = "COMPLETED";
        if (args.length === 1) {
            context.exitCode = "COMPLETED";
            context.result = args[0];
            updateSubscribers();
        } else if (args.length === 2) {
            context.exitCode = args[0];
            context.result = args[1];
            updateSubscribers();
        }
    }

    /**
     * @param args array of args, either a result object,
     * or an exit code object and a result object.
     */
    function statusFailed(...args) {
        context.status = "FAILED";

        if (args.length === 1) {
            context.exitCode = "FAILED";
            context.result = args[0];
            updateSubscribers();
        } else if (args.length === 2) {
            context.exitCode = args[0];
            context.result = args[1];
            updateSubscribers();
        }
    }

    // default events, can be overriden
    let events = {
        starting: function() {
            statusStarting();
        },
        started: function() {
            statusStarted();
        },
        completed: function(...args) {
            statusCompleted(...args);
        },
        failed: function(...args) {
            statusFailed(...args);
        },
        stopping: function() {
            statusStoping();
        },
        stopped: function(...args) {
            statusStopped(...args)
        }
    };

    // may not need this...
    let stopEvents = {};

    let subscribers = {};

    function listen(listener) {
        listenerKey++;
        subscribers["listener" + listenerKey] = listener;
    }

    function updateSubscribers() {
        for (let key in subscribers) {
            if (subscribers.hasOwnProperty(key)) {
                // notify the subscriber
                subscribers[key]();
            }
        }
    }

    let execution = {
        get events() {
            return events
        },
        set events(newEvents) {
            events = newEvents;
        },
        get context() {
            return context
        },
        get subscribers() {
            return subscribers
        },
        get updateSubscribers() {
            return updateSubscribers;
        },
        statusStarting,
        statusStarted,
        statusCompleted,
        statusFailed,
        statusStopping,
        statusStopped,
        isFinished,
        isRunning,
        listen
    };

    config(execution);

    return execution;
}

function Activity() {
    return (
        <div>
            <PageLoader/>
            <PageLoader/>
            <PageLoader/>
        </div>
    );
}

class PageLoader extends React.Component {

    constructor(props) {
        super(props);
        this.execution = null;

        this.load = this.load.bind(this);
    }

    load() {
        if (this.execution == null || this.execution.isFinished()) {
            this.execution = new TaskExecution({ttl: this.refs.ttl.value})

            this.execution.listen(this.forceUpdate.bind(this))
        }

        this.doLoad(this.execution);
    }

    doLoad(execution) {
        var csrfParameterName = $("meta[name=_csrf]").attr("content");
        var csrfParameterValue = $("meta[name=_csrf_parameter]").attr("content");
        console.debug("making request.");

        execution.events.started();
        $.post("/activities/basicActivity/task1", {
            ttl: execution.context.params.ttl,
            [csrfParameterName]: csrfParameterValue
        }).done(function (data, status, xhr) {
            execution.events.completed(data);
            console.debug("response", data);
            console.debug("status", status);
            console.debug("xhr", xhr);
        }).fail(function (response) {
            console.debug("response", response);
            console.debug("HTTP " + response.status);
            execution.events.failed(response);
        });
    }

    render() {
        if (this.execution == null) {
            return this.renderReady();
        }

        switch (this.execution.context.status) {
            case "STARTING":
                return this.renderReady();
            case "STARTED":
                return this.renderInProgress();
            case "COMPLETED":
                return this.renderCompleted();
            case "FAILED":
                return this.renderFailed();
        }
    }

    renderReady() {
        return (
            <div>
                <input type="text" ref="ttl" />
                <button className="btn btn-info" onClick={this.load}>Go</button>
                <p></p>
            </div>
        )
    }

    renderInProgress() {
        return (
            <div>
                <button className="btn btn-info" disabled>Go</button>
                <img className="showInProgress loader" src="/resources/loading.gif" alt="loading"/>
                <p></p>
            </div>
        )
    }

    renderCompleted() {
        return (
            <div>
                <input type="text" ref="ttl" />
                <button className="btn btn-info" onClick={this.load}>Go</button>
                <p></p>
                <div className="alert alert-success">Done. Result: {this.execution.context.result.toString()}</div>
            </div>
        )
    }

    renderFailed() {
        return (
            <div>
                <input type="text" ref="ttl" />
                <button className="btn btn-info" onClick={this.load}>Go</button>
                <p></p>
                <div className="showOnErrorAlert alert alert-danger">Error: {this.execution.context.result.toString()}</div>
            </div>
        )
    }
}

$(function () {
    ReactDOM.render(<Activity/>, $("#activity")[0]);
});