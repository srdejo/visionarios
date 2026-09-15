package co.com.srdejo.visionarios.modules.materials;

import co.com.srdejo.visionarios.modules.materials.dto.MaterialRequest;
import co.com.srdejo.visionarios.modules.materials.dto.MaterialResponse;
import co.com.srdejo.visionarios.platform.webcommon.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/materials")
public class AdminMaterialController {

    private final MaterialService materialService;

    public AdminMaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping
    public ApiResponse<MaterialResponse> create(@Valid @RequestBody MaterialRequest request) {
        return ApiResponse.ok(materialService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<MaterialResponse> update(@PathVariable UUID id, @Valid @RequestBody MaterialRequest request) {
        return ApiResponse.ok(materialService.update(id, request));
    }
}
