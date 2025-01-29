import logo from './logo.svg';
import './App.css';
import ChatWindow from "./ChatWindow";
import {io} from "socket.io-client";
import React, {useState, useEffect} from "react";

const socket = io("http://localhost:12345");

function App() {
  const[messages, setMessages] = useState([]);
  const[input, setInput] = useState("");
  
  useEffect(() => {
    socket.on("message", (message) =>
    setMessages((prev) => [...prev, message]));
    


  return () => {
    socket.off("message");
  };
}, []);

  const sendMessage = () => {
    socket.emit("message", input);
    setMessages([...messages, input]);
    setInput("");
  }



  return (
    <div className="App">
      <header className="App-header">
        <h1> P2P Messaging App </h1>
      </header>
      <main>

      <div id = "chat-window">
        {messages.map((msg,index) => (
          <p key={index}>{msg}</p>
        ))}
      </div>

      <div id = "message-input">
        <input type="text" 
        placeholder = "Write a message" 
        value={input}
        onChange={(e) => setInput(e.target.value)}
        />
        <button oncLick={sendMessage}>Send</button>

      </div>


      </main>
    </div>
  );
}

export default App;
