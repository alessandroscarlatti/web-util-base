class ParamsProvider extends React.Component {
    /**
     * @param props, contains wrapper for props example object.
     */
    constructor(props) {
        super(props);

        this.updaterFuncs = {};
        this.styles = {
            jsonTextArea: {
                resize: "vertical",
                fontFamily: "Consolas, monospace"
            }
        };

        this.state = {
            params: {...this.props.wrapper.params},
            view: "form"
        };

        // create the updater funcs
        for (let key in props.wrapper.params) {
            if (!props.wrapper.params.hasOwnProperty(key))
                continue;

            // this is the method that the rendered input control
            // will call when it is updated.
            this.updaterFuncs[key] = (e) => {
                this.props.wrapper.params[key] = e.target.value;
                let newParams = {...this.state.params, [key]: e.target.value};
                this.setState({
                    params: newParams
                });
            }
        }

        this.switchToFormView = this.switchToFormView.bind(this);
        this.switchToJsonView = this.switchToJsonView.bind(this);
        this.saveJson = this.saveJson.bind(this);
    }

    saveJson(e) {
        try {
            let jsonParams = JSON.parse(e.target.value);
            this.props.wrapper.params = jsonParams;
            this.setState({
                params: jsonParams
            })
        } catch (e) {
            // ignore this error.
        }
    }

    switchToJsonView() {
        this.setState({
            view: "json"
        });
    }

    switchToFormView() {
        this.setState({
            view: "form"
        });
    }

    render() {
        switch (this.state.view) {
            case "form":
                return this.renderFormView();
            case "json":
                return this.renderJsonView();
        }
    }

    renderFormView() {
        let inputs = [];

        for (let key in this.state.params) {
            if (!this.state.params.hasOwnProperty(key))
                continue;

            inputs.push(
                <div className="form-group" key={key}>
                    <label className="control-label col-sm-2">{key}</label>
                    <div className="col-sm-10">
                        <input
                            type="text"
                            className="form-control"
                            placeholder={key}
                            value={this.state.params[key]}
                            onChange={this.updaterFuncs[key]}
                        />
                    </div>
                </div>
            )
        }

        return (
            <div className="container">
                <form className="form-horizontal">
                    <div className="col-sm-2"></div>
                    <div className="col-sm-10">
                        <ul className="list-inline">
                            <li><a href="#" onClick={this.switchToJsonView}>
                                <span className="glyphicon glyphicon-pencil"></span> Edit JSON
                            </a></li>
                        </ul>
                    </div>
                    {inputs}
                </form>
            </div>
        )
    }

    renderJsonView() {

        let json = JSON.stringify(this.state.params, 0, 2);
        let rows = json.split(/\r\n|\r|\n/).length;

        return (
            <div className="container">
                <form className="form-horizontal">
                    <div className="col-sm-2"></div>
                    <div className="col-sm-10">
                        <ul className="list-inline">
                            <li><a href="#" onClick={this.switchToFormView}>
                                <span className="glyphicon glyphicon-pencil"></span> Edit Form
                            </a></li>
                        </ul>
                    </div>
                    <div className="form-group">
                        <div className="col-sm-2">
                            {/*<h4 className="pull-right glyphicon glyphicon-pencil"></h4>*/}
                        </div>
                        <div className="col-sm-10">
                            <textarea
                                style={this.styles.jsonTextArea}
                                className="form-control autoExpand"
                                onBlur={this.saveJson}
                                rows={rows}
                                defaultValue={json} />
                        </div>
                    </div>

                </form>
            </div>
        )
    }
}

window.ParamsProvider = ParamsProvider;