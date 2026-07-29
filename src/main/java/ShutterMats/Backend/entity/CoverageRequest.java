package ShutterMats.Backend.entity;

import ShutterMats.Backend.entity.enums.BeltCategory;
import ShutterMats.Backend.entity.enums.CompetitionModality;
import ShutterMats.Backend.entity.enums.Division;
import ShutterMats.Backend.entity.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "coverage_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoverageRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String athleteName;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String athleteEmail;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String athletePhone;

    private String athleteInstagram;

    private String athleteGym;

    private String athleteCity;

    private String athleteCountry;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private String organizer;

    @Column(length = 500)
    private String smoothcompLink;

    private String weight;

    @Enumerated(EnumType.STRING)
    private BeltCategory belt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Division division;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompetitionModality modality;

    private String smoothcompDisplayName;

    @Column(length = 500)
    private String smoothcompProfileLink;

    private String estimatedFirstFightTime;

    @ManyToMany
    @JoinTable(
            name = "coverage_request_extras",
            joinColumns = @JoinColumn(name = "coverage_request_id"),
            inverseJoinColumns = @JoinColumn(name = "coverage_extra_id")
    )
    private Set<CoverageExtra> extras = new HashSet<>();

    @Column(length = 1000)
    private String photoPreferences;

    @Column(length = 1000)
    private String specialMoments;

    @Column(length = 1000)
    private String additionalNotes;

    private Boolean needsInvoice;

    private String invoiceName;

    private String invoiceTaxId;

    private String invoiceAddress;

    private String invoiceCountry;

    @NotNull
    @AssertTrue
    @Column(nullable = false)
    private Boolean termsAccepted;

    private Boolean portfolioConsent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(length = 1000)
    private String adminResponse;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
