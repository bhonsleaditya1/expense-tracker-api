package com.springboot.expensetracker.services;

import java.util.List;

import com.springboot.expensetracker.domain.Category;
import com.springboot.expensetracker.exceptions.EtBadRequestException;
import com.springboot.expensetracker.exceptions.EtResourceNotFoundException;
import com.springboot.expensetracker.repositories.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<Category> fetchAllCategories(Integer userId) {
        return categoryRepository.findAll(userId);
    }

    @Override
    public Category fetchCategoryById(Integer userId, Integer categoryId) throws EtResourceNotFoundException {
        return categoryRepository.findById(userId, categoryId);
    }

    @Override
    public Category addCategory(Integer userId, String title, String description) throws EtBadRequestException {
        Integer categoryId = categoryRepository.create(userId, title, description);
        return categoryRepository.findById(userId, categoryId);
    }

    @Override
    public void updateCategory(Integer userId, Integer categoryId, Category category) throws EtBadRequestException {
        categoryRepository.update(userId, categoryId, category);   
    }

    @Override
    public void removeCategoryWithAllTransactions(Integer userId, Integer categoryId)
            throws EtResourceNotFoundException {
        this.fetchAllCategories(userId);
        categoryRepository.removeById(userId, categoryId);
    }
    
}
