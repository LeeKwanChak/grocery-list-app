import React , {useState} from 'react';
import LoginForm from '../components/LoginForm';
import RegisterForm from '../components/RegisterForm';
import './AuthPage.css';
import { useNavigate } from 'react-router-dom';

interface AuthPagePropos{
  onLoginSuccess: (jwt_token: string) => void
}

const AuthPage: React.FC<AuthPagePropos> = ({onLoginSuccess}) => {
  const navigate = useNavigate();
  const [isLoginView, setIsLoginView] = useState(true);
  const handleSwitchToRegister = () => {
    setIsLoginView(false);
  };

  const handleSwitchToLogin = () => {
    setIsLoginView(true);
  };

  return (
    <div className="auth-container">

      {isLoginView ? (
        <LoginForm 
        onSwitchToRegister = {handleSwitchToRegister}
        onLoginSuccess = {onLoginSuccess}/>
      ) : (
        <RegisterForm onSwitchToLogin = {handleSwitchToLogin}/>
      )}
    </div>
  );
};

export default AuthPage;