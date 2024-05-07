import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import "./App.css";
import QRCodeDisplay from "./components/QRCodeDisplay";
import Landing from "./components/Landing";
import SignIn from "./components/SignIn";
import Register from "./components/Register";
import VerifyOTP from "./components/VerifyOTP";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route exact path="/" element={<Landing />} />
        <Route path="/signin" element={<SignIn />} />
        <Route path="/login" element={<SignIn />} />
        <Route path="/register" element={<Register />} />
        <Route path="/verify-otp" element={<VerifyOTP />} />
      </Routes>
    </BrowserRouter>

    // <div>
    //   <h1>Google Authenticator TOTP QR Code Generator</h1>
    //   <Landing></Landing>
    //   <QRCodeDisplay username="test" />
    // </div>
  );
}

export default App;
