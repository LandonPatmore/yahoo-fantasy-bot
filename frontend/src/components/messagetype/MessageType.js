import React from 'react';
import './MessageType.scss';

class MessageType extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            messageType: -1
        }
    }

    componentDidMount() {
        fetch("http://localhost:8080/messageType")
            .then(res => res.json())
            .then((result) => {
                console.log(result)
                this.setState({
                    messageType: result.type
                })
            },
                (error) => {
                    console.log("Error mate!")
                })
    }

    mapToMessageName() {
        switch(this.state.messageType) {
            case 0: return "Individual";
            case 1: return "Batch";
            default: return "N/A"
        }
    }

    render() {
        return (
            <div id="alerts-area">
                <h2 className="area-header">Message Type</h2>
                <h2 id="message-type">{this.mapToMessageName()}</h2>
            </div>
        )
    }
}

export default MessageType;