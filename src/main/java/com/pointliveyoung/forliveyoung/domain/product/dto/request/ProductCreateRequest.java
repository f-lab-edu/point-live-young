package com.pointliveyoung.forliveyoung.domain.product.dto.request;

import com.pointliveyoung.forliveyoung.domain.product.entity.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 100, message = "상품명은 100자를 초과할 수 없습니다.")
    private String name;

    private String description;

    @NotNull(message = "재고 수량은 필수입니다.")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    private Integer stock;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "카테고리는 필수입니다.")
    private Category category;
}
