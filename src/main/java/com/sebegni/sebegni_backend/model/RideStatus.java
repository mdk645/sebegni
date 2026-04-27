package com.sebegni.sebegni_backend.model;

public enum RideStatus {
    PENDING,      // Course créée, en attente de chauffeur
    ASSIGNED,     // Chauffeur a accepté
    IN_PROGRESS,  // Trajet en cours
    COMPLETED,    // Trajet terminé
    CANCELLED     // Trajet annulé
}
