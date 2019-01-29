function TaskExecution(paramsObjOrConfig) {

    let config = null;
    let params = null;
    if (paramsObjOrConfig.constructor.name === "Function") {
        config = paramsObjOrConfig;
    } else {
        params = paramsObjOrConfig;
    }

    if (config == null) {
        config = function () {
        };
    }

    if (params == null) {
        params = {};
    }

    let context = {
        params: params,
        status: "STARTING",
        exitCode: "UNKNOWN",
        startTime: null,
        endTime: null
    };

    let listenerKey = 0;

    function isRunning() {
        return !this.isFinished();
    }

    function isFinished() {
        return (context.status === "COMPLETED" || context.status === "FAILED" || context.status === "STOPPED");
    }

    function statusStarting() {
        if (isFinished())
            return;

        // only allow if task is ready.
        context.status = "STARTING";
        context.exitCode = "UNKNOWN";
        updateSubscribers();
    }

    function statusStarted() {
        if (isFinished())
            return;

        context.status = "STARTED";
        context.startTime = new Date();
        updateSubscribers();
    }

    function statusStopping() {
        if (isFinished())
            return;

        context.status = "STOPPING";
        updateSubscribers();
    }

    function statusStopped(exitCode) {
        if (isFinished())
            return;

        if (exitCode == null)
            exitCode = "STOPPED";

        context.status = "STOPPED";
        context.exitCode = exitCode;
        context.endTime = new Date();
        updateSubscribers();
    }

    /**
     * @param args array of args, either a result object,
     * or an exit code object and a result object.
     */
    function statusCompleted(...args) {
        if (isFinished())
            return;

        context.endTime = new Date();
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
        if (isFinished())
            return;

        context.endTime = new Date();
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
        starting: function () {
            statusStarting();
        },
        started: function () {
            statusStarted();
        },
        completed: function (...args) {
            statusCompleted(...args);
        },
        failed: function (...args) {
            statusFailed(...args);
        },
        stopping: function () {
            statusStopping();
        },
        stopped: function (...args) {
            statusStopped(...args)
        }
    };

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
        this.modalId = "taskModal" + Math.floor(Math.random() * 1000);

        this.styles = {
            jsonTextArea: {
                resize: "vertical",
                fontFamily: "Consolas, monospace"
            }
        };

        this.load = this.load.bind(this);
        this.cancel = this.cancel.bind(this);
        this.renderModal = this.renderModal.bind(this);
        this.renderModalReport = this.renderModalReport.bind(this);
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

        // pretend that anything longer than 3 seconds is a timeout
        setTimeout(() => {
            this.execution.events.stopped("TIMEOUT");
        }, 3000);
    }

    cancel() {
        this.doCancel(this.execution);
    }

    doCancel(execution) {
        execution.events.stopping();

        setTimeout(function () {
            execution.events.stopped("CANCELLED");
        }, 1000);
    }

    render() {
        return (
            <div>
                {this.renderButton()}
                <this.renderModal modalId={this.modalId}>
                    <this.renderModalReport/>
                </this.renderModal>
            </div>
        )
    }

    renderButton() {

        let modalId = "#" + this.modalId;

        let loaderGifStyles = {
            backgroundImage: "url(/resources/spinner.gif)",
            backgroundPosition: "center",
            backgroundSize: "contain",
            width: "2em",
            height: "2em",
            verticalAlign: "middle"
        };

        if (this.execution == null) {
            return (
                <div>
                    <input className="form-control" type="text" ref="ttl" placeholder="ttl"/>
                    <p/>
                    <button className="btn btn-primary" onClick={this.load}>
                        <span className="glyphicon glyphicon-play"/> Execute
                    </button>
                    <p/>
                </div>
            );
        }

        switch (this.execution.context.status) {
            case "STARTING":
                return (
                    <div>

                        <button className="btn btn-primary" onClick={this.load}>
                            <span className="glyphicon glyphicon-play"/> Execute
                        </button>
                        <p/>
                    </div>
                );
            case "STARTED":
                return (
                    <div>
                        <span className="glyphicon" style={loaderGifStyles}/><span>&nbsp;</span>
                        <button className="btn btn-danger" onClick={this.cancel}>
                            <span className="glyphicon glyphicon-stop"/> Cancel
                        </button>
                        <p/>
                    </div>
                );
            case "COMPLETED":
                return (
                    <div>
                        <input className="form-control" type="text" ref="ttl" placeholder="ttl"/>
                        <div className="btn-group">
                            <button className="btn btn-success" onClick={this.load}>
                                <span className="glyphicon glyphicon-ok"/> Execute Again
                            </button>
                            <button className="btn btn-default" data-toggle="modal" data-target={modalId}>
                                <span className="text-success glyphicon glyphicon-info-sign"/>
                            </button>
                        </div>
                        <p/>
                    </div>
                );
            case "FAILED":
                return (
                    <div>
                        <input className="form-control" type="text" ref="ttl" placeholder="ttl"/>
                        <div className="btn-group">
                            <button className="btn btn-danger" onClick={this.load}>
                                <span className="glyphicon glyphicon-remove"/> Execute Again
                            </button>
                            <button className="btn btn-default" data-toggle="modal" data-target={modalId}>
                                <span className="text-danger glyphicon glyphicon-info-sign"/>
                            </button>
                        </div>
                        <p/>
                    </div>
                );
            case "STOPPING":
                return (
                    <div>
                        <button className="btn btn-danger" data-toggle="modal" data-target={modalId}>
                            <span className="glyphicon glyphicon-stop"/> Stopping...
                        </button>
                        <p/>
                    </div>
                );
            case "STOPPED":
                if (this.execution.context.exitCode === "CANCELLED")
                    return (
                        <div>
                            <input className="form-control" type="text" ref="ttl" placeholder="ttl"/>
                            <div className="btn-group">
                                <button className="btn btn-danger" onClick={this.load}>
                                    <span className="glyphicon glyphicon-remove"/> Execute Again
                                </button>
                                <button className="btn btn-default" data-toggle="modal" data-target={modalId}>
                                    <span className="text-danger glyphicon glyphicon-info-sign"/>
                                </button>
                            </div>
                            <p/>
                        </div>
                    );
                else if (this.execution.context.exitCode === "TIMEOUT")
                    return (
                        <div>
                            <input className="form-control" type="text" ref="ttl" placeholder="ttl"/>
                            <div className="btn-group">
                                <button className="btn btn-danger" onClick={this.load}>
                                    <span className="glyphicon glyphicon-remove"/> Execute Again
                                </button>
                                <button className="btn btn-default" data-toggle="modal" data-target={modalId}>
                                    <span className="text-danger glyphicon glyphicon-info-sign"/>
                                </button>
                            </div>
                            <p/>
                        </div>
                    );
                else return this.renderReady();
        }
    }

    renderModalReport() {

        if (this.execution == null) {
            return (
                <div>
                    <span className="glyphicon glyphicon-exclamation-sign"/> Task has not been executed yet.
                </div>
            )
        }

        let durationMs = (this.execution.context.startTime && this.execution.context.endTime) ? this.execution.context.endTime - this.execution.context.startTime : "";

        let paramsJson = this.execution.context.params ? JSON.stringify(this.execution.context.params, 0, 2) : "";
        let paramsJsonRows = paramsJson.split(/\r\n|\r|\n/).length;
        paramsJsonRows = Math.min(paramsJsonRows, 11);

        return (
            <div className="row">
                <div className="container col-sm-12">
                    <form className="form-horizontal">

                        <div className="form-group">
                            <label className="control-label col-sm-2">Start Time</label>
                            <div className="col-sm-10">
                                <input
                                    type="text"
                                    className="form-control"
                                    disabled
                                    value={this.execution.context.startTime ? this.execution.context.startTime : ""}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="control-label col-sm-2">End Time</label>
                            <div className="col-sm-10">
                                <input
                                    type="text"
                                    className="form-control"
                                    disabled
                                    value={this.execution.context.endTime ? this.execution.context.endTime : ""}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="control-label col-sm-2">Duration (milli)</label>
                            <div className="col-sm-10">
                                <input
                                    type="text"
                                    className="form-control"
                                    disabled
                                    value={durationMs}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="control-label col-sm-2">Parameters</label>
                            <div className="col-sm-10">
                                <textarea
                                    type="text"
                                    style={this.styles.jsonTextArea}
                                    className="form-control"
                                    disabled
                                    value={paramsJson}
                                    rows={paramsJsonRows}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="control-label col-sm-2">Status</label>
                            <div className="col-sm-10">
                                <input
                                    type="text"
                                    className="form-control"
                                    disabled
                                    value={this.execution.context.status}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="control-label col-sm-2">Exit Code</label>
                            <div className="col-sm-10">
                                <input
                                    type="text"
                                    className="form-control"
                                    disabled
                                    value={this.execution.context.exitCode}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="control-label col-sm-2">Result</label>
                            <div className="col-sm-10">
                                <textarea
                                    type="text"
                                    style={this.styles.jsonTextArea}
                                    className="form-control"
                                    disabled
                                    value={this.execution.context.result ? this.execution.context.result : ""}
                                />
                            </div>
                        </div>

                    </form>
                </div>
            </div>
        );
    }

    renderModal(props) {
        return (
            <div id={props.modalId} className="modal fade" role="dialog">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <button type="button" className="close" data-dismiss="modal">&times;</button>
                            <h2 className="modal-title">
                                <span className="text-info glyphicon glyphicon-info-sign"/>
                                <span> Execution Info</span>
                            </h2>
                        </div>
                        <div className="modal-body">
                            {props.children}
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

    // renderReady() {
    //     return (
    //         <div>
    //             <div className="input-group">
    //                 <input type="text" className="form-control" ref="ttl" placeholder="ttl"/>
    //                 <span className="input-group-btn">
    //                     <button className="btn btn-info" onClick={this.load}>Go</button>
    //                 </span>
    //             </div>
    //             <p></p>
    //         </div>
    //     )
    // }

    // renderInProgress() {
    //     return (
    //         <div>
    //             <button className="btn btn-info" disabled>Go</button>
    //             <button className="btn btn-danger" onClick={this.cancel}>Cancel</button>
    //             button>
    //             <img className="showInProgress loader" src="/resources/loading.gif" alt="loading"/>
    //             <p></p>
    //         </div>
    //     )
    // }
    //
    // renderCompleted() {
    //     return (
    //         <div>
    //             <input type="text" ref="ttl"/>
    //             <button className="btn btn-info" onClick={this.load}>Go</button>
    //             <p></p>
    //             <div className="alert alert-success">Done. Result: {this.execution.context.result.toString()}</div>
    //         </div>
    //     )
    // }
    //
    // renderFailed() {
    //     return (
    //         <div>
    //             <input type="text" ref="ttl"/>
    //             <button className="btn btn-info" onClick={this.load}>Go</button>
    //             <p></p>
    //             <div
    //                 className="showOnErrorAlert alert alert-danger">Error: {this.execution.context.result.toString()}</div>
    //         </div>
    //     )
    // }
    //
    // renderCancelling() {
    //     return (
    //         <div>
    //             <p></p>
    //             <div className="showOnErrorAlert alert alert-danger">Attempting to cancel...</div>
    //         </div>
    //     )
    // }
    //
    // renderCancelled() {
    //     return (
    //         <div>
    //             <input type="text" ref="ttl"/>
    //             <button className="btn btn-info" onClick={this.load}>Go</button>
    //             <p></p>
    //             <div className="showOnErrorAlert alert alert-danger">Canceled.</div>
    //         </div>
    //     )
    // }
    //
    // renderTimedOut() {
    //     return (
    //         <div>
    //             <input type="text" ref="ttl"/>
    //             <button className="btn btn-info" onClick={this.load}>Go</button>
    //             <p></p>
    //             <div className="showOnErrorAlert alert alert-danger">Timed out.</div>
    //         </div>
    //     )
    // }
}

$(function () {
    ReactDOM.render(<Activity/>, $("#activity")[0]);
});