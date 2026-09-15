package co.com.srdejo.visionarios.modules.admin.dto;

import co.com.srdejo.visionarios.modules.identityaccess.dto.UserResponse;

import java.util.List;
import java.util.Map;

public record AdminUsersResponse(List<UserResponse> users, long total, Map<String, Long> countByProfile) {
}
