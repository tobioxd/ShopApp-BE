package com.shopapp.services.interfaces;

import com.shopapp.dtos.CategoryDTO;
import com.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category);

    Category getCategoryById(long id);

    List<Category> getAllCategories();

    Category updateCategory(long categoryId, CategoryDTO category);
    
    void deleteCategory(long id);
}
