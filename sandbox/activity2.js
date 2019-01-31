function Activity() {

    const paramsWrapper = {
        params: {
            name: "Phil",
            age: "2"
        }
    };

    console.log("params: ", paramsWrapper);

    return (
        <div>
            <div>ParamsProvider Test</div>
            <ParamsProvider wrapper={paramsWrapper}/>


            {/*see what an info button would look like*/}
            <h1>Ready</h1>
            <button className="btn btn-primary">
                <span className="fas fa-play"/> Execute
            </button>

            <h1>Started</h1>
            <button className="btn btn-danger">
                <span className="fas fa-stop"/> Cancel
            </button>
            <span> or </span>
            <button className="btn btn-primary" disabled>
                <span className="fas fa-play"/> Working...
            </button>

            <h1>Completed</h1>

            <h2>Success</h2>

            <h2>button group 2</h2>
            <div className="btn-group">
                <button className="btn btn-success">
                    <span className="fas fa-ok"/> Execute Again
                </button>
                <button className="btn btn-default">
                    <span className="text-success fas fa-info"/>
                </button>
            </div>

            <h2>Failure</h2>
            <div className="btn-group">
                <button className="btn btn-danger">
                    <span className="fas fa-remove"/> Execute Again
                </button>
                <button className="btn btn-default">
                    <span className="text-danger fas fa-info"/>
                </button>
            </div>

            <h2>Not Retryable</h2>
            <div className="btn-group">
                <button className="btn btn-success" disabled>
                    <span className="fas fa-ok"/> Completed
                </button>
                <button className="btn btn-default">
                    <span className="text-success fas fa-info"/>
                </button>
            </div>

            <h2>Not Retryable Failure</h2>
            <div className="btn-group">
                <button className="btn btn-danger" disabled>
                    <span className="fas fa-remove"/> Failed
                </button>
                <button className="btn btn-default">
                    <span className="text-danger fas fa-info"/>
                </button>
            </div>

        </div>
    )
}

const activity = document.getElementById("activity2");
ReactDOM.render(<Activity/>, activity);