package co.com.srdejo.visionarios.modules.identityaccess;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> withFilters(String search, Profile profile, BusinessCategory businessCategory,
                                                    Integer minAge, Integer maxAge,
                                                    Integer minEmployees, Integer maxEmployees) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (search != null && !search.isBlank()) {
                String term = "%" + search.trim().toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.get("fullName")), term),
                        cb.like(cb.lower(root.get("email")), term)));
            }
            if (profile != null) {
                predicates = cb.and(predicates, cb.equal(root.get("profile"), profile));
            }
            if (businessCategory != null) {
                predicates = cb.and(predicates, cb.equal(root.get("businessCategory"), businessCategory));
            }
            LocalDate today = LocalDate.now();
            if (minAge != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("birthDate"), today.minusYears(minAge)));
            }
            if (maxAge != null) {
                predicates = cb.and(predicates, cb.greaterThan(root.get("birthDate"), today.minusYears(maxAge + 1)));
            }
            if (minEmployees != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("employeeCount"), minEmployees));
            }
            if (maxEmployees != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("employeeCount"), maxEmployees));
            }
            return predicates;
        };
    }
}
