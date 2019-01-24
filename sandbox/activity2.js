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
        </div>
    )
}

const activity = document.getElementById("activity2");
ReactDOM.render(<Activity/>, activity);