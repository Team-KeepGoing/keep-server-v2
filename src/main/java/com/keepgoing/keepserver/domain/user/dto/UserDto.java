package com.keepgoing.keepserver.domain.user.dto;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String email,
        String studentId,
        String name,
        boolean teacher
) {
}
