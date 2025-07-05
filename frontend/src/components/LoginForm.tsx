import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Form.css';

  interface LoginFormProps{
    onSwitchToRegister: () => void;
  }

const LoginForm: React.FC<LoginFormProps> = ({onSwitchToRegister}) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try{
      const response = await fetch('http://localhost:8080/auth/login',{
        method: 'POST',
        headers: {
          'Content-Type' : 'application/json',
        },
        body: JSON.stringify({ email, password }),
    });
    if(response.ok){
      const data = await response.json();
      console.log('Login successful:' , data);
      alert('Login successful!');
      localStorage.setItem('token', data.token)
      console.log('JWT Token saved:', data.token);
      navigate('/grocery-list');
      
    }else{

      const errorData = await response.json();
      console.error('Login failed:', errorData);
      if (response.status === 401) {
          setError(errorData.message || 'Invalid email or password.');
      } else {
          setError(errorData.message || 'An unexpected error occurred during login.');
      }
    }}
    catch(err){
      console.error('Network error or unexpected issue:', err);
      setError('Network error or server is unreachable.');

    } finally{
      setLoading(false);
    }
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
        {error && <p className="error-message">{error}</p>} 

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