import React, { useState } from 'react';
import './Form.css';


interface RegisterFormProps{
    onSwitchToLogin: () => void;
}


const RegisterForm: React.FC<RegisterFormProps> = ({onSwitchToLogin}) => {
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const handleSubmit = async  (e: React.FormEvent) => {
        e.preventDefault()
        setError(null);
        setLoading(true);
        setSuccessMessage(null);
        if(password !== confirmPassword){
            setError('Passwords do not match')
            setLoading(false);
            return;
        }
        setError('');
    

    try{
        const response = await fetch('http://localhost:8080/auth/register',{
            method :'Post',
            headers : {
                'Content-Type' : 'application/json',
            },
            body: JSON.stringify({username, email, password }),
        }   
        )
        if(response.ok){
            const data = await response.json();
            console.log('Registration successful!')
            alert('Registration successful!')
            onSwitchToLogin();
        }else{
            const errorData = await response.json();
            console.error('Registration failed:', errorData);
            setError(errorData.message || 'An unexpected error occurred.')
        }
    } catch(err) {
        console.error('Network error or unexpected issue:', err);
        setError('Network error or server is unreachable.');
    } finally {
      setLoading(false);
    }
    }

    return(
        <div className = "form-container">
            <h2>Sign up</h2>
            <p className="subtitle">Sign up to continue</p>
            <form onSubmit={handleSubmit}>
                <div className="form-group"> 
                    <input
                        type = "text"
                        id = "username"
                        value = {username}
                        onChange={(e) => setUsername(e.target.value)}
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