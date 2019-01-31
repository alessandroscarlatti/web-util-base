class TaskTemplate {

    constructor({workFunc}) {
        this.canceled = false;
        this.workFunc = workFunc;
        this.execute = this.execute.bind(this);
        this._doExecute = this._doExecute.bind(this);
    }

    /**
     * @return Object (the return value of the execution)
     */
    execute(params, callbackInterface) {
        // setTimeout(this._doExecute.bind(this, params, callbackInterface), 0);
        this._doExecute(params, callbackInterface);
    }

    _doExecute(params, callbackInterface) {
        callbackInterface.started();
        try {
            let result = this.workFunc(params, {
                completed: callbackInterface.completed
            });
        } catch (e) {
            callbackInterface.failed(e);
        }
    }

    /**
     * Cancel this execution (if running).
     */
    cancel() {
        this.canceled = true;
    }
}

class TaskExecutorUi extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            taskState: "ready",
            exception: ""
        };

        this.started = this.started.bind(this);
        this.completed = this.completed.bind(this);
        this.failed = this.failed.bind(this);
        this.renderReady = this.renderReady.bind(this);
        this.renderInProgress = this.renderInProgress.bind(this);
        this.renderCompleted = this.renderCompleted.bind(this);
        this.renderFailed = this.renderFailed.bind(this);
        this.execute = this.execute.bind(this);
    }

    started() {
        this.setState({
            taskState: "inProgress"
        })
    }

    completed(result) {
        this.setState({
            taskState: "completed"
        })
    }

    failed(response) {
        console.log("failed", response);

        this.setState({
            taskState: "failed",
            exception: response.status
        })
    }

    execute() {
        console.log("params", this.props.paramsWrapper.params)
        this.props.taskTemplate.execute(this.props.params, this);
    }

    render() {

        let executorControls = this.renderExecutorControls();

        return (
            <div className="container">
                <div className="row">
                    <h3>{this.props.title}</h3>
                </div>
                <div className="row">
                    <p>{this.props.message}</p>
                </div>
                <div className="row">
                    {this.props.paramProvider}
                </div>
                {executorControls}
            </div>
        );
    }

    renderExecutorControls() {
        switch (this.state.taskState) {
            case "ready":
                return this.renderReady();
            case "inProgress":
                return this.renderInProgress();
            case "completed":
                return this.renderCompleted();
            case "failed":
                return this.renderFailed();
        }
    }

    renderReady() {
        return (
            <div className="row">
                <button className="btn btn-primary" onClick={this.execute}><span
                    className="fas fa-play"></span> Execute
                </button>
            </div>
        )
    }

    renderInProgress() {
        return (
            <div className="row">
                <button className="btn btn-primary" onClick={this.execute} disabled><span
                    className="fas fa-play"></span> Execute
                </button>
                <p></p>
                <p className="alert"><img style={{height: "33px"}} src="../src/main/resources/static/resources/spinner.gif"/>Working...</p>
            </div>
        )
    }

    renderCompleted() {
        return (
            <div className="row">
                <button className="btn btn-primary" onClick={this.execute}><span
                    className="fas fa-play"></span> Execute
                </button>
                <p></p>
                <p className="alert alert-success">Completed</p>
            </div>
        )
    }

    renderFailed() {
        return (
            <div className="row">
                <button className="btn btn-primary" onClick={this.execute}><span
                    className="fas fa-play"></span> Execute
                </button>
                <p></p>
                <p className="alert alert-danger">Error</p>
            </div>
        )
    }
}

window.TaskTemplate = TaskTemplate;
window.TaskExecutorUi = TaskExecutorUi;