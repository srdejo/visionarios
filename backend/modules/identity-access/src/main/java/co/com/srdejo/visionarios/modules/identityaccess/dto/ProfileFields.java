package co.com.srdejo.visionarios.modules.identityaccess.dto;

import co.com.srdejo.visionarios.modules.identityaccess.BusinessCategory;

/**
 * Campos específicos por perfil (Profesional/Emprendedor/Empresario), compartidos entre
 * el registro y la actualización de perfil para poder aplicarlos con la misma lógica.
 */
public interface ProfileFields {
    String profession();
    Integer yearsExperience();
    String currentCompany();
    String businessProduct();
    String operatingTime();
    String companyName();
    Integer employeeCount();
    Integer yearsWithCompany();
    BusinessCategory businessCategory();
}
