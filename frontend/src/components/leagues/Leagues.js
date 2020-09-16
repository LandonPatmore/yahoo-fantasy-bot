import React from 'react';
import './Leagues.scss';

class Leagues extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            leagues: []
        }
    }

    componentDidMount() {
        fetch("http://localhost:8080/leagues")
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
                            this.state.leagues.map(league => {
                                return <tr>
                                    <td>{league.leagueId}</td>
                                    <td>{league.gameKey}</td>
                                </tr>
                            })
                        }
                    </tbody>
                </table>
            </div>
        )
    }
}

export default Leagues;