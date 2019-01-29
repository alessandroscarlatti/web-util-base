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
        statusStopped
    };

    config(execution);

    return execution;
}

function toUpperCaseInOneSecond(execution) {
    execution.events.started();
    setTimeout(function () {
        // it should take a second to get here...
        let result = execution.context.params.text.toUpperCase();
        execution.events.completed(result);
    }, 1000);
}

class Activity extends React.Component {

    constructor(props) {
        super(props);

        this.invocation = null;
        this.invoke = this.invoke.bind(this);
    }

    invoke() {
        // don't allow invocation if task already in progress
        if (this.invocation == null || this.invocation.context.status === "COMPLETED" || this.invocation.context.status === "STOPPED") {
            this.invocation = new TaskExecution({text: "qwer"});
            this.invocation.subscribers["react"] = () => {
                this.forceUpdate();
            }
        }

        toUpperCaseInOneSecond(this.invocation);
    }

    render() {
        return (
            <div className="container">
                <div>Task Template</div>
                <button onClick={this.invoke}>Go</button>
                {this.renderStatus()}
            </div>
        )
    }

    renderStatus() {
        if (this.invocation == null) {
            return (
                <div>None</div>
            )
        }

        switch (this.invocation.context.status) {
            case "FAILED":
                return (
                    <div>State: FAILED with error {this.invocation.context.result.toString()}</div>
                );
            case "COMPLETED":
                return (
                    <div>State: COMPLETED with result {this.invocation.context.result}</div>
                );
            default:
                return (
                    <div>State: {this.invocation.context.status}</div>
                )
        }
    }
}

const activity = document.getElementById("activity1");
ReactDOM.render(<Activity/>, activity);