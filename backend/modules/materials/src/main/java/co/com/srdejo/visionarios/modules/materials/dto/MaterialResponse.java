package co.com.srdejo.visionarios.modules.materials.dto;

import co.com.srdejo.visionarios.modules.identityaccess.Profile;
import co.com.srdejo.visionarios.modules.materials.Material;
import co.com.srdejo.visionarios.modules.materials.MaterialType;

import java.time.Instant;
import java.util.UUID;

public record MaterialResponse(
        UUID id,
        String title,
        MaterialType type,
        String meta,
        String driveUrl,
        Profile visibleProfile,
        Instant createdAt
) {
    public static MaterialResponse from(Material material) {
        return new MaterialResponse(material.getId(), material.getTitle(), material.getType(), material.getMeta(),
                material.getDriveUrl(), material.getVisibleProfile(), material.getCreatedAt());
    }
}
