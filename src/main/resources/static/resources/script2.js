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
        this.state = {
            state: "ready",
            exception: ""
        };

        this.load = this.load.bind(this);
        this.completed = this.completed.bind(this);
        this.failed = this.failed.bind(this);
        this.renderReady = this.renderReady.bind(this);
        this.renderInProgress = this.renderInProgress.bind(this);
        this.renderCompleted = this.renderCompleted.bind(this);
        this.renderFailed = this.renderFailed.bind(this);
        this.doLoad = this.doLoad.bind(this);
    }

    completed() {
        this.setState({
            state: "completed"
        })
    }

    failed(response) {
        this.setState({
            state: "failed",
            exception: response.status
        })
    }

    load() {
        this.setState({
            state: "inProgress"
        }, this.doLoad);
    }

    doLoad() {
        let comp = this;
        var csrfParameterName = $("meta[name=_csrf]").attr("content");
        var csrfParameterValue = $("meta[name=_csrf_parameter]").attr("content");
        console.debug("making request.");

        $.post("/activities/basicActivity/task1", {
            ttl: "1000",
            [csrfParameterName]: csrfParameterValue
        }).done(function (data, status, xhr) {
            comp.completed();
            console.debug("response", data);
            console.debug("status", status);
            console.debug("xhr", xhr);
        }).fail(function (response) {
            console.debug("response", response);
            console.debug("HTTP " + response.status);
            comp.failed(response);
        });
    }

    render() {
        switch (this.state.state) {
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
            <div>
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
                <button className="btn btn-info" onClick={this.load}>Go</button>
                <p></p>
                <div className="alert alert-success">Done.</div>
            </div>
        )
    }

    renderFailed() {
        return (
            <div>
                <button className="btn btn-info" onClick={this.load}>Go</button>
                <p></p>
                <div className="showOnErrorAlert alert alert-danger">Error.</div>
            </div>
        )
    }
}

$(function () {
    ReactDOM.render(<Activity/>, $("#activity")[0]);
});