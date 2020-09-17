import React from 'react'
import './MainArea.scss'
import Alerts from '../alerts/Alerts'

class MainArea extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            authenticated: false
        }
    }

    componentDidMount() {
        fetch("/authenticate")
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

    render() {
        return (
            <main id="main-area">
                <h1 id="dashboard-text">Dashboard</h1>
                <Alerts />
                {/* <Leagues/>
                <MessagingServices/>
                <MessageType/> */}
            </main>
        )
    }
}

export default MainArea