package com.flavorwocky.repository;

import com.flavorwocky.domain.LatestPairing;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface LatestPairingRepository extends GraphRepository<LatestPairing> {
}
