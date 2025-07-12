import React, { useState } from 'react';
import './Form.css';
import { setToken } from '../utils/auth';
import api from '../utils/api';

  interface LoginFormProps{
    onLoginSuccess: (token: string) => void
    onSwitchToRegister: () => void;
  }

const LoginForm: React.FC<LoginFormProps> = ({onSwitchToRegister, onLoginSuccess}) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try{
      const response = await api.post('/auth/login',{
        email, password
      })
      const data = response.data;
      console.log('Login successful:' , data);
      alert('Login successful!');
      setToken(data.token);
      console.log('JWT Token saved:', data.token);
      onLoginSuccess(data.token);
    }catch(err:any){
      console.error('Login failed:');
      if(err.response && err.response.status === 401){
        setError('Invalid email or password.')
      }else if(err.response){
        setError(err.response.data.message)
      }
      else if(err.request){
        setError('Network error or server is unreachable.');
      }else{
        setError('An unexpected error occurred.');
      }
    } finally{
      setLoading(false)
    }
  }



  return (
    <div className="form-container">
      <h2>My Grocery List</h2>
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
        {error && <p className="error-message">{error}</p>} 

        <button type="submit" disabled={loading}>Sign in</button>
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