import React , {useState} from 'react';
import LoginForm from '../components/LoginForm';
import RegisterForm from '../components/RegisterForm';
import './AuthPage.css'

const AuthPage: React.FC = () => {
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
        <LoginForm/>
      ) : (
        <RegisterForm/>
      )}
    </div>
  );
};

export default AuthPage;