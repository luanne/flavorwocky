package com.flavorwocky.repository;

import com.flavorwocky.domain.Pairing;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Luanne Misquitta
 */
@Repository
public interface PairingRepository extends GraphRepository<Pairing> {

}
