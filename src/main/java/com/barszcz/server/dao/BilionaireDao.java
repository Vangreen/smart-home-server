package com.barszcz.server.dao;

import com.barszcz.server.entity.Bilionare;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BilionaireDao extends CrudRepository<Bilionare, Long> {

}
