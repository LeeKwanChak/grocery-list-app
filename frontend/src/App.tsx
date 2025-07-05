import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import AuthPage from './pages/AuthPage';
import GroceryListPage from './pages/GroceryListPage';
import './App.css'

const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const isAuthenticated = localStorage.getItem('token');
  return isAuthenticated ? <>{children}</> : <Navigate to="/auth" replace />;
};

function App() {

  return (
    <Router>
      <div className = "App">
        <header>
          <h1>My Grocery List App</h1>
        </header>
        <Routes>
          <Route path ="/auth" element = {<AuthPage />}/>

          <Route
            path="/grocery-list"
            element={
              <PrivateRoute>
                <GroceryListPage />
              </PrivateRoute>
            }
          />

          <Route
            path="/"
            element={<Navigate to={localStorage.getItem('token') ? "/grocery-list" : "/auth"} replace />}
          />
        </Routes>
        <footer>
          <p>&copy; 2025 My Grocery List App</p>
        </footer>
      </div>
    </Router>
  )
}

export default App
