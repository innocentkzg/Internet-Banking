import React, { useState } from "react";
import axios from "axios";

const VerifyOTP = () => {
  const [otpValues, setOTPValues] = useState(["", "", "", "", "", ""]);

  const handleInputChange = (index, value) => {
    const updatedOTPValues = [...otpValues];
    updatedOTPValues[index] = value;
    setOTPValues(updatedOTPValues);

    // Move to the next input box
    if (value.length === 1 && index < 5) {
      document.getElementById(`otp-input-${index + 1}`).focus();
    }
  };

  const handleSubmit = () => {
    const otp = otpValues.join("");
    // Call API endpoint with OTP
    console.log("OTP:", otp);
  };

  return (
    <div>
      <h2>Verify OTP</h2>
      <div>
        {otpValues.map((value, index) => (
          <input
            key={index}
            id={`otp-input-${index}`}
            type="text"
            maxLength="1"
            value={value}
            onChange={(e) => handleInputChange(index, e.target.value)}
          />
        ))}
      </div>
      <button onClick={handleSubmit}>Submit</button>
    </div>
  );
};

export default VerifyOTP;
