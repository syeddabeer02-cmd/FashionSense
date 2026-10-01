package com.fashionsense.catalog.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with slug: " + slug
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Transactional(readOnly = true)
    public List<Category> getChildCategories(Long parentId) {
        return categoryRepository.findChildrenWithParent(parentId);
    }

    @Transactional
    public Category createCategory(
            String name,
            String slug,
            Long parentId
    ) {

        if (categoryRepository.existsBySlug(slug)) {
            throw new CategoryAlreadyExistsException(
                    "Category already exists with slug: " + slug
            );
        }

        Category category = new Category();

        category.setName(name);
        category.setSlug(slug);

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() ->
                            new CategoryNotFoundException(
                                    "Parent category not found with id: " + parentId
                            )
                    );

            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }
}