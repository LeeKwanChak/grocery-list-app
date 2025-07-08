import React, {useState, useEffect} from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utils/api';
import Sidebar from '../components/Sidebar';
import GroceryListDetail from '../components/GroceryListDetail';
import './HomePage.css';


interface HomePageProps {
    onLogout: () => void;
}

interface User {
    id: number;
    username: string;
    email: string;
}
export interface GroceryList{
    id: number;
    name: string;
    user: User;
}

export interface Item{
    id: number;
    name: string;
    quantity: number;
    completed: boolean;
    groceryList: GroceryList
}

interface HomePageProps {
    onLogout: () => void;
}

const HomePage: React.FC<HomePageProps> = ({onLogout}) => {
    const navigate = useNavigate();
    const [groceryLists, setGroceryLists] = useState<GroceryList[]>([]);
    const [selectedListId, setSelectedListId] = useState<number | null>(null);
    const [error, setError] = useState<string|null>(null);
    const [loadingLists, setLoadingLists] = useState<boolean>(true);

    async function fetchGroceryLists() {
        setError(null);
        try{
            const response = await api.get<GroceryList[]>('lists');
            setGroceryLists(response.data);
            if(response.data.length > 0 && selectedListId ==null){
                setSelectedListId(response.data[0].id);
            }
        } catch(err){
            console.error('Fail to fetch grocery lists: ', err);
            setError('Failed to load grocery lists.');
        }finally{
            setLoadingLists(false)
        }
    }
    
    useEffect(function(){
        fetchGroceryLists();
    }, []);


    const handleInternalLogout = () =>{
        onLogout();
        navigate('/auth');

    }
    

    function handleListCreated(newList: GroceryList) {
        setGroceryLists(function(prevLists) {
            return [...prevLists, newList];
        });
        setSelectedListId(newList.id);
    }

    function handleListDeleted(deletedListId: number) {
        setGroceryLists(function(prevLists) {
            const updatedLists = prevLists.filter(function(list) {
                return list.id !== deletedListId;
        });

        if(selectedListId === deletedListId){
            if(updatedLists.length > 0){
                setSelectedListId(updatedLists[0].id)
            } else{
                setSelectedListId(null)
            }
        }
        return updatedLists;
    })
    };

    return(
        <div className='home-page'>

            <div className= 'main-content-area'>
                <Sidebar
                groceryLists={groceryLists}
                selectedListId={selectedListId}
                onSelectGroceryList={setSelectedListId}
                onListCreated={handleListCreated}
                onListDeleted={handleListDeleted}
                loading={loadingLists}
                error={error}
                />

                <GroceryListDetail 
                selectedListId = {selectedListId}
                selectedList = {
                    groceryLists.find(function(list){
                        return list.id === selectedListId;
                    }) ||null
                }
                />
            </div>
            <button onClick = {handleInternalLogout}>Logout</button>
        </div>
    )
}

export default HomePage;