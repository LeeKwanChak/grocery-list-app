// src/components/Sidebar.tsx
import React, { useState } from 'react';
import type { FormEvent, ChangeEvent } from 'react';
import api from '../utils/api';
import type { GroceryList } from '../pages/HomePage';
import './Sidebar.css';

interface SidebarProps {
  groceryLists: GroceryList[];
  selectedListId: number | null;
  onSelectGroceryList: (listId: number) => void;
  onListCreated: (newList: GroceryList) => void;
  onListDeleted: (deletedListId: number) => void;
  loading: boolean;
  error: string | null;
  onLogout: () => void //onLogout: function(): void
}

const Sidebar: React.FC<SidebarProps> = ({
  groceryLists,
  selectedListId,
  onSelectGroceryList,
  onListCreated,
  onListDeleted,
  loading,
  error,
  onLogout
  }) => {
  const [newListName, setNewListName] = useState<string>('');
  const [isCreatingList, setIsCreatingList] = useState<boolean>(false);
  const [createListError, setCreateListError] = useState<string | null>(null);

  async function handleCreateList(e:FormEvent) {
    e.preventDefault();
    if (!newListName.trim()){
      setCreateListError('List name cannot be empty.');
      return;
    }
    setIsCreatingList(true);
    setCreateListError(null);

    try{
      const response = await api.post<GroceryList>('/lists', { name: newListName });
      onListCreated(response.data);
      setNewListName(''); 
    }catch(err: any) {
      console.error('Failed to create list:', err);
      setCreateListError('Failed to create list.');
    }finally{
      setIsCreatingList(false);
    }
  };

  async function handleDeleteList(listId: number, listName: string) {
    if (window.confirm(`Are you confirm to delete "${listName}"?`)) {
      try{
        await api.delete(`/lists/${listId}`);
        onListDeleted(listId);
      }catch (err) {
        console.error('Failed to delete list:', err);
        setCreateListError('Failed to delete list.');
      }
    }
  };

  return(
    <aside className='sidebar'>
      <div className = 'sidebar-header'>
      <h2>My Lists</h2>
      <button onClick ={onLogout} className= 'Logout-button'>Logout</button>
      </div>
      <div className="create-list-section">
        <form onSubmit={handleCreateList}>
          <input
            type="text"
            placeholder="New list name"
            value={newListName}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setNewListName(e.target.value)}
            disabled={isCreatingList}
          />
          <button type="submit" disabled={isCreatingList}>
            {isCreatingList ? 'Creating...' : 'Create List'}
          </button>
        </form>
        {createListError && <p className="error-message">{createListError}</p>}
      </div>

      <div className="list-container">
        {loading ? (
          <p className="loading-message">Loading lists...</p>
        ) : groceryLists.length === 0 ? (
          <p className="no-lists-message">No lists yet. Create one above!</p>
        ) : (
          <div className="list-items" >
            {groceryLists.map((list) => (
              <div
                key={list.id}
                className={`list-item ${selectedListId === list.id ? 'selected' : ''}`}
                onClick={() => onSelectGroceryList(list.id)}
              >
                <span className="list-name"># {list.name}</span>
                <button
                  className="delete-list-button"
                  onClick={() => handleDeleteList(list.id, list.name)}
                  title={`Delete ${list.name}`}
                >
                  &times;
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </aside>
  );
};

export default Sidebar;