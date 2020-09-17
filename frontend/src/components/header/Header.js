import React from 'react'
import GitHubButton from 'react-github-btn'
import './Header.scss'

class Header extends React.Component {
    constructor(props) {
        super(props)

        this.state ={
            version: ""
        }
    }

    componentDidMount() {
        fetch("/releaseInformation")
        .then(res => res.json())
        .then((result) => {
            this.setState({
                version: result.currentVersion
            })
        },
        (error) => {
            console.log("Error mate!")
        })
    }

    render() {
        return(
            <header>
                <div id="header-information">
                    <h2>Yahoo Fantasy Bot</h2>
                    <p>v{this.state.version}</p>
                </div>
                <div id="header-github">
                    <GitHubButton href="https://github.com/LandonPatmore" data-color-scheme="no-preference: dark; light: dark; dark: dark;" aria-label="Follow @LandonPatmore on GitHub">Follow @LandonPatmore</GitHubButton>
                    <GitHubButton href="https://github.com/LandonPatmore/yahoo-fantasy-bot" data-color-scheme="no-preference: dark; light: dark; dark: dark;" data-icon="octicon-star" data-show-count="true" aria-label="Star LandonPatmore/yahoo-fantasy-bot on GitHub">Star</GitHubButton>
                    <GitHubButton href="https://github.com/LandonPatmore/yahoo-fantasy-bot/fork" data-color-scheme="no-preference: dark; light: dark; dark: dark;" data-icon="octicon-repo-forked" data-show-count="true" aria-label="Fork LandonPatmore/yahoo-fantasy-bot on GitHub">Fork</GitHubButton>
                    <GitHubButton href="https://github.com/LandonPatmore/yahoo-fantasy-bot/issues" data-color-scheme="no-preference: dark; light: dark; dark: dark;" data-icon="octicon-issue-opened" aria-label="Issue LandonPatmore/yahoo-fantasy-bot on GitHub">Issue</GitHubButton>
                </div>
            </header>
        )
    }
}

export default Header