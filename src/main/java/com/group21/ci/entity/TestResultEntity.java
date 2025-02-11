package com.group21.ci.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_results")
public class TestResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commit_sha", nullable = false, unique = true)
    private String commitSha;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TestStatus status;  // SUCCESS / FAILED

    @Column(name = "test_log", columnDefinition = "TEXT", nullable = false)
    private String testLog;  // Stores the output of "mvn test"

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Constructors
    public TestResultEntity() {}

    public TestResultEntity(String commitSha, TestStatus status, String testLog, LocalDateTime timestamp) {
        this.commitSha = commitSha;
        this.status = status;
        this.testLog = testLog;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getCommitSha() { return commitSha; }
    public void setCommitSha(String commitSha) { this.commitSha = commitSha; }

    public TestStatus getStatus() { return status; }
    public void setStatus(TestStatus status) { this.status = status; }

    public String getTestLog() { return testLog; }
    public void setTestLog(String testLog) { this.testLog = testLog; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}