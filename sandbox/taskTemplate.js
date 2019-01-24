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
        setTimeout(this._doExecute.bind(this, params, callbackInterface), 0);
    }

    _doExecute(params, callbackInterface) {

        callbackInterface.started();
        try {
            let result = this.workFunc(params);

            if (!this.canceled) {
                callbackInterface.completed(result);
            }
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

        this.completed = this.completed.bind(this);
        this.failed = this.failed.bind(this);
        this.renderReady = this.renderReady.bind(this);
        this.renderInProgress = this.renderInProgress.bind(this);
        this.renderCompleted = this.renderCompleted.bind(this);
        this.renderFailed = this.renderFailed.bind(this);
        this.execute = this.execute.bind(this);
        this.doExecute = this.doExecute.bind(this);
    }

    completed(result) {
        this.setState({
            taskState: "completed"
        })
    }

    failed(response) {
        this.setState({
            taskState: "failed",
            exception: response.status
        })
    }

    execute() {
        // this.setState({
        //     state: "inProgress"
        // }, this.doExecute);

        console.log("params", this.props.paramsWrapper.params)
    }

    doExecute() {
        this.props.taskTemplate.execute(this.props.params, this);
    }

    render() {
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
            <div class="container">
                <div className="row">
                    <h3>{this.props.title}</h3>
                </div>
                <div className="row">
                    <p>{this.props.message}</p>
                </div>
                <div className="row">
                    {this.props.paramProvider}
                </div>
                <div className="row">
                    <button className="btn btn-primary" onClick={this.execute}><span className="glyphicon glyphicon-play"></span> Execute</button>
                </div>
            </div>
        )
    }

    renderInProgress() {
        return (
            <div>In Progress</div>
        )
    }

    renderCompleted() {
        return (
            <div>
                <p>Completed</p>
                <button className="btn" onClick={this.execute}>Execute</button>
            </div>
        )
    }

    renderFailed() {
        return (
            <div>
                <p>Failed</p>
                <button className="btn" onClick={this.execute}>Execute</button>
            </div>
        )
    }
}

window.TaskTemplate = TaskTemplate;
window.TaskExecutorUi = TaskExecutorUi;