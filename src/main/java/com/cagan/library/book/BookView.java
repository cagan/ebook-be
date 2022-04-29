package com.cagan.library.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookView {
    private Long id;
    private String title;
    private String author; // TODO: put the Author model here
    private Integer height;
    private String publisher;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
