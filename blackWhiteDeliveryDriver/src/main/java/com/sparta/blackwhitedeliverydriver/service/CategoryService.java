package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.entity.Category;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.repository.CategoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;


    public UUID getOrCreateCategory(String categoryName, User user) {
        // 카테고리가 이미 존재하는지 확인
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    // 없으면 새로 생성하고 저장
                    Category newCategory = Category.from(categoryName);
                    return categoryRepository.save(newCategory);
                }).getCategoryId();
    }
}
