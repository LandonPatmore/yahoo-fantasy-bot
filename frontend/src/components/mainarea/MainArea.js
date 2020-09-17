import React from 'react'
import './MainArea.scss'
import Alerts from '../alerts/Alerts'
import Leagues from '../leagues/Leagues'
import MessagingServices from '../messagingservices/MessagingServices'
import MessageType from '../messagetype/MessageType'

class MainArea extends React.Component {
    constructor(props) {
        super(props)
    }

    render() {
        return(
            <main id="main-area">
                <h1 id="dashboard-text">Dashboard</h1>
                <Alerts/>
                <Leagues/>
                <MessagingServices/>
                <MessageType/>
            </main>
        )
    }
}

export default MainArea