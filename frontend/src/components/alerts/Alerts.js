import React from 'react'
import './Alerts.scss'
import { MdDeleteForever } from 'react-icons/md'

const months = ["January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"]
const dayOfWeek = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]

class Alerts extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            alerts: [],
            addAlert: {
                type: null,
                hour: null,
                minute: null,
                startMonth: null,
                endMonth: null,
                dayOfWeek: null,
            //    timeZone: ""
            }
        }

        this.addAlert = this.addAlert.bind(this)
    }

    componentDidMount() {
        fetch("http://localhost:8080/alerts")
            .then(res => res.json())
            .then((result) => {
                console.log(result)
                this.setState({
                    alerts: result
                })
            },
                (error) => {
                    console.log("Error mate!")
                })
    }

    mapToAlertName(alert) {
        switch (alert) {
            case 0: return "Score"
            case 1: return "Close Score"
            case 2: return "Standings"
            case 3: return "Matchup"
            default: return "N/A"
        }
    }

    mapToMonth(month) {
        if (month < 1 || month > 12) {
            return "N/A"
        }
        return months[month - 1]
    }

    mapToDayOfWeek(day) {
        if (day < 1 || day > 7) {
            return "N/A"
        }
        return dayOfWeek[day - 1]
    }

    formatMinute(minute) {
        if (minute < 10) {
            return `0${minute}`
        }
        return minute
    }

    handleTypeChange = (event) => {
        this.setState({
            addAlert: {
                type: parseInt(event.target.value)
            }
        })
    }

    handleHourChange = (event) => {
        this.setState({
            addAlert: {
                ...this.state.addAlert,
                hour: parseInt(event.target.value)
            }
        })
    }

    handleMinuteChange = (event) => {
        this.setState({
            addAlert: {
                ...this.state.addAlert,
                minute: parseInt(event.target.value)
            }
        })
    }

    handleStartMonth = (event) => {
        this.setState({
            addAlert: {
                ...this.state.addAlert,
                startMonth: parseInt(event.target.value)
            }
        })
    }

    handleEndMonth = (event) => {
        this.setState({
            addAlert: {
                ...this.state.addAlert,
                endMonth: parseInt(event.target.value)
            }
        })
    }

    handleDayOfWeek = (event) => {
        this.setState({
            addAlert: {
                ...this.state.addAlert,
                dayOfWeek: parseInt(event.target.value)
            }
        })
    }

//    handleTimeZone = (event) => {
//        this.setState({
//            addAlert: {
//                ...this.state.addAlert,
//                timeZone: event.target.value
//            }
//        })
//    }

    addAlert() {
        const alerts = [...this.state.alerts]
        alerts.push(this.state.addAlert)
        console.log(alerts)

        fetch("http://localhost:8080/alerts", {
            method: "PUT",
            body: JSON.stringify(alerts)
        })
        .then(res => res.json())
        .then((result) => {
            console.log(result)
            this.setState({
                alerts: result
            })
        },
        (error) => {
            console.log("Error mate!")
        })
        console.log(this.state)
    }

    deleteAlert(event, index) {
        const alerts = [...this.state.alerts]
        alerts.splice(index, 1)
        console.log(alerts)

        fetch("http://localhost:8080/alerts", {
            method: "PUT",
            body: JSON.stringify(alerts)
        })
        .then(res => res.json())
        .then((result) => {
            console.log(result)
            this.setState({
                alerts: result
            })
        },
        (error) => {
            console.log("Error mate!")
        })
        console.log(this.state)
    }

    render() {
        const columns = [
            "Type",
            "Hour",
            "Minute",
            "Start Month",
            "End Month",
            "Day Of Week"
//            "Time Zone"
        ]

        // TODO: 12/24hr time conversion

        return (
            <div id="alerts-area">
                <h2 className="area-header">Alerts</h2>
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
                            this.state.alerts.map((alert, index) => {
                                return <tr>
                                    <td>{this.mapToAlertName(alert.type)}</td>
                                    <td>{alert.hour}</td>
                                    <td>{this.formatMinute(alert.minute)}</td>
                                    <td>{this.mapToMonth(alert.startMonth)}</td>
                                    <td>{this.mapToMonth(alert.endMonth)}</td>
                                    <td>{this.mapToDayOfWeek(alert.dayOfWeek)}</td>
                                    {/* <td>{alert.timeZone}</td> */}
                                    <td><button onClick={(event) => this.deleteAlert(event, index)}><MdDeleteForever/></button></td>
                                </tr>
                            })
                        }
                    </tbody>
                </table>
                <div>
                    <div className="add-area">
                        <select name="type" onChange={this.handleTypeChange} value={this.state.type}>
                            <option value="" disabled selected>Type</option>
                            {
                                Array.from(Array(4).keys()).map(num => {
                                    return <option value={num + 1}>{this.mapToAlertName(num)}</option>
                                })
                            }
                        </select>
                        <select name="hour" onChange={this.handleHourChange} value={this.state.hour}>
                            <option value="" disabled selected>Hour</option>
                            {
                                Array.from(Array(24).keys()).map(num => {
                                    return <option value={num}>{num}</option>
                                })
                            }
                        </select>
                        <select name="minute" onChange={this.handleMinuteChange} value={this.state.minute}>
                            <option value="" disabled selected>Minute</option>
                            {
                                Array.from(Array(60).keys()).map(num => {
                                    return <option value={num}>{num}</option>
                                })
                            }
                        </select>
                        <select name="startMonth" onChange={this.handleStartMonth} value={this.state.startMonth}>
                            <option value="" disabled selected>Start Month</option>
                            {
                                Array.from(Array(12).keys()).map(num => {
                                    return <option value={num + 1}>{this.mapToMonth(num + 1)}</option>
                                })
                            }
                        </select>
                        <select name="endMonth" onChange={this.handleEndMonth} value={this.state.endMonth}>
                            <option value="" disabled selected>End Month</option>
                            {
                                Array.from(Array(12).keys()).map(num => {
                                    return <option value={num + 1}>{this.mapToMonth(num + 1)}</option>
                                })
                            }
                        </select>
                        <select name="dayOfWeek" onChange={this.handleDayOfWeek} value={this.state.dayOfWeek}>
                            <option value="" disabled selected>Day Of Week</option>
                            {
                                Array.from(Array(7).keys()).map(num => {
                                    return <option value={num + 1}>{this.mapToDayOfWeek(num + 1)}</option>
                                })
                            }
                        </select>
                        {/* <input placeholder="Time Zone" onChange={this.handleTimeZone} value={this.state.timeZone}></input> */}
                    </div>
                    <div className="add-button-area">
                        <button id="add-alert-button" type="button" onClick={this.addAlert}>Add Alert</button>
                    </div>
                </div>
            </div>
        )
    }
}

export default Alerts