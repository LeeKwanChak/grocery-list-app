import React, { useState, useEffect } from 'react';
import type { FormEvent, ChangeEvent } from 'react';
import api from '../utils/api';
import type { GroceryList, Item } from '../pages/HomePage';
import './GroceryListDetail.css';

interface ItemCreateRequestBody {
  name: string;
  quantity: number;
  completed?: boolean;
  groceryListId: number;
}

interface ItemUpdateRequestBody {
  name?: string;
  quantity?: number;
  completed?: boolean;
}

interface GroceryListDetailProps {
  selectedListId: number | null;
  selectedList: GroceryList | null;
}

const GroceryListDetail: React.FC<GroceryListDetailProps> = ({ selectedListId, selectedList }) => {
  // const [items, setItems] = useState<Item[]>([]);
  const [completedItem, setCompletedItem] = useState<Item[]>([])
  const [uncompletedItem, setUncompletedItem] = useState<Item[]>([])
  const [newItemName, setNewItemName] = useState<string>('');
  const [newItemQuantity, setNewItemQuantity] = useState<number>(1);
  const [loadingItems, setLoadingItems] = useState<boolean>(false);
  const [itemError, setItemError] = useState<string | null>(null);
  const [isAddingItem, setIsAddingItem] = useState<boolean>(false);

  async function fetchItems(){
    setLoadingItems(true)
    setItemError(null)

    try{
      const response = await api.get<Item[]>(`items/list/${selectedListId}`)
      const fetchCompleted = response.data.filter(item => item.completed)
      const fetchUncompleted = response.data.filter(item => !item.completed)
      setCompletedItem(fetchCompleted) 
      setUncompletedItem(fetchUncompleted)
    }catch{
      console.error('Fail to fetch items')
      setItemError('Fail to fetch items')
      setCompletedItem([]) 
      setUncompletedItem([])
    }finally{
      setLoadingItems(false)
    }
  }

  useEffect(function() {
    if(selectedListId){
        fetchItems();
    }
  }, [selectedListId]);


  async function handleAddItem(e: FormEvent){
    e.preventDefault();
    if (!selectedListId) {
      setItemError('Please select a grocery list first.');
      return;
    }
    if (!newItemName.trim()) {
      setItemError('Item name cannot be empty.');
      return;
    }

    setIsAddingItem(true);
    setItemError(null);

    const newItemData: ItemCreateRequestBody = {
      name: newItemName,
      quantity: newItemQuantity,
      completed: false,
      groceryListId: selectedListId,
    };

    try {
      const response = await api.post<Item>('/items', newItemData);
      setUncompletedItem((prevItems) => [...prevItems, response.data]);
      setNewItemName('');
      setNewItemQuantity(1);
    } catch (err: any) {
      console.error('Failed to add item:', err);
      setItemError(err.response?.data?.message || 'Failed to add item.');
    } finally {
      setIsAddingItem(false);
    }
  };

  
  async function handleDeleteItem(itemId: number){
      try {
        await api.delete(`/items/${itemId}`);
        setCompletedItem((prevItems) => prevItems.filter((item) => item.id !== itemId))
        setUncompletedItem((prevItems) => prevItems.filter((item) => item.id !== itemId))
      } catch (err: any) {
        console.error('Failed to delete item:', err);
        setItemError(err.response?.data?.message || 'Failed to delete item.');
      }
  };


  async function handleUpdateItem(item: Item, updates: Partial<ItemUpdateRequestBody>){
    const updatedData: ItemUpdateRequestBody = {
      name: updates.name !== undefined ? updates.name : item.name,
      quantity: updates.quantity !== undefined ? updates.quantity : item.quantity,
      completed: updates.completed !== undefined ? updates.completed : item.completed,
    };

    try {
      const response = await api.put<Item>(`/items/${item.id}`, updatedData);
      const updatedItem = response.data;

      if (updatedItem.completed) {
        setUncompletedItem((prevItems) => prevItems.filter((prevItem) => prevItem.id !== updatedItem.id));
        setCompletedItem((prevItems) => {
          const existing = prevItems.find(prev => prev.id === updatedItem.id);
          if (existing) {
              return prevItems.map(prevItem => prevItem.id === updatedItem.id ? updatedItem : prevItem);
          } else {
              return [...prevItems, updatedItem];
          }
        });
      } else {
        setCompletedItem((prevItems) => prevItems.filter((prevItem) => prevItem.id !== updatedItem.id));
        setUncompletedItem((prevItems) => {
            const existing = prevItems.find(prev => prev.id === updatedItem.id);
            if (existing) {
                return prevItems.map(prevItem => prevItem.id === updatedItem.id ? updatedItem : prevItem);
            } else {
                return [...prevItems, updatedItem];
            }
        });
      }
    } catch{
      console.error('Failed to update item:');
      setItemError('Failed to update item.');
    }
  }

  
  async function handleBatchDeleteItem(){
  }

  async function handleBatchCreateItem(){

  }

  if (!selectedListId || !selectedList){
    return (
        <p>Please select or create a grocery list from the sidebar</p>
    );
  }

  return (
    <div className="grocery-list-detail-container">
      <h2>{selectedList.name}</h2>

      <div className="add-item-section">
        <form onSubmit={handleAddItem}>
          <input
            type="text"
            placeholder="New item name"
            value={newItemName}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setNewItemName(e.target.value)}
            required
            disabled={isAddingItem}
          />
          <input
            type="number"
            min="1"
            placeholder="Quantity"
            value={newItemQuantity}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setNewItemQuantity(parseInt(e.target.value) || 1)}
            required
            disabled={isAddingItem}
          />
          <button type="submit" disabled={isAddingItem}>
            {isAddingItem ? 'Adding...' : 'Add Item'}
          </button>
        </form>
        {itemError && <p className="error-message">{itemError}</p>}
      </div>

      <div className="item-list-container">
        {loadingItems ? (
          <p>Loading items...</p>
        ) : uncompletedItem.length === 0 && completedItem.length === 0 ? (
          <p>No items in this list yet!</p>
        ) : (
          <div className="item-list">
            <h3>Item to buy</h3>
            {uncompletedItem.map((item) => (
              <div className="item-row">
                    <input
                      type="checkbox"
                      checked={item.completed}
                      onChange={() => handleUpdateItem(item, { completed: !item.completed })}
                    />
                  <span className={`item-name ${item.completed ? 'completed' :''}`}>
                    {item.name}
                  </span>
                    <input
                      type="number"
                      min="1"
                      value={item.quantity}
                      onChange={(e: ChangeEvent<HTMLInputElement>) => handleUpdateItem(item, { quantity: parseInt(e.target.value) || 1 })
                      }
                      className="quantity-input"
                    />
                  <button
                    className="delete-item-button"
                    onClick={() => handleDeleteItem(item.id)}
                  >
                    &times;
                  </button>
              </div>
            ))}
          </div>
        )}
      </div>

      {completedItem.length > 0 && (
        <div className="purchased-item-list-container">
          <h3>Purchased Items</h3>
          <div className="item-list">
            {completedItem.map((item) => (
              <div className="item-row">
                  <input
                    type="checkbox"
                    checked={item.completed}
                    onChange={() => handleUpdateItem(item, { completed: !item.completed })}
                  />
                  <span className={`item-name ${item.completed ? 'completed' : ''}`}>
                    {item.name}
                  </span>
                    {/* <input
                      type="number"
                      min="1"
                      value={item.quantity}
                      onChange={(e: ChangeEvent<HTMLInputElement>) => handleUpdateItem(item, {quantity: parseInt(e.target.value) || 1})
                      }
                      className="quantity-input"
                    /> */}
                 <button
                   className="delete-item-button"
                   onClick={() => handleDeleteItem(item.id)}
                 >
                   &times;
                 </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default GroceryListDetail;