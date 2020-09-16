import React from 'react';
import './Alerts.scss';

const months = [ "January", "February", "March", "April", "May", "June", 
"July", "August", "September", "October", "November", "December" ]
const dayOfWeek = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]

class Alerts extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            alerts: []
        }
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
        switch(alert) {
            case 0: return "Score";
            case 1: return "Close Score";
            case 2: return "Standings";
            case 3: return "Matchup";
            default: return "N/A"; 
        }
    }

    mapToMonth(month) {
        if(month < 1 || month > 12) {
            return "N/A"
        }
        return months[month - 1]
    }

    mapToDayOfWeek(day) {
        if(day < 1 || day > 7) {
            return "N/A"
        }
        return dayOfWeek[day - 1]
    }

    formatMinute(minute) {
        if(minute < 10) {
            return `0${minute}`
        }
        return minute
    }

    render() {
        const columns = [
            "Type",
            "Hour",
            "Minute",
            "Start Month",
            "End Month",
            "Day Of Week",
            "Time Zone"
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
                            this.state.alerts.map(alert => {
                                return <tr>
                                    <td>{this.mapToAlertName(alert.type)}</td>
                                    <td>{alert.hour}</td>
                                    <td>{this.formatMinute(alert.minute)}</td>
                                    <td>{this.mapToMonth(alert.startMonth)}</td>
                                    <td>{this.mapToMonth(alert.endMonth)}</td>
                                    <td>{this.mapToDayOfWeek(alert.dayOfWeek)}</td>
                                    <td>{alert.timeZone}</td>
                                </tr>
                            })
                        }
                    </tbody>
                </table>
            </div>
        )
    }
}

export default Alerts;