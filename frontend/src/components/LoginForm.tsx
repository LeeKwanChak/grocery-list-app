import React, { useState } from 'react';
import './Form.css';

  interface LoginFormProps{
    onSwitchToRegister: () => void;
  }

const LoginForm: React.FC<LoginFormProps> = ({onSwitchToRegister}) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
  }

  return (
    <div className="form-container">
      <h2>Sign in</h2>
      <p className = "subtitle">Sign in to continue</p>
      <form onSubmit={handleSubmit}>
        <div className ="form-group">
          <input
            type = "email"
            id = "email"
            value = {email}
            onChange = {(e) => setEmail(e.target.value)}
            placeholder = "Email"
            required
          />
        </div>
        <div className="form-group">
          <input
            type = "password"
            id = "password"
            value = {password}
            onChange = {(e) => setPassword(e.target.value)}
            placeholder = "Password"
            required
          />
        </div>
        <button type="submit">Sign in</button>
      </form>
      <p>
        Do not have an account?{' '}
        <span className="switch-link" onClick ={onSwitchToRegister}>
          Sign up
        </span>
      </p>
    </div>
  );
};

export default LoginForm;