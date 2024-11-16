package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.entity.Category;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;


    @Transactional
    public List<Category> getOrCreateCategory(String categoryNames, User user) {
        Set<String> categorySet = new HashSet<>();
        Arrays.stream(categoryNames.split(","))
                .map(String::trim)
                .forEach(categorySet::add);

        List<Category> categoryList = new ArrayList<>();
        for(String categoryName : categorySet) {
            categoryRepository.findByName(categoryName)
                    .orElseGet(() -> {
                        // 없으면 새로 생성하고 저장
                        Category category = Category.from(categoryName);
                        categoryList.add(category);
                        return categoryRepository.save(category);
                    });
        }

        return categoryList;
    }
}
