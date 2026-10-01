package com.fashionsense.catalog.category;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @GetMapping("/{slug}")
    public CategoryResponse getCategoryBySlug(
            @PathVariable String slug
    ) {
        return CategoryResponse.from(
                categoryService.getCategoryBySlug(slug)
        );
    }

    @GetMapping("/roots")
    public List<CategoryResponse> getRootCategories() {
        return categoryService.getRootCategories()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @GetMapping("/{parentId}/children")
    public List<CategoryResponse> getChildCategories(
            @PathVariable Long parentId
    ) {
        return categoryService.getChildCategories(parentId)
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {

        Category category = categoryService.createCategory(
                request.name(),
                request.slug(),
                request.parentId()
        );

        return CategoryResponse.from(category);
    }
}