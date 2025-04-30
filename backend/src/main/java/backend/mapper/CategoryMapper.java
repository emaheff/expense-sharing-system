package backend.mapper;

import backend.logic.Category;
import shared.CategoryDto;

public class CategoryMapper {
    public static Category toCategory(CategoryDto dto) {
        return new Category(dto.getName());
    }
}
