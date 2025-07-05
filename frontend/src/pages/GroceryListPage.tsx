import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const GroceryListPage: React.FC = () => {
    const navigate = useNavigate();
    const getToken = () => localStorage.getItem('token'); 
    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/auth');
    };
    return (
        <div>
            <p>GroceryListPage</p>
            <button onClick={handleLogout}>Logout</button>
        </div>
    )
}

export default GroceryListPage;