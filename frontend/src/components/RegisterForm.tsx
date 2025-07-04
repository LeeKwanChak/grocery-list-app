import React, { useState } from 'react';
import './Form.css';


interface RegisterFormProps{
    onSwitchToLogin: () => void;
}


const RegisterForm: React.FC<RegisterFormProps> = ({onSwitchToLogin}) => {
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault()
        if(password !== confirmPassword){
            setError('Passwords do not match')
            return;
        }
        setError('');
    }

    return(
        <div className = "form-container">
            <h2>Sign up</h2>
            <p className="subtitle">Sign up to continue</p>
            <form onSubmit={handleSubmit}>
                <div className="form-group"> 
                    <input
                        type = "text"
                        id = "name"
                        value = {name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder= "Username"
                        required />
                </div>

                <div className="form-group"> 
                    <input
                        type = "email"
                        id = "email"
                        value = {email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder= "Email"
                        required />
                </div>

                <div className="form-group">
                <input
                    type="password"
                    id="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Password"
                    required
                />
                </div>

                <div className="form-group">
                    <input
                        type="password"
                        id="confirmPassword"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        placeholder="Confirm Password"
                        required
                    />
                </div>
                {error && <p className="error-message">{error}</p>}
                <button type="submit">Sign up</button>
            </form>
            <p>
                Already have an account?{' '}
                <span className = "switch-link" onClick= {onSwitchToLogin}>
                Sign in
                </span>
            </p>
        </div>
    );

}

export default RegisterForm;