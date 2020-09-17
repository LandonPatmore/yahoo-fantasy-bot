import React from 'react'
import './Authenticate.scss'

class Authenticate extends React.Component {
    render() {
        return(
            <div>
                <div id="auth-area">
                    <a href="/authenticate"><button>Authenticate</button></a>
                </div>
            </div>
        )
    }
}

export default Authenticate