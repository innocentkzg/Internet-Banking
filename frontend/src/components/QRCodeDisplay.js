import React, { useState, useEffect } from "react";
import QRCode from "qrcode.react";
import axios from "axios";

const QRCodeDisplay = ({ username }) => {
  const [secretKey, setSecretKey] = useState("");

  useEffect(() => {
    const generateSecretKey = async () => {
      try {
        const response = await axios.post(
          "http://localhost:8090/api/auth/generate",
          {
            username: username,
          }
        );
        setSecretKey(response.data);
      } catch (error) {
        console.error("Error generating secret key:", error);
      }
    };
    generateSecretKey();
  }, []);

  return (
    <div>
      <h2>QR Code:</h2>
      <QRCode
        value={`otpauth://totp/YourApp:${username}?secret=${secretKey}`}
      />
    </div>
  );
};

export default QRCodeDisplay;
