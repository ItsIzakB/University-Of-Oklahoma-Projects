import React from "react"

function ChatWindow({messages}){
    return(
        <div id="chat-window">
           {messages.map((msg, index) => (
           <p key = {index}>{msg}</p>
           ))} 
        </div>
    );


}

export default ChatWindow