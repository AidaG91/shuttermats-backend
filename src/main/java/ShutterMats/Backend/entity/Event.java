package ShutterMats.Backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_events_date", columnList = "date")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_events_name_date", columnNames = {"name", "date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String location;

    @Column(name = "image_url", nullable = true, length = 500)
    private String imageUrl;

    @Column(nullable = true, length = 1000)
    private String description;

    // Official page where athletes register for the competition itself
    // (Smoothcomp/Polaris/IBJJF/...). Distinct from
    // CoverageRequest#smoothcompProfileLink, which is a specific athlete's
    // own profile link, not the event's registration page.
    @Column(name = "registration_url", nullable = true, length = 500)
    private String registrationUrl;

    // Snapshot of the price that applied when this event was created/last
    // re-priced - copied from a PricingPlan at that moment (see
    // EventServiceImpl). Kept even if that plan is later edited or deleted,
    // so past events always show what was actually charged. Nullable only
    // to tolerate rows created before pricing plans existed; EventMapper
    // falls back to the default plan's current price for those.
    @Column(name = "base_price", nullable = true, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "extra_match_price", nullable = true, precision = 10, scale = 2)
    private BigDecimal extraMatchPrice;

    // Which plan the prices above were copied from, kept only so the admin
    // UI can show a friendly label (e.g. "Polaris"). Not the source of
    // truth for this event's price - basePrice/extraMatchPrice are. Set to
    // null automatically if that plan is later deleted.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_plan_id", nullable = true)
    private PricingPlan pricingPlan;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}