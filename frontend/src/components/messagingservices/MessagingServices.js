import React from 'react';
import './MessagingServices.scss';

class MessagingServices extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            messagingServices: []
        }
    }

    componentDidMount() {
        fetch("http://localhost:8080/messagingServices")
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
                            this.state.messagingServices.map(messagingService => {
                                return <tr>
                                    <td>{messagingService.service}</td>
                                    <td>{messagingService.url}</td>
                                </tr>
                            })
                        }
                    </tbody>
                </table>
            </div>
        )
    }
}

export default MessagingServices;