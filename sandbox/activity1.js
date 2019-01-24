


function Activity() {

    const taskTemplate = new TaskTemplate({
        workFunc: () => {
            for (let i = 0; i < 100, i++;) {
                console.log("asdf");
            }
        }
    });

    const wrapper = {
        params: {
            "stuff": "default stuff",
            "things": "some things",
            "more things": "lots of things"
        }
    };

    return (
        <div className="container">
            <div>Task Template Test</div>
            <TaskExecutorUi
                taskTemplate={taskTemplate}
                title="Task 1"
                message="This is the most wonderful task in the whole world."
                paramsWrapper={wrapper}
                paramProvider={<ParamsProvider wrapper={wrapper} />} />
        </div>
    )
}

const activity = document.getElementById("activity1");
ReactDOM.render(<Activity/>, activity);