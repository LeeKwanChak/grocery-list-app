import React, { useState, useEffect } from 'react';
import type { FormEvent, ChangeEvent } from 'react';
import api from '../utils/api';
import type { GroceryList, Item } from '../pages/HomePage';

interface GroceryListDetailProps {
  selectedListId: number | null;
  selectedList: GroceryList | null;
}

const GroceryListDetail: React.FC<GroceryListDetailProps> = () => {


  return (
  <div className="grocery-list-detail-container">
    <p>Items info</p>
  </div>
  );
};

export default GroceryListDetail;