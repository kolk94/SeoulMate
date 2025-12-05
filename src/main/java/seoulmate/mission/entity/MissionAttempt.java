package seoulmate.mission.entity;

import jakarta.persistence.*;
import lombok.*;
import seoulmate.user.entity.User;
import java.time.Instant;

@Entity
@Table(name = "mission_attempt")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MissionAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private boolean success;

    @Column(nullable = false)
    private Instant attemptedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.attemptedAt = now;
        this.success = false;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void verifySuccess(String label, Double score) {
        this.success = true;
        this.attemptedAt = Instant.now();
    }

    public void verifyFail(String label, Double score) {
        this.success = false;
        this.attemptedAt = Instant.now();
    }

    public void updateLocation(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
        this.attemptedAt = Instant.now();
    }
}
