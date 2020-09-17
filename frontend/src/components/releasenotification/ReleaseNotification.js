import React from 'react'
import './ReleaseNotification.scss'
import Modal from 'react-modal'

class ReleaseNotification extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            releaseInformation: {},
            showModal: false
        }

        this.showNotification = this.showNotification.bind(this)
        this.handleOpenModal = this.handleOpenModal.bind(this)
        this.handleCloseModal = this.handleCloseModal.bind(this)
    }

    componentDidMount() {
        fetch("/releaseInformation")
            .then(res => res.json())
            .then((result) => {
                this.setState({
                    releaseInformation: result
                })
            },
                (error) => {
                    console.log(error)
                })
    }

    showNotification() {
        if (this.state.releaseInformation.upgrade) {
            return <React.Fragment>
                <h3>v{this.state.releaseInformation.latestVersion} has been released!</h3>
                <button onClick={this.handleOpenModal}>Show Changelog</button>
            </React.Fragment>
        }
    }

    handleOpenModal() {
        this.setState({ showModal: true })
    }

    handleCloseModal() {
        this.setState({ showModal: false })
    }

    handleOpenRepo() {
        window.open('http://github.com/LandonPatmore/yahoo-fantasy-bot','_blank')
    }

    render() {
        return (
            <div id="release-notification">
                {this.showNotification()}
                <Modal
                    isOpen={this.state.showModal}
                    contentLabel="Minimal Modal Example"
                >
                    <div id="release-notification-modal">
                        <h1>Changelog</h1>
                        <h3>{this.state.releaseInformation.changelog}</h3>
                        <button onClick={this.handleCloseModal}>Close Changelog</button>
                        <button onClick={this.handleOpenRepo}>Get the latest version!</button>
                    </div>
                </Modal>
            </div>
        )
    }
}

export default ReleaseNotification