import React from 'react'
import './MessagingServices.scss'
import { MdDeleteForever } from 'react-icons/md'

class MessagingServices extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            messagingServices: [],
            service: "",
            url: ""
        }

        this.addMessagingService = this.addMessagingService.bind(this)
    }

    componentDidMount() {
        fetch("/messagingServices")
            .then(res => res.json())
            .then((result) => {
                console.log(result)
                this.setState({
                    messagingServices: result
                })
            },
                (error) => {
                    console.log("Error mate!")
                })
    }

    handleService = (event) => {
        this.setState({
            service: event.target.value
        })
    }

    handleUrl = (event) => {
        this.setState({
            url: event.target.value
        })
    }

    mapToService(service){
        switch(service) {
            case 0: return "Discord"
            case 1: return "Slack"
            case 2: return "GroupMe"
            default: return "N/A"
        }
    }

    addMessagingService() {
        const messagingServices = [...this.state.messagingServices]
        messagingServices.push({
            service: this.state.service,
            url: this.state.url
        })
        console.log(messagingServices)

        fetch("/messagingServices", {
            method: "PUT",
            body: JSON.stringify(messagingServices)
        })
            .then(res => res.json())
            .then((result) => {
                console.log(result)
                this.setState({
                    messagingServices: result
                })
            },
                (error) => {
                    console.log("Error mate!")
                })
        console.log(this.state)
    }

    deleteMessagingService(event, index) {
        const messagingServices = [...this.state.messagingServices]
        messagingServices.splice(index, 1)
        console.log(messagingServices)

        fetch("/messagingServices", {
            method: "PUT",
            body: JSON.stringify(messagingServices)
        })
            .then(res => res.json())
            .then((result) => {
                console.log(result)
                this.setState({
                    messagingServices: result
                })
            },
                (error) => {
                    console.log("Error mate!")
                })
        console.log(this.state)
    }

    render() {
        const columns = [
            "Service",
            "Url"
        ]

        return (
            <div id="messagingservices-area">
                <h2 className="area-header">Messaging Services</h2>
                <table>
                    <thead>
                        <tr>
                            {
                                columns.map(column => {
                                    return <th>{column}</th>
                                })
                            }
                        </tr>
                    </thead>
                    <tbody>
                        {
                            this.state.messagingServices.map((messagingService, index) => {
                                return <tr>
                                    <td>{messagingService.service}</td>
                                    <td>{messagingService.url}</td>
                                    <td><button onClick={(event) => this.deleteMessagingService(event, index)}><MdDeleteForever /></button></td>
                                </tr>
                            })
                        }
                    </tbody>
                </table>
                <div>
                    <div className="add-area">
                        <select name="service" onChange={this.handleService} value={this.state.service}>
                            <option value="" disabled selected>Service</option>
                            {
                                Array.from(Array(3).keys()).map(num => {
                                    const val = this.mapToService(num)
                                    return <option value={val}>{val}</option>
                                })
                            }
                        </select>
                        <input placeholder="Url" onChange={this.handleUrl} value={this.state.url}></input>
                    </div>
                    <div className="add-button-area">
                        <button id="add-button" type="button" onClick={this.addMessagingService}>Add Service</button>
                    </div>
                </div>
            </div>
        )
    }
}

export default MessagingServices