import React from 'react'
import './MessageType.scss'

class MessageType extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            typeDisplay: -1,
            type: null
        }

        this.setMessageType = this.setMessageType.bind(this)
    }

    componentDidMount() {
        fetch("/messageType")
            .then(res => res.json())
            .then((result) => {
                console.log(result)
                this.setState({
                    typeDisplay: result.type
                })
            },
                (error) => {
                    console.log("Error mate!")
                })
    }

    mapToMessageName(messageType) {
        switch(messageType) {
            case 0: return "Individual"
            case 1: return "Batch"
            default: return "N/A"
        }
    }

    handleMessageType = (event) => {
        this.setState({
            type: parseInt(event.target.value)
        })
    }

    setMessageType() {
        fetch("/messageType", {
            method: "PUT",
            body: JSON.stringify({
                type: this.state.type
            })
        })
        .then(res => res.json())
        .then((result) => {
            console.log(result)
            this.setState({
                typeDisplay: result.type
            })
        },
        (error) => {
            console.log("Error mate!")
        })
        console.log(this.state)
    }

    render() {
        return (
            <div id="alerts-area">
                <h2 className="area-header">Message Type</h2>
                <h2 id="message-type">{this.mapToMessageName(this.state.typeDisplay)}</h2>
                <div>
                    <div id="type-area" className="add-area">
                        <select name="type" onChange={this.handleMessageType} value={this.state.type}>
                            <option value="" disabled selected>Type</option>
                            {
                                Array.from(Array(2).keys()).map(num => {
                                    const val = this.mapToMessageName(num)
                                    return <option value={num}>{val}</option>
                                })
                            }
                        </select>
                    </div>
                    <div className="add-button-area">
                        <button id="add-button" type="button" onClick={this.setMessageType}>Set Type</button>
                    </div>
                </div>
            </div>
        )
    }
}

export default MessageType