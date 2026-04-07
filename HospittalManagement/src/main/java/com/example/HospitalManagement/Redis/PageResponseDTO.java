package com.example.HospitalManagement.Redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

// we Create This DTO for Redis jab JSON value send krega tab Spring usko Pageable Format me Response kr paaye kyu ki Jackson usko Jackson me convert nahi kr pa raha .
public class PageResponseDTO<T> {

    private List<T> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElement;
    private Integer totalPages;
}
//---> "Spring Data PageImpl is not directly serializable/deserializable via Jackson, so we use a custom DTO wrapper for caching paginated data in Redis."