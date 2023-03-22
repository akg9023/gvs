package com.voice.registration.dao;

import com.voice.registration.model.DevoteeInfoResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DevoteeInfoDao extends MongoRepository<DevoteeInfoResponse,String> {
}
