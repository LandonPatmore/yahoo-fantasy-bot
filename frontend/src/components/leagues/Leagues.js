import React from 'react'
import './Leagues.scss'
import { MdDeleteForever } from 'react-icons/md'

class Leagues extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            leagues: [],
            leagueId: "",
            gameKey: ""
        }

        this.addLeague = this.addLeague.bind(this)
    }

    componentDidMount() {
        fetch("/leagues")
            .then(res => res.json())
            .then((result) => {
                console.log(result)
                this.setState({
                    leagues: result
                })
            },
                (error) => {
                    console.log("Error mate!")
                })
    }

    mapToGameKey(gameKey) {
        switch (gameKey) {
            case 0: return "NFL"
            case 1: return "MLB"
            case 2: return "NBA"
            default: return "N/A"
        }
    }

    handleLeagueId = (event) => {
        this.setState({
            leagueId: event.target.value
        })
    }

    handleGameKey = (event) => {
        this.setState({
            gameKey: event.target.value
        })
    }

    addLeague() {
        const leagues = [...this.state.leagues]
        leagues.push({
            leagueId: this.state.leagueId,
            gameKey: this.state.gameKey
        })
        console.log(leagues)

        fetch("/leagues", {
            method: "PUT",
            body: JSON.stringify(leagues)
        })
        .then(res => res.json())
        .then((result) => {
            console.log(result)
            this.setState({
                leagues: result
            })
        },
        (error) => {
            console.log("Error mate!")
        })
        console.log(this.state)
    }

    deleteLeague(event, index) {
        const leagues = [...this.state.leagues]
        leagues.splice(index, 1)
        console.log(leagues)

        fetch("/leagues", {
            method: "PUT",
            body: JSON.stringify(leagues)
        })
        .then(res => res.json())
        .then((result) => {
            console.log(result)
            this.setState({
                leagues: result
            })
        },
        (error) => {
            console.log("Error mate!")
        })
        console.log(this.state)
    }

    render() {
        const columns = [
            "League ID",
            "Game Key"
        ]

        return (
            <div id="leagues-area">
                <h2 className="area-header">Leagues</h2>
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
                            this.state.leagues.map((league, index) => {
                                return <tr>
                                    <td>{league.leagueId}</td>
                                    <td>{league.gameKey}</td>
                                    <td><button onClick={(event) => this.deleteLeague(event, index)}><MdDeleteForever /></button></td>
                                </tr>
                            })
                        }
                    </tbody>
                </table>
                <div>
                    <div className="add-area">
                        <input placeholder="League ID" onChange={this.handleLeagueId} value={this.state.leagueId}></input>
                        <select name="gameKey" onChange={this.handleGameKey} value={this.state.gameKey}>
                            <option value="" disabled selected>Game Key</option>
                            {
                                Array.from(Array(3).keys()).map(num => {
                                    const val = this.mapToGameKey(num)
                                    return <option value={val}>{val}</option>
                                })
                            }
                        </select>
                    </div>
                    <div className="add-button-area">
                        <button id="add-button" type="button" onClick={this.addLeague}>Add League</button>
                    </div>
                </div>
            </div>
        )
    }
}

export default Leagues