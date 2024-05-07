import React, { useState } from "react";
import axios from "axios";

const Register = () => {
  const [state, setState] = React.useState({
    name: "",
    username: "",
    email: "",
    password: "",
  });
  const handleChange = (evt) => {
    const value = evt.target.value;
    setState({
      ...state,
      [evt.target.name]: value,
    });
  };

  const handleOnSubmit = (evt) => {
    evt.preventDefault();

    const { name, username, email, password } = state;
    alert(
      `You are sign up with name: ${name} email: ${email} and password: ${password}`
    );

    for (const key in state) {
      setState({
        ...state,
        [key]: "",
      });
    }
  };

  return (
    <div className="form-container sign-up-container">
      <form onSubmit={handleOnSubmit}>
        <h1>Create Account</h1>
        <input
          type="text"
          name="name"
          value={state.name}
          onChange={handleChange}
          placeholder="Name"
        />
        <input
          type="text"
          name="username"
          value={state.username}
          onChange={handleChange}
          placeholder="Username"
        />
        <input
          type="email"
          name="email"
          value={state.email}
          onChange={handleChange}
          placeholder="Email"
        />
        <input
          type="number"
          name="phoneNum"
          value={state.phoneNum}
          onChange={handleChange}
          placeholder="Phone number"
        />
        <input
          type="password"
          name="password"
          value={state.password}
          onChange={handleChange}
          placeholder="Enter password"
        />
        <input
          type="password"
          name="passwordConfirm"
          value={state.passwordConfirm}
          onChange={handleChange}
          placeholder="Confirm password"
        />
        <button>Register</button>
      </form>
    </div>
  );
};

export default Register;
