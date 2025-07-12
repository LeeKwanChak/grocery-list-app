import { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import AuthPage from './pages/AuthPage';
import HomePage from './pages/HomePage';
import './App.css'
import { getToken, removeToken } from './utils/auth';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

  useEffect(() =>{
    if(getToken()) {
      setIsAuthenticated(true)
    }
  }, [])

  const handleLoginSuccess = () =>{
    setIsAuthenticated(true)
  }

  const handleLogout = () =>{
    removeToken();
    setIsAuthenticated(false)
  }

  return (
    <Router>
      <div className = "App">
        {/* <header>
          <h1>My Grocery List App</h1>
        </header> */}
        <Routes>
          <Route path ="/auth" element = {
            isAuthenticated ? <Navigate to = "/" replace/> : <AuthPage onLoginSuccess ={handleLoginSuccess}/>}/>

          <Route path = "/" element ={
            isAuthenticated ? <HomePage onLogout={handleLogout} /> : <Navigate to = "/auth" replace/>
          }
          />
        </Routes>
      </div>
    </Router>
  )
}

export default App
