import React from 'react';
import { useNavigate } from 'react-router-dom';

interface HomePageProps {
    onLogout: () => void;
}

const HomePage: React.FC<HomePageProps> = ({onLogout}) => {
    const navigate = useNavigate();

    const handleInternalLogout = () =>{
        onLogout();
        navigate('/auth');

    }
    return(
        <div>
            <button onClick = {handleInternalLogout}>Logout</button>
        </div>
    )
}

export default HomePage;