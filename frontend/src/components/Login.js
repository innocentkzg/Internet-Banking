import React, { useState } from "react";
import axios from "axios";

const Login = () => {
  const [state, setState] = React.useState({
    username: "",
    password: "",
  });
  const handleChange = (evt) => {
    const value = evt.target.value;
    setState({
      ...state,
      [evt.target.name]: value,
    });
  };

  const handleOnSubmit = async (evt) => {
    evt.preventDefault();
    const { username, password } = state;
    try {
      const response = await axios.post(
        "http://localhost:8090/api/auth/login",
        {
          username,
          password,
        }
      );
      console.log(response.data); // Handle registration success
    } catch (error) {
      console.error("Login failed:", error); // Handle registration failure
    }

    for (const key in state) {
      setState({
        ...state,
        [key]: "",
      });
    }
  };

  return (
    <div className="form-container sign-in-container">
      <form onSubmit={handleOnSubmit}>
        <h1>Login</h1>
        <input
          type="text"
          placeholder="Username"
          name="username"
          value={state.username}
          onChange={handleChange}
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          value={state.password}
          onChange={handleChange}
        />
        <a href="#">Forgot your password?</a>
        <button>Login</button>
      </form>
    </div>
  );
};

export default Login;
