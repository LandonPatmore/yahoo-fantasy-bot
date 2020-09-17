import React from 'react'
import './MainArea.scss'
import Alerts from '../alerts/Alerts'
import Authenticate from '../authenticate/Authenticate'

class MainArea extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            authenticated: false
        }

        this.showAlerts = this.showAlerts.bind(this)
    }

    componentDidMount() {
        fetch("/auth")
            .then(res => res.json())
            .then((result) => {
                console.log(result)
                this.setState({
                    authenticated: result.authenticated
                })
            },
                (error) => {
                    console.log("Error mate!")
                })
    }

    showAlerts() {
        if (this.state.authenticated) {
            return <div id="main-area">
                <h1 id="dashboard-text">Dashboard</h1>
                <Alerts/>
                {/* <Leagues/>
                <MessagingServices/>
                <MessageType/> */}
            </div>
        } else {
            return <Authenticate />
        }
    }

    render() {
        return (
            <main>
                {this.showAlerts()}
            </main>
        )
    }
}

export default MainArea